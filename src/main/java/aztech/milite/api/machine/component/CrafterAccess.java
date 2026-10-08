package aztech.milite.api.machine.component;

public interface CrafterAccess {
    InventoryAccess getInventory();

    boolean hasActiveRecipe();

    boolean matchesMultipleRecipes();

    float getProgress();

    int getEfficiencyTicks();

    int getMaxEfficiencyTicks();

    long getBaseRecipeEu();

    long getCurrentRecipeEu();
}
