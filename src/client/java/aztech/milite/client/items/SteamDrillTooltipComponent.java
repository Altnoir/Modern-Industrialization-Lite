package aztech.milite.client.items;

import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.client.machines.guicomponents.ProgressBarClient;
import aztech.milite.client.util.RenderHelper;
import aztech.milite.items.SteamDrillItem;
import aztech.milite.machines.guicomponents.ProgressBar;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class SteamDrillTooltipComponent implements ClientTooltipComponent {
    final SteamDrillItem.SteamDrillTooltipData data;

    public SteamDrillTooltipComponent(SteamDrillItem.SteamDrillTooltipData data) {
        this.data = data;
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public int getWidth(Font textRenderer) {
        return 40;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        guiGraphics.blit(MachineScreen.SLOT_ATLAS, x, y, 0, 0, 18, 18, 256, 256);
        RenderHelper.renderAndDecorateItem(guiGraphics, font, data.variant().toStack((int) data.amount()), x + 1, y + 1);
        var progressParams = new ProgressBar.Params(0, 0, "furnace", 14, 14, true);
        ProgressBarClient.renderProgress(guiGraphics, x + 20, y, progressParams, (float) data.burnTicks() / data.maxBurnTicks());
    }
}
