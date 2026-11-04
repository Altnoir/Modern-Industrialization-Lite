package aztech.milite.client.compat.viewer.impl.emi;

import aztech.milite.MIText;
import aztech.milite.client.blocks.processingtable.ProcessingTableScreen;
import aztech.milite.client.compat.viewer.usage.ProcessingTableCategory;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class ProcessingTableClickAreaEmi {
    private ProcessingTableClickAreaEmi() {}

    public static void init() {
        NeoForge.EVENT_BUS.addListener(ProcessingTableClickAreaEmi::onMousePressed);
        NeoForge.EVENT_BUS.addListener(ProcessingTableClickAreaEmi::onScreenRendered);
    }

    private static void onMousePressed(ScreenEvent.MouseButtonPressed.Pre event) {
        if (event.getButton() != 0 || !(event.getScreen() instanceof ProcessingTableScreen screen)) {
            return;
        }

        if (!screen.isOverArrow(event.getMouseX(), event.getMouseY())) {
            return;
        }

        EmiRecipeCategory category = ViewerCategoryEmi.BY_ID.get(ProcessingTableCategory.ID);
        if (category != null) {
            EmiApi.displayRecipeCategory(category);
        }
    }

    private static void onScreenRendered(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof ProcessingTableScreen screen)) {
            return;
        }

        if (!screen.isOverArrow(event.getMouseX(), event.getMouseY())) {
            return;
        }

        event.getGuiGraphics().renderTooltip(Minecraft.getInstance().font, MIText.ClickToSeeRecipes.text(), event.getMouseX(),
                event.getMouseY());
    }
}
