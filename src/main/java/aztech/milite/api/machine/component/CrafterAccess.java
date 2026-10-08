package aztech.milite.api.machine.component;

public interface CrafterAccess {
    InventoryAccess getInventory();

    boolean hasActiveRecipe();

    boolean matchesMultipleRecipes();

    float getProgress();

    /**
     * The RF per tick the active recipe asks for at the speed of this machine.
     */
    long getBaseRecipeRf();

    /**
     * The RF per tick the machine currently consumes, which is the requested amount unless it runs dry.
     */
    long getCurrentRecipeRf();
}
