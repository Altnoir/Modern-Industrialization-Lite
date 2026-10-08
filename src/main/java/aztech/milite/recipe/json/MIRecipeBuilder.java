package aztech.milite.recipe.json;

import net.minecraft.data.recipes.RecipeOutput;

public interface MIRecipeBuilder {
    void offerTo(RecipeOutput recipeOutput, String path);
}
