package aztech.milite.materials.recipe.builder;

import net.minecraft.data.recipes.RecipeOutput;

public interface MaterialRecipeBuilder {
    String getRecipeId();

    void cancel();

    boolean isCanceled();

    /**
     * @deprecated don't call, let the MaterialBuilder do it
     */
    @Deprecated
    void save(RecipeOutput recipeOutput);
}
