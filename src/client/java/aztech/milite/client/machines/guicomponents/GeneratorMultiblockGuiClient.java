package aztech.milite.client.machines.guicomponents;

import aztech.milite.client.machines.gui.ClientComponentRenderer;
import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.machines.guicomponents.CraftingMultiblockGui;
import aztech.milite.machines.guicomponents.GeneratorMultiblockGui;
import aztech.milite.MIText;
import aztech.milite.util.TextHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Unit;

public class GeneratorMultiblockGuiClient extends GuiComponentClient<Unit, GeneratorMultiblockGui.Data> {
    public GeneratorMultiblockGuiClient(Unit params, GeneratorMultiblockGui.Data data) {
        super(params, data);
    }

    public boolean isShapeValid() {
        return data.isShapeValid();
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        return new Renderer();
    }

    public class Renderer extends CraftingMultiblockGuiClient.BaseScreenRenderer implements ClientComponentRenderer {
        @Override
        public void renderBackground(GuiGraphics guiGraphics, int x, int y) {
            Font font = Minecraft.getInstance().font;

            int lineWidth = CraftingMultiblockGui.W - 10;
            int deltaY = renderScreenAndStatus(data.isShapeValid(), false, font, guiGraphics, x, y);

            if (data.isShapeValid()) {
                deltaY += 11;

                deltaY += drawWordWrap(guiGraphics, font, MIText.GeneratorCurrentRf.text(TextHelper.getRfTextTick(data.currentRfGeneration())), x + 10, y + deltaY, 11, 0xFFFFFF, false);

                drawWordWrap(guiGraphics, font, MIText.GeneratorMaxRf.text(TextHelper.getRfTextTick(data.maxRfGeneration())), x + 10, y + deltaY, 11, 0xFFFFFF, false);
            }
        }
    }
}
