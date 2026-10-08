package aztech.milite.client.machines.guicomponents;

import aztech.milite.client.machines.gui.ClientComponentRenderer;
import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.machines.guicomponents.AutoExtract;
import aztech.milite.MIText;
import aztech.milite.network.machines.SetAutoExtractPacket;
import aztech.milite.util.TextHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class AutoExtractClient extends GuiComponentClient<AutoExtract.Params, AutoExtract.Data> {
    public AutoExtractClient(AutoExtract.Params params, AutoExtract.Data data) {
        super(params, data);
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        return new Renderer();
    }

    private class Renderer implements ClientComponentRenderer {
        @Override
        public void addButtons(ButtonContainer container) {
            if (params.hasExtractFluids()) {
                addExtractButton(container, false);
            }
            if (params.hasExtractItems()) {
                addExtractButton(container, true);
            }
        }

        private void addExtractButton(ButtonContainer container, boolean isItem) {
            int u = isItem ? 12 : 0;
            var display = params.display();
            container.addButton(u, syncId -> {
                boolean newExtract;
                if (isItem) {
                    newExtract = !data.extractItems();
                    data = new AutoExtract.Data(newExtract, data.extractFluids());
                } else {
                    newExtract = !data.extractFluids();
                    data = new AutoExtract.Data(data.extractItems(), newExtract);
                }
                new SetAutoExtractPacket(syncId, isItem, newExtract).sendToServer();
            }, () -> {
                List<Component> lines = new ArrayList<>();
                if (isItem ? data.extractItems() : data.extractFluids()) {
                    if (isItem) {
                        if (display != AutoExtract.Display.EXTRACT) {
                            lines.add(MIText.ItemAutoInsertOn.text());
                        }
                        if (display != AutoExtract.Display.INSERT) {
                            lines.add(MIText.ItemAutoExtractOn.text());
                        }
                    } else {
                        if (display != AutoExtract.Display.EXTRACT) {
                            lines.add(MIText.FluidAutoInsertOn.text());
                        }
                        if (display != AutoExtract.Display.INSERT) {
                            lines.add(MIText.FluidAutoExtractOn.text());
                        }
                    }
                    lines.add(MIText.ClickToDisable.text().setStyle(TextHelper.GRAY_TEXT));
                } else {
                    if (isItem) {
                        if (display != AutoExtract.Display.EXTRACT) {
                            lines.add(MIText.ItemAutoInsertOff.text());
                        }
                        if (display != AutoExtract.Display.INSERT) {
                            lines.add(MIText.ItemAutoExtractOff.text());
                        }
                    } else {
                        if (display != AutoExtract.Display.EXTRACT) {
                            lines.add(MIText.FluidAutoInsertOff.text());
                        }
                        if (display != AutoExtract.Display.INSERT) {
                            lines.add(MIText.FluidAutoExtractOff.text());
                        }
                    }
                    lines.add(MIText.ClickToEnable.text().setStyle(TextHelper.GRAY_TEXT));
                }
                return lines;
            }, () -> isItem ? data.extractItems() : data.extractFluids());
        }

        @Override
        public void renderBackground(GuiGraphics guiGraphics, int x, int y) {}
    }
}
