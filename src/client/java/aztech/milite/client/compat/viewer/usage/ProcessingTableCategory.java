package aztech.milite.client.compat.viewer.usage;

import aztech.milite.blocks.processingtable.ProcessingTableRecipe;
import aztech.milite.client.compat.viewer.abstraction.ViewerCategory;
import aztech.milite.MI;
import aztech.milite.MIBlock;
import aztech.milite.MIRegistries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Comparator;
import java.util.function.Consumer;

/**
 * Recipe viewer category of the Processing Table: two inputs make one machine.
 *
 * <p>
 * The geometry deliberately matches {@link ForgeHammerCategory}: same 150x40 box, the first input
 * on the Forge Hammer's input position and the arrow on the Forge Hammer's arrow position, so both
 * hand-crafted stations look alike in EMI, JEI and REI. No tooltip is registered.
 */
public class ProcessingTableCategory extends ViewerCategory<RecipeHolder<ProcessingTableRecipe>> {
    public static final ResourceLocation ID = MI.id("processing_table");

    private static final int WIDTH = 98;
    private static final int HEIGHT = 28;

    private final int startPointY;

    public ProcessingTableCategory() {
        super(recipeHolderClass(), ID, MIBlock.PROCESSING_TABLE.asBlock().getName(),
                MIBlock.PROCESSING_TABLE.asItem().getDefaultInstance(), WIDTH, HEIGHT);

        this.startPointY = HEIGHT / 2 - 8;
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

        builder.inputSlot(8, startPointY).ingredient(recipe.first(), 1, 1);
        builder.inputSlot(26, startPointY).ingredient(recipe.second(), 1, 1);
        builder.outputSlot(74, startPointY).item(recipe.result());
    }

    @Override
    public void buildWidgets(RecipeHolder<ProcessingTableRecipe> holder, WidgetList widgets) {
        // Same arrow position and size as the Forge Hammer category, centred on the two inputs.
        widgets.arrow(46, startPointY);
    }

    @Override
    public ResourceLocation getRecipeId(RecipeHolder<ProcessingTableRecipe> recipe) {
        return recipe.id();
    }
}
