package aztech.milite.client.machines.guicomponents;

import aztech.milite.client.machines.gui.ClientComponentRenderer;
import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.client.util.RenderHelper;
import aztech.milite.machines.guicomponents.GunpowderOverclockGui;
import aztech.milite.MIText;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class GunpowderOverclockGuiClient extends GuiComponentClient<GunpowderOverclockGui.Params, Integer> {
    public GunpowderOverclockGuiClient(GunpowderOverclockGui.Params params, Integer data) {
        super(params, data);
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        return new Renderer();
    }

    public class Renderer implements ClientComponentRenderer {
        @Override
        public void renderBackground(GuiGraphics guiGraphics, int x, int y) {
            if (data > 0) {
                int px = x + params.renderX();
                int py = y + params.renderY();
                guiGraphics.blit(MachineScreen.SLOT_ATLAS, px, py, 0, 42, 20, 20);
            }
        }

        @Override
        public boolean renderTooltip(MachineScreen screen, Font font, GuiGraphics guiGraphics, int x, int y, int cursorX, int cursorY) {
            if (data > 0) {
                if (RenderHelper.isPointWithinRectangle(params.renderX(), params.renderY(), 20, 20, cursorX - x, cursorY - y)) {
                    guiGraphics.renderTooltip(font, formatOverclock(data), cursorX, cursorY);
                    return true;
                }
            }
            return false;
        }

        public static Component formatOverclock(int remTick) {
            int seconds = remTick / 20;
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;

            String time = String.format("%d", seconds);

            if (hours > 0) {
                time = String.format("%d:%02d:%02d", hours, minutes, seconds % 60);
            } else if (minutes > 0) {
                time = String.format("%d:%02d", minutes, seconds % 60);
            }

            return MIText.GunpowderTime.text(time);
        }
    }
}
