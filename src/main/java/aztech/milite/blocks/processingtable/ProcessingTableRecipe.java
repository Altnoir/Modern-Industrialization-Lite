/*
 * MIT License
 *
 * Copyright (c) 2026 Altnoir Null々
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package aztech.milite.blocks.processingtable;

import aztech.milite.MIRegistries;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
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

/**
 * A recipe for the Processing Table: one machine casing plus one characterising item make a machine.
 *
 * <p>
 * Both ingredients are plain {@link Ingredient}s, so a recipe may ask for a tag or an exact item in either slot. The
 * result is always a single item, machines are never crafted in bulk.
 */
public record ProcessingTableRecipe(
        Ingredient first,
        Ingredient second,
        ItemStack result) implements Recipe<RecipeInput> {
    private static final MapCodec<ProcessingTableRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("first").forGetter(ProcessingTableRecipe::first),
                    Ingredient.CODEC_NONEMPTY.fieldOf("second").forGetter(ProcessingTableRecipe::second),
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ProcessingTableRecipe::result))
                    .apply(instance, ProcessingTableRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, ProcessingTableRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            ProcessingTableRecipe::first,
            Ingredient.CONTENTS_STREAM_CODEC,
            ProcessingTableRecipe::second,
            ItemStack.STREAM_CODEC,
            ProcessingTableRecipe::result,
            ProcessingTableRecipe::new);

    /**
     * @return whether the two given stacks match this recipe's inputs, either way around
     */
    public boolean matches(ItemStack firstStack, ItemStack secondStack) {
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
        return NonNullList.copyOf(List.of(first, second));
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
