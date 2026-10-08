package aztech.milite.inventory;

import net.minecraft.resources.ResourceLocation;

public interface MoveRecipeHandler {
    void moveRecipe(ResourceLocation recipeId, int fillAction, int amount);
}
