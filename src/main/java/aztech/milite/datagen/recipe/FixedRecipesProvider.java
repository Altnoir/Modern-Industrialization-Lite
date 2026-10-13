package aztech.milite.datagen.recipe;

import aztech.milite.blocks.forgehammer.ForgeHammerRecipe;
import aztech.milite.recipe.json.ShapedRecipeJson;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.material.Fluid;

/** Shared helpers for fixed recipes, including the assembler counterpart of an {@code _asbl} shaped recipe. */
public abstract class FixedRecipesProvider extends MIRecipesProvider {
    protected FixedRecipesProvider(PackOutput output) {
        super(output);
    }

    protected static void shaped(RecipeOutput output, String path, ShapedRecipeJson recipe) {
        recipe.validate();
        recipe.offerTo(output, path);
        if (path.endsWith("_asbl")) {
            recipe.exportToAssembler().offerTo(output,
                    "assembler_generated/" + path.substring(0, path.length() - "_asbl".length()));
        }
    }

    protected static Item item(String id) {
        var location = ResourceLocation.parse(id);
        if (!BuiltInRegistries.ITEM.containsKey(location)) {
            throw new IllegalArgumentException("Unknown recipe item " + id);
        }
        return BuiltInRegistries.ITEM.get(location);
    }

    protected static Ingredient ingredient(String itemOrTag) {
        return itemOrTag.startsWith("#")
                ? Ingredient.of(ItemTags.create(ResourceLocation.parse(itemOrTag.substring(1))))
                : Ingredient.of(item(itemOrTag));
    }

    protected static TagKey<Fluid> fluidTag(String id) {
        return FluidTags.create(ResourceLocation.parse(id));
    }

    protected static void forgeHammer(RecipeOutput output, String path, String input, int count,
            String result, int resultCount, int damage) {
        output.accept(id(path), new ForgeHammerRecipe(ingredient(input), count,
                new ItemStack(item(result), resultCount), damage), null);
    }

    protected static void smelting(RecipeOutput output, String path, String input, String result,
            int resultCount, float experience, int duration) {
        output.accept(id(path), new SmeltingRecipe("", CookingBookCategory.MISC, ingredient(input),
                new ItemStack(item(result), resultCount), experience, duration), null);
    }

    protected static void blasting(RecipeOutput output, String path, String input, String result,
            int resultCount, float experience, int duration) {
        output.accept(id(path), new BlastingRecipe("", CookingBookCategory.MISC, ingredient(input),
                new ItemStack(item(result), resultCount), experience, duration), null);
    }

    protected static void smithing(RecipeOutput output, String path, String template, String base,
            String addition, String result, int resultCount) {
        output.accept(id(path), new SmithingTransformRecipe(ingredient(template), ingredient(base),
                ingredient(addition), new ItemStack(item(result), resultCount)), null);
    }
}
