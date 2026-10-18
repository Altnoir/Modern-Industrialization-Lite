package aztech.milite.client.machines.gui;

import aztech.milite.util.Rectangle;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.List;

/**
 * Renderer for the shared data of a client component on the machine screen.
 */
public interface ClientComponentRenderer {
    default void addButtons(ButtonContainer container) {}

    void renderBackground(GuiGraphics guiGraphics, int leftPos, int topPos);

    /**
     * Called after the background of every other component was rendered.
     */
    default void renderForeground(GuiGraphics guiGraphics, int leftPos, int topPos) {}

    /**
     * @return true if a tooltip was rendered, false otherwise
     */
    default boolean renderTooltip(MachineScreen screen, Font font, GuiGraphics guiGraphics, int leftPos, int topPos, int cursorX, int cursorY) {
        return false;
    }

    default void addExtraBoxes(List<Rectangle> rectangles, int leftPos, int topPos) {}

    default boolean mouseClicked(MachineScreen screen, double mouseX, double mouseY, int button) {
        return false;
    }

    default boolean mouseDragged(MachineScreen screen, double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }

    default boolean mouseReleased(MachineScreen screen, double mouseX, double mouseY, int button) {
        return false;
    }

    interface ButtonContainer {
        /**
         * @param u               Texture button u.
         * @param pressAction     A function that is called with the syncId every time
         *                        the button is pressed.
         * @param tooltipSupplier A function that is called every time the tooltip must
         *                        be rendered.
         * @param isPressed       A function that returns true if the button is
         *                        currently pressed.
         * @return
         */
        MachineScreen.MachineButton addButton(int u, Consumer<Integer> pressAction, Supplier<List<Component>> tooltipSupplier,
                Supplier<Boolean> isPressed);

        MachineScreen.MachineButton addButton(int posX, int posY, int width, int height, Consumer<Integer> pressAction,
                Supplier<List<Component>> tooltipSupplier,
                CustomButtonRenderer renderer, Supplier<Boolean> isButtonPresent);

        default MachineScreen.MachineButton addButton(int posX, int posY, int width, int height, Consumer<Integer> pressAction,
                Supplier<List<Component>> tooltipSupplier, CustomButtonRenderer renderer) {
            return addButton(posX, posY, width, height, pressAction, tooltipSupplier, renderer, () -> true);
        }
    }

    @FunctionalInterface
    interface CustomButtonRenderer {
        void renderButton(MachineScreen screen, MachineScreen.MachineButton button, GuiGraphics guiGraphics, int mouseX, int mouseY,
                float partialTicks);
    }
}
