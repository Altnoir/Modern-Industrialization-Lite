package aztech.milite.client.machines.guicomponents;

import aztech.milite.client.machines.gui.ClientComponentRenderer;
import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.machines.guicomponents.ProgressBar;
import net.minecraft.client.gui.GuiGraphics;

public class ProgressBarClient extends GuiComponentClient<ProgressBar.Params, Float> {
    public ProgressBarClient(ProgressBar.Params params, Float data) {
        super(params, data);
    }

    public static void renderProgress(GuiGraphics guiGraphics, int x, int y, ProgressBar.Params params, float progress) {
        // background
        guiGraphics.blit(params.getTextureId(), x + params.renderX(), y + params.renderY(), 0, 0, params.width(), params.height(), params.width(), params.textureHeight());
        // foreground
        if (progress > 0) {
            if (!params.isVertical()) {
                int foregroundPixels = (int) (progress * params.width());
                guiGraphics.blit(params.getTextureId(), x + params.renderX(), y + params.renderY(), 0, params.height(), foregroundPixels, params.height(), params.width(), params.textureHeight());
            } else {
                int foregroundPixels = (int) (progress * params.height());
                guiGraphics.blit(params.getTextureId(), x + params.renderX(), y + params.renderY() + params.height() - foregroundPixels, 0,
                        params.textureHeight() - foregroundPixels, params.width(), foregroundPixels, params.width(), params.textureHeight());
            }
        }
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        return new Renderer();
    }

    public class Renderer implements ClientComponentRenderer {
        @Override
        public void renderBackground(GuiGraphics guiGraphics, int x, int y) {
            renderProgress(guiGraphics, x, y, params, data);
        }
    }
}
