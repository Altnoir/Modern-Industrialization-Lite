package aztech.milite.client.compat.viewer.usage;

import aztech.milite.MI;
import aztech.milite.MIBlock;
import aztech.milite.MIRegistries;
import aztech.milite.blocks.processingtable.ProcessingTableRecipe;
import aztech.milite.client.compat.viewer.abstraction.ViewerCategory;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Comparator;
import java.util.function.Consumer;

public class ProcessingTableCategory extends ViewerCategory<RecipeHolder<ProcessingTableRecipe>> {
    public static final ResourceLocation ID = MI.id("processing_table");

    private static final int SLOT = 18;
    private static final int GRID_X = 35;
    private static final int GRID_Y = 5;
    private static final int MIDDLE_Y = GRID_Y + SLOT;
    private static final int KEY_X = 5;
    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_HEIGHT = 17;
    private static final int GAP = 5;
    private static final int ARROW_X = GRID_X + SLOT * 3 + GAP;
    private static final int OUTPUT_X = ARROW_X + ARROW_WIDTH + GAP;
    private static final int WIDTH = OUTPUT_X + SLOT + GAP;
    private static final int HEIGHT = GRID_Y * 2 + SLOT * 3 - 2;

    public ProcessingTableCategory() {
        super(recipeHolderClass(), ID, MIBlock.PROCESSING_TABLE.asBlock().getName(),
                MIBlock.PROCESSING_TABLE.asItem().getDefaultInstance(), WIDTH, HEIGHT);
    }

    @Override
    public void buildWorkstations(WorkstationConsumer consumer) {
        consumer.accept(MIBlock.PROCESSING_TABLE);
    }

    @Override
    public void buildRecipes(RecipeManager recipeManager, RegistryAccess registryAccess, Consumer<RecipeHolder<ProcessingTableRecipe>> consumer) {
        recipeManager.getAllRecipesFor(MIRegistries.PROCESSING_TABLE_RECIPE_TYPE.get())
                .stream()
                .sorted(Comparator.comparing(RecipeHolder::id))
                .forEach(consumer);
    }

    @Override
    public void buildLayout(RecipeHolder<ProcessingTableRecipe> holder, LayoutBuilder builder) {
        var recipe = holder.value();

        builder.inputSlot(KEY_X, MIDDLE_Y).ingredient(recipe.casing(), 1, 1);

        for (int slot = ProcessingTableRecipe.MIN_SLOT; slot <= ProcessingTableRecipe.MAX_SLOT; slot++) {
            int column = (slot - ProcessingTableRecipe.MIN_SLOT) % 3;
            int row = (slot - ProcessingTableRecipe.MIN_SLOT) / 3;
            var cell = builder.inputSlot(GRID_X + column * SLOT, GRID_Y + row * SLOT);

            Ingredient ingredient = recipe.ingredientAt(slot);
            cell.ingredient(ingredient, 1, 1);
        }

        builder.outputSlot(OUTPUT_X, MIDDLE_Y).item(recipe.result());
    }

    @Override
    public void buildWidgets(RecipeHolder<ProcessingTableRecipe> holder, WidgetList widgets) {
        widgets.arrow(ARROW_X, MIDDLE_Y + (SLOT - ARROW_HEIGHT) / 2);
    }

    @Override
    public ResourceLocation getRecipeId(RecipeHolder<ProcessingTableRecipe> recipe) {
        return recipe.id();
    }
}
