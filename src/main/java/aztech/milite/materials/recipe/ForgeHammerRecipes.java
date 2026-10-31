package aztech.milite.materials.recipe;

import static aztech.milite.materials.part.MIParts.*;

import aztech.milite.materials.MaterialBuilder;
import aztech.milite.materials.part.PartKeyProvider;
import aztech.milite.materials.recipe.builder.ForgeHammerRecipeBuilder;
import net.minecraft.world.item.ItemStack;

/**
 * Standard forge hammer recipes for early-game materials.
 */
public class ForgeHammerRecipes {
    public static void apply(MaterialBuilder.RecipeContext ctx) {
        addRecipe(ctx, INGOT, 1, DUST, 1);
        addRecipe(ctx, NUGGET, 1, TINY_DUST, 1);

        addRecipe(ctx, INGOT, 2, DOUBLE_INGOT, 1);

        addRecipe(ctx, INGOT, 2, PLATE, 1);
        addRecipe(ctx, INGOT, 1, ROD, 1);
        addRecipe(ctx, INGOT, 1, RING, 1);
        addRecipe(ctx, INGOT, 1, BOLT, 2);

        addRecipe(ctx, DOUBLE_INGOT, 1, PLATE, 1);
        addRecipe(ctx, DOUBLE_INGOT, 1, ROD, 2);
        addRecipe(ctx, DOUBLE_INGOT, 1, RING, 2);
        addRecipe(ctx, DOUBLE_INGOT, 1, BOLT, 4);

        addRecipe(ctx, DOUBLE_INGOT, 4, GEAR, 1);

        addRecipe(ctx, ROD, 1, BOLT, 2);
        addRecipe(ctx, ROD, 1, RING, 1);

        addRecipe(ctx, ORE, 1, CRUSHED_DUST, 2);
        addRecipe(ctx, ORE, 1, RAW_METAL, 2);
        addRecipe(ctx, ORE, 1, DUST, 4);
        addRecipe(ctx, RAW_METAL, 3, DUST, 4);
        addRecipe(ctx, CRUSHED_DUST, 3, DUST, 4);
    }

    private static void addRecipe(MaterialBuilder.RecipeContext ctx, PartKeyProvider inputPart, int inputCount, PartKeyProvider outputPart,
            int outputCount) {
        String recipeName = inputPart.key() + "_to_" + outputPart.key();
        var input = ctx.getPart(inputPart);
        var output = ctx.getPart(outputPart);

        if (input != null && output != null) {
            var outputStack = new ItemStack(output, outputCount);
            new ForgeHammerRecipeBuilder(ctx, recipeName, input.getTaggedIngredient(), inputCount, outputStack);
        }
    }
}
