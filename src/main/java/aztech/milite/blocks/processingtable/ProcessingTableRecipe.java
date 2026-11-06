package aztech.milite.blocks.processingtable;

import aztech.milite.MIRegistries;
import com.mojang.datafixers.util.Either;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public record ProcessingTableRecipe(
        Ingredient casing,
        List<String> pattern,
        List<Ingredient> ingredients,
        ItemStack result) implements Recipe<RecipeInput> {
    public static final char EMPTY = ' ';
    public static final char INPUT = 'X';
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

    private static final Codec<Either<List<Ingredient>, Map<Character, Ingredient>>> INPUTS_CODEC =
            Codec.either(Ingredient.CODEC_NONEMPTY.listOf(1, SLOT_COUNT), INGREDIENTS_CODEC);

    private static final MapCodec<ProcessingTableRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("casing").forGetter(ProcessingTableRecipe::casing),
                    PATTERN_CODEC.fieldOf("pattern").forGetter(ProcessingTableRecipe::pattern),
                    INPUTS_CODEC.fieldOf("ingredients").forGetter(recipe -> Either.left(recipe.ingredients)),
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ProcessingTableRecipe::result))
                    .apply(instance, ProcessingTableRecipe::fromEncoded));

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

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Ingredient>> INGREDIENTS_STREAM_CODEC = StreamCodec
            .of(
                    (buffer, ingredients) -> {
                        buffer.writeVarInt(ingredients.size());
                        ingredients.forEach(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient));
                    },
                    buffer -> {
                        List<Ingredient> ingredients = new ArrayList<>();
                        int size = buffer.readVarInt();
                        for (int index = 0; index < size; index++) {
                            ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
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
        int inputCount = 0;
        for (String row : pattern) {
            if (row.length() != width) {
                throw new IllegalArgumentException("Pattern rows must all be the same width: " + pattern);
            }
            for (int column = 0; column < width; column++) {
                char character = row.charAt(column);
                if (character == INPUT) {
                    inputCount++;
                } else if (character != EMPTY) {
                    throw new IllegalArgumentException("Pattern may only use X and spaces: " + pattern);
                }
            }
        }
        if (inputCount == 0 || inputCount != ingredients.size()) {
            throw new IllegalArgumentException("Pattern input count must equal ingredient count: " + pattern);
        }
        pattern = List.copyOf(pattern);
        ingredients = List.copyOf(ingredients);
    }

    private static ProcessingTableRecipe fromEncoded(Ingredient casing, List<String> pattern,
            Either<List<Ingredient>, Map<Character, Ingredient>> encoded, ItemStack result) {
        return encoded.map(ingredients -> new ProcessingTableRecipe(casing, pattern, ingredients, result), legacy -> {
            List<Ingredient> ingredients = new ArrayList<>();
            List<String> normalized = new ArrayList<>();
            for (String row : pattern) {
                StringBuilder line = new StringBuilder();
                for (char symbol : row.toCharArray()) {
                    if (symbol == EMPTY) {
                        line.append(EMPTY);
                    } else {
                        Ingredient ingredient = legacy.get(symbol);
                        if (ingredient == null) {
                            throw new IllegalArgumentException("Missing ingredient for " + symbol);
                        }
                        ingredients.add(ingredient);
                        line.append(INPUT);
                    }
                }
                normalized.add(line.toString());
            }
            return new ProcessingTableRecipe(casing, normalized, ingredients, result);
        });
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

        List<Ingredient> ingredients = new ArrayList<>();
        List<String> pattern = new ArrayList<>();
        for (int row = minRow; row <= maxRow; row++) {
            StringBuilder line = new StringBuilder();
            for (int column = minColumn; column <= maxColumn; column++) {
                Ingredient ingredient = slots.get(row * GRID_SIZE + column + MIN_SLOT);
                if (ingredient == null) {
                    line.append(EMPTY);
                    continue;
                }
                ingredients.add(ingredient);
                line.append(INPUT);
            }
            pattern.add(line.toString());
        }

        return new ProcessingTableRecipe(casing, pattern, ingredients, result);
    }

    public SortedMap<Integer, Ingredient> slots() {
        int width = pattern.get(0).length();
        int offsetX = (GRID_SIZE - width) / 2;
        int offsetY = (GRID_SIZE - pattern.size()) / 2;
        SortedMap<Integer, Ingredient> slots = new TreeMap<>();
        int ingredientIndex = 0;
        for (int row = 0; row < pattern.size(); row++) {
            String line = pattern.get(row);
            for (int column = 0; column < width; column++) {
                if (line.charAt(column) == INPUT) {
                    slots.put((offsetY + row) * GRID_SIZE + offsetX + column + MIN_SLOT, ingredients.get(ingredientIndex++));
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
            ItemStack stack = grid.get(slot - MIN_SLOT);
            if (slots.containsKey(slot) == stack.isEmpty()) {
                return false;
            }
        }
        List<ItemStack> stacks = slots.keySet().stream().map(slot -> grid.get(slot - MIN_SLOT)).toList();
        int[] matchedStacks = new int[ingredients.size()];
        Arrays.fill(matchedStacks, -1);
        for (int index = 0; index < stacks.size(); index++) {
            if (!assignIngredient(stacks, index, matchedStacks, new boolean[ingredients.size()])) {
                return false;
            }
        }
        return true;
    }

    private boolean assignIngredient(List<ItemStack> stacks, int index, int[] matchedStacks, boolean[] visited) {
        for (int ingredient = 0; ingredient < ingredients.size(); ingredient++) {
            if (!visited[ingredient] && ingredients.get(ingredient).test(stacks.get(index))) {
                visited[ingredient] = true;
                if (matchedStacks[ingredient] == -1 || assignIngredient(stacks, matchedStacks[ingredient], matchedStacks, visited)) {
                    matchedStacks[ingredient] = index;
                    return true;
                }
            }
        }
        return false;
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
