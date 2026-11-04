package aztech.milite.client.compat.viewer.impl.jei;

import aztech.milite.client.blocks.processingtable.ProcessingTableScreen;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;

import java.util.Collection;
import java.util.List;

class ProcessingTableGuiContainerHandler implements IGuiContainerHandler<ProcessingTableScreen> {
    private static final Rect2i ARROW_AREA = new Rect2i(ProcessingTableScreen.ARROW_CLICK_X,
            ProcessingTableScreen.ARROW_CLICK_Y, ProcessingTableScreen.ARROW_CLICK_WIDTH,
            ProcessingTableScreen.ARROW_CLICK_HEIGHT);

    @Override
    public Collection<IGuiClickableArea> getGuiClickableAreas(ProcessingTableScreen screen, double guiMouseX, double guiMouseY) {
        return List.of(new IGuiClickableArea() {
            @Override
            public Rect2i getArea() {
                return ARROW_AREA;
            }

            @Override
            public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
                List<RecipeType<?>> types = List.of(ManualPluginJei.processingTableType());
                recipesGui.showTypes(types);
            }
        });
    }
}
