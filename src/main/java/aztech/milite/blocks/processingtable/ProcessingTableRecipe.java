package aztech.milite.blocks.processingtable;

import aztech.milite.MIRegistries;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * A recipe for the Processing Table: a machine casing plus two characterising items make a machine.
 *
 * <p>
 * The casing is the {@link #key}, and it is the only ingredient the middle input slot accepts. The other two go in the
 * two side slots, which stay locked until the key is in place and which do not care which of them holds what.
 *
 * <p>
 * All three ingredients are plain {@link Ingredient}s, so a recipe may ask for a tag or an exact item anywhere. The
 * result is always a single item, machines are never crafted in bulk.
 */
public record ProcessingTableRecipe(
        Ingredient key,
        Ingredient first,
        Ingredient second,
        ItemStack result) implements Recipe<RecipeInput> {
    private static final MapCodec<ProcessingTableRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("key").forGetter(ProcessingTableRecipe::key),
                    Ingredient.CODEC_NONEMPTY.fieldOf("first").forGetter(ProcessingTableRecipe::first),
                    Ingredient.CODEC_NONEMPTY.fieldOf("second").forGetter(ProcessingTableRecipe::second),
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ProcessingTableRecipe::result))
                    .apply(instance, ProcessingTableRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, ProcessingTableRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            ProcessingTableRecipe::key,
            Ingredient.CONTENTS_STREAM_CODEC,
            ProcessingTableRecipe::first,
            Ingredient.CONTENTS_STREAM_CODEC,
            ProcessingTableRecipe::second,
            ItemStack.STREAM_CODEC,
            ProcessingTableRecipe::result,
            ProcessingTableRecipe::new);

    /**
     * @return whether this recipe's key is the given stack, which is what unlocks the two side slots
     */
    public boolean matchesKey(ItemStack keyStack) {
        return key.test(keyStack);
    }

    /**
     * @return whether the three given stacks match this recipe's inputs, with the two side stacks either way around
     */
    public boolean matches(ItemStack keyStack, ItemStack firstStack, ItemStack secondStack) {
        if (!key.test(keyStack)) {
            return false;
        }
        return (first.test(firstStack) && second.test(secondStack)) || (first.test(secondStack) && second.test(firstStack));
    }

    @Override
    public boolean isSpecial() {
        // The Processing Table manages its own recipe selection, like the Forge Hammer.
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
        return NonNullList.copyOf(List.of(key, first, second));
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
