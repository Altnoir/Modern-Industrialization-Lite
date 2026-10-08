package aztech.milite.client.machines.guicomponents;

import aztech.milite.client.machines.gui.ClientComponentRenderer;
import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.client.util.RenderHelper;
import aztech.milite.machines.guicomponents.EnergyBar;
import aztech.milite.MIText;
import aztech.milite.util.TextHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.Optional;

public class EnergyBarClient extends GuiComponentClient<EnergyBar.Params, EnergyBar.Data> {
    public EnergyBarClient(EnergyBar.Params parameters, EnergyBar.Data data) {
        super(parameters, data);
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        return new Renderer();
    }

    public class Renderer implements ClientComponentRenderer {
        public static final int WIDTH = 13;
        public static final int HEIGHT = 18;

        public static void renderEnergy(GuiGraphics guiGraphics, int px, int py, float fill) {
            guiGraphics.blit(MachineScreen.SLOT_ATLAS, px, py, 230, 0, WIDTH, HEIGHT);
            int fillPixels = (int) (fill * HEIGHT * 0.9 + HEIGHT * 0.1);
            if (fill > 0.95)
                fillPixels = HEIGHT;
            guiGraphics.blit(MachineScreen.SLOT_ATLAS, px, py + HEIGHT - fillPixels, 243, HEIGHT - fillPixels, WIDTH, fillPixels);
        }

        @Override
        public void renderBackground(GuiGraphics guiGraphics, int x, int y) {
            renderEnergy(guiGraphics, x + params.renderX(), y + params.renderY(), (float) data.rf() / data.maxRf());
        }

        @Override
        public boolean renderTooltip(MachineScreen screen, Font font, GuiGraphics guiGraphics, int x, int y, int cursorX, int cursorY) {
            if (RenderHelper.isPointWithinRectangle(params.renderX(), params.renderY(), WIDTH, HEIGHT, cursorX - x, cursorY - y)) {
                Component tooltip;
                if (Screen.hasShiftDown()) {
                    tooltip = MIText.RfMaxed.text(data.rf(), data.maxRf(), "");
                } else {
                    TextHelper.MaxedAmount maxedAmount = TextHelper.getMaxedAmount(data.rf(), data.maxRf());
                    tooltip = MIText.RfMaxed.text(maxedAmount.digit(), maxedAmount.maxDigit(), maxedAmount.unit());
                }
                guiGraphics.renderTooltip(font, Collections.singletonList(tooltip), Optional.empty(), cursorX, cursorY);
                return true;
            }
            return false;
        }
    }
}
