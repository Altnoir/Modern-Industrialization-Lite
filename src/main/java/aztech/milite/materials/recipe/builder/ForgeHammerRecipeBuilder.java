package aztech.milite.materials.recipe.builder;

import aztech.milite.blocks.forgehammer.ForgeHammerRecipe;
import aztech.milite.materials.MaterialBuilder;
import aztech.milite.MI;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;

public class ForgeHammerRecipeBuilder implements MaterialRecipeBuilder {
    private final String recipeId;
    private final MaterialBuilder.RecipeContext context;
    private boolean canceled = false;
    private final ForgeHammerRecipe recipe;

    public ForgeHammerRecipeBuilder(MaterialBuilder.RecipeContext context, String id, Ingredient input, int inputCount, ItemStack output,
            int hammerDamage) {
        this.recipeId = "forge_hammer/" + id;
        this.context = context;
        this.recipe = new ForgeHammerRecipe(input, inputCount, output, hammerDamage);
        context.addRecipe(this);
    }

    @Override
    public String getRecipeId() {
        return recipeId;
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void save(RecipeOutput recipeOutput) {
        if (!canceled) {
            String fullId = "materials/" + context.getMaterialName() + "/" + recipeId;
            recipeOutput.accept(MI.id(fullId), recipe, null);
        }
    }
}
