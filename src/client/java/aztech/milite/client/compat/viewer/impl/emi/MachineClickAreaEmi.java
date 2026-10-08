package aztech.milite.client.compat.viewer.impl.emi;

import aztech.milite.client.compat.viewer.impl.MachineScreenPredicateTest;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.compat.rei.machines.ReiMachineRecipes;
import aztech.milite.MIText;
import aztech.milite.util.Rectangle;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Gives EMI the "click the progress bar to see the recipes" behaviour that JEI and REI already have.
 *
 * <p>
 * EMI exposes no screen click hook, so the click and the hover hint are hooked here through NeoForge's screen
 * events. The clickable rectangle and the recipe categories come from the very same maps that the JEI and REI
 * implementations use, so the behaviour and its gating stay identical. Only single block machines are covered;
 * multiblock controllers are intentionally left alone.
 */
public final class MachineClickAreaEmi {
    private MachineClickAreaEmi() {}

    public static void init() {
        NeoForge.EVENT_BUS.addListener(MachineClickAreaEmi::onMousePressed);
        NeoForge.EVENT_BUS.addListener(MachineClickAreaEmi::onScreenRendered);
    }

    private static void onMousePressed(ScreenEvent.MouseButtonPressed.Pre event) {
        // Left click only, to stay out of the way of EMI's own drag and drop handling.
        if (event.getButton() != 0 || !(event.getScreen() instanceof MachineScreen screen)) {
            return;
        }

        EmiRecipeCategory category = categoryAt(screen, event.getMouseX(), event.getMouseY());
        if (category != null) {
            EmiApi.displayRecipeCategory(category);
        }
    }

    /**
     * Draws the hint while the mouse is over the progress bar.
     *
     * <p>
     * This uses {@link ScreenEvent.Render.Post} rather than {@code RenderTooltipEvent.Pre}: the latter only fires
     * when vanilla already decided to draw a tooltip, and over a progress bar there is nothing to draw, so it
     * would never fire.
     */
    private static void onScreenRendered(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof MachineScreen screen)) {
            return;
        }

        if (categoryAt(screen, event.getMouseX(), event.getMouseY()) == null) {
            return;
        }

        event.getGuiGraphics().renderTooltip(Minecraft.getInstance().font, MIText.ClickToSeeRecipes.text(), event.getMouseX(),
                event.getMouseY());
    }

    /**
     * @return the EMI recipe category to display if the given screen position is over the machine's progress bar,
     *         or {@code null} if this position is not a clickable recipe area.
     */
    private static @Nullable EmiRecipeCategory categoryAt(MachineScreen screen, double mouseX, double mouseY) {
        ResourceLocation blockId = screen.getMenu().guiParams.blockId;
        List<ReiMachineRecipes.ClickAreaCategory> categories = ReiMachineRecipes.machineToClickAreaCategory.get(blockId);
        Rectangle rectangle = ReiMachineRecipes.machineToClickArea.get(blockId);

        if (categories == null || rectangle == null) {
            return null;
        }

        // The stored rectangle is relative to the GUI, so translate the mouse position like the REI handler does.
        if (!rectangle.contains(mouseX - screen.x(), mouseY - screen.y())) {
            return null;
        }

        for (ReiMachineRecipes.ClickAreaCategory clickAreaCategory : categories) {
            if (!MachineScreenPredicateTest.test(clickAreaCategory.predicate, screen)) {
                continue;
            }

            EmiRecipeCategory category = ViewerCategoryEmi.BY_ID.get(clickAreaCategory.category);
            if (category != null) {
                return category;
            }
        }

        return null;
    }
}
