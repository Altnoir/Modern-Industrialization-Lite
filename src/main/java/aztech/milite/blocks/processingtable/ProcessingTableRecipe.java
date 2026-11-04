package aztech.milite.blocks.processingtable;

import aztech.milite.MIRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public record ProcessingTableRecipe(
        Ingredient casing,
        List<String> pattern,
        Map<Character, Ingredient> ingredients,
        ItemStack result) implements Recipe<RecipeInput> {
    public static final char EMPTY = ' ';
    public static final int GRID_SIZE = 3;
    public static final int SLOT_COUNT = GRID_SIZE * GRID_SIZE;
    public static final int MIN_SLOT = 1, MAX_SLOT = SLOT_COUNT;

    private static final Codec<Character> INGREDIENT_KEY_CODEC = Codec.STRING.comapFlatMap(
            key -> key.length() == 1 && key.charAt(0) != EMPTY
                    ? DataResult.success(key.charAt(0))
                    : DataResult.error(() -> "Recipe keys must be a single character that is not a space: " + key),
            key -> String.valueOf((char) key));

    private static final Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf(1, GRID_SIZE)
            .validate(ProcessingTableRecipe::validatePattern);

    private static final Codec<Map<Character, Ingredient>> INGREDIENTS_CODEC = Codec
            .unboundedMap(INGREDIENT_KEY_CODEC, Ingredient.CODEC_NONEMPTY);

    private static final MapCodec<ProcessingTableRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("casing").forGetter(ProcessingTableRecipe::casing),
                    PATTERN_CODEC.fieldOf("pattern").forGetter(ProcessingTableRecipe::pattern),
                    INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(ProcessingTableRecipe::ingredients),
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ProcessingTableRecipe::result))
                    .apply(instance, ProcessingTableRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, List<String>> PATTERN_STREAM_CODEC = StreamCodec.of(
            (buffer, pattern) -> {
                buffer.writeVarInt(pattern.size());
                pattern.forEach(buffer::writeUtf);
            },
            buffer -> {
                List<String> pattern = new ArrayList<>();
                int size = buffer.readVarInt();
                for (int index = 0; index < size; index++) {
                    pattern.add(buffer.readUtf());
                }
                return pattern;
            });

    private static final StreamCodec<RegistryFriendlyByteBuf, Map<Character, Ingredient>> INGREDIENTS_STREAM_CODEC = StreamCodec
            .of(
                    (buffer, ingredients) -> {
                        buffer.writeVarInt(ingredients.size());
                        ingredients.forEach((character, ingredient) -> {
                            buffer.writeChar(character);
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
                        });
                    },
                    buffer -> {
                        Map<Character, Ingredient> ingredients = new LinkedHashMap<>();
                        int size = buffer.readVarInt();
                        for (int index = 0; index < size; index++) {
                            ingredients.put(buffer.readChar(), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                        }
                        return ingredients;
                    });

    private static final StreamCodec<RegistryFriendlyByteBuf, ProcessingTableRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            ProcessingTableRecipe::casing,
            PATTERN_STREAM_CODEC,
            ProcessingTableRecipe::pattern,
            INGREDIENTS_STREAM_CODEC,
            ProcessingTableRecipe::ingredients,
            ItemStack.STREAM_CODEC,
            ProcessingTableRecipe::result,
            ProcessingTableRecipe::new);

    public ProcessingTableRecipe {
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("A pattern needs at least one row");
        }
        int width = pattern.get(0).length();
        if (width == 0) {
            throw new IllegalArgumentException("A pattern row needs at least one character");
        }
        for (String row : pattern) {
            if (row.length() != width) {
                throw new IllegalArgumentException("Pattern rows must all be the same width: " + pattern);
            }
            for (int column = 0; column < width; column++) {
                char character = row.charAt(column);
                if (character != EMPTY && !ingredients.containsKey(character)) {
                    throw new IllegalArgumentException("Pattern uses '" + character + "' but defines no ingredient for it");
                }
            }
        }
        if (ingredients.containsKey(EMPTY)) {
            throw new IllegalArgumentException("'" + EMPTY + "' cannot be used as an ingredient key");
        }
        pattern = List.copyOf(pattern);
        ingredients = Map.copyOf(ingredients);
    }

    private static DataResult<List<String>> validatePattern(List<String> pattern) {
        int width = pattern.get(0).length();
        if (width == 0) {
            return DataResult.error(() -> "A pattern row needs at least one character: " + pattern);
        }
        if (width > GRID_SIZE) {
            return DataResult.error(() -> "A pattern row can hold at most " + GRID_SIZE + " characters: " + pattern);
        }
        for (String row : pattern) {
            if (row.length() != width) {
                return DataResult.error(() -> "Pattern rows must all be the same width: " + pattern);
            }
        }
        return DataResult.success(pattern);
    }

    public static ProcessingTableRecipe fromSlots(Ingredient casing, SortedMap<Integer, Ingredient> slots, ItemStack result) {
        int minRow = GRID_SIZE, maxRow = -1, minColumn = GRID_SIZE, maxColumn = -1;
        for (int slot : slots.keySet()) {
            int row = (slot - MIN_SLOT) / GRID_SIZE;
            int column = (slot - MIN_SLOT) % GRID_SIZE;
            minRow = Math.min(minRow, row);
            maxRow = Math.max(maxRow, row);
            minColumn = Math.min(minColumn, column);
            maxColumn = Math.max(maxColumn, column);
        }
        if (maxRow < 0) {
            throw new IllegalArgumentException("A recipe needs at least one ingredient");
        }

        Map<Ingredient, Character> characters = new LinkedHashMap<>();
        List<String> pattern = new ArrayList<>();
        for (int row = minRow; row <= maxRow; row++) {
            StringBuilder line = new StringBuilder();
            for (int column = minColumn; column <= maxColumn; column++) {
                Ingredient ingredient = slots.get(row * GRID_SIZE + column + MIN_SLOT);
                if (ingredient == null) {
                    line.append(EMPTY);
                    continue;
                }
                characters.computeIfAbsent(ingredient, ignored -> (char) ('a' + characters.size()));
                line.append(characters.get(ingredient).charValue());
            }
            pattern.add(line.toString());
        }

        Map<Character, Ingredient> ingredients = new LinkedHashMap<>();
        characters.forEach((ingredient, character) -> ingredients.put(character, ingredient));
        return new ProcessingTableRecipe(casing, pattern, ingredients, result);
    }

    public SortedMap<Integer, Ingredient> slots() {
        int width = pattern.get(0).length();
        int offsetX = (GRID_SIZE - width) / 2;
        int offsetY = (GRID_SIZE - pattern.size()) / 2;
        SortedMap<Integer, Ingredient> slots = new TreeMap<>();
        for (int row = 0; row < pattern.size(); row++) {
            String line = pattern.get(row);
            for (int column = 0; column < width; column++) {
                Ingredient ingredient = ingredients.get(line.charAt(column));
                if (ingredient != null) {
                    slots.put((offsetY + row) * GRID_SIZE + offsetX + column + MIN_SLOT, ingredient);
                }
            }
        }
        return slots;
    }

    public boolean matchesCasing(ItemStack casingStack) {
        return casing.test(casingStack);
    }

    public boolean matches(ItemStack casingStack, List<ItemStack> grid) {
        if (!casing.test(casingStack) || grid.size() != SLOT_COUNT) {
            return false;
        }
        SortedMap<Integer, Ingredient> slots = slots();
        for (int slot = MIN_SLOT; slot <= MAX_SLOT; slot++) {
            Ingredient required = slots.get(slot);
            ItemStack stack = grid.get(slot - MIN_SLOT);
            if (required == null ? !stack.isEmpty() : !required.test(stack)) {
                return false;
            }
        }
        return true;
    }

    public boolean usesSlot(int slot) {
        return slots().containsKey(slot);
    }

    public Ingredient ingredientAt(int slot) {
        return slots().get(slot);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider registryAccess) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(casing);
        ingredients.addAll(slots().values());
        return ingredients;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MIRegistries.PROCESSING_TABLE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MIRegistries.PROCESSING_TABLE_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ProcessingTableRecipe> {
        @Override
        public MapCodec<ProcessingTableRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ProcessingTableRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
