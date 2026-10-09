package aztech.milite.client.machines.guicomponents;

import aztech.milite.client.machines.gui.ClientComponentRenderer;
import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.client.util.RenderHelper;
import aztech.milite.machines.guicomponents.TemperatureBar;
import aztech.milite.MI;
import aztech.milite.MIText;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class TemperatureBarClient extends GuiComponentClient<TemperatureBar.Params, Integer> {
    public TemperatureBarClient(TemperatureBar.Params params, Integer data) {
        super(params, data);
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        return new Renderer();
    }

    public class Renderer implements ClientComponentRenderer {
        private final ResourceLocation TEXTURE = MI.id("textures/gui/temperature_bar.png");
        private final int WIDTH = 100, HEIGHT = 2;

        @Override
        public void renderBackground(GuiGraphics guiGraphics, int x, int y) {
            guiGraphics.blit(TEXTURE, x + params.renderX() - 1, y + params.renderY() - 1, 0, 2,
                    WIDTH + 2, HEIGHT + 2, 102, 6);
            int barPixels = (int) ((float) data / params.temperatureMax() * WIDTH);
            guiGraphics.blit(TEXTURE, x + params.renderX(), y + params.renderY(), 0, 0, barPixels,
                    HEIGHT, 102, 6);
            guiGraphics.blit(MachineScreen.SLOT_ATLAS, x + params.renderX() - 22, y + params.renderY() + HEIGHT / 2 - 10, 144, 0, 20, 20);
        }

        @Override
        public boolean renderTooltip(MachineScreen screen, Font font, GuiGraphics guiGraphics, int x, int y, int cursorX, int cursorY) {
            if (RenderHelper.isPointWithinRectangle(params.renderX(), params.renderY(), WIDTH, HEIGHT, cursorX - x,
                    cursorY - y)) {
                guiGraphics.renderTooltip(font, MIText.Temperature.text(data), cursorX, cursorY);
                return true;
            }
            return false;
        }
    }
}
