package aztech.milite.client.machines.guicomponents;

import aztech.milite.MIText;
import aztech.milite.client.machines.gui.ClientComponentRenderer;
import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.machines.guicomponents.ShapeSelection;
import aztech.milite.network.machines.ChangeShapePacket;
import aztech.milite.util.Rectangle;
import aztech.milite.util.TextHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class ShapeSelectionClient extends GuiComponentClient<List<ShapeSelection.LineInfo>, List<Integer>> {
    private Renderer renderer;

    public ShapeSelectionClient(List<ShapeSelection.LineInfo> params, List<Integer> data) {
        super(params, data);
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        int maxWidth = 1;
        for (var line : params) {
            for (var tooltip : line.translations()) {
                maxWidth = Math.max(maxWidth, Minecraft.getInstance().font.width(tooltip));
            }
        }

        return renderer = new Renderer(maxWidth);
    }

    Renderer getRenderer() {
        return renderer;
    }

    List<Integer> getData() {
        return data;
    }

    class Renderer implements ClientComponentRenderer {
        boolean isPanelOpen = false;
        private final int btnSize = 12;
        private final int borderSize = 3;
        private final int outerPadding = 5;
        private final int innerPadding = 5;

        private final int textMaxWidth;
        private final int panelWidth;

        private Renderer(int textMaxWidth) {
            this.textMaxWidth = textMaxWidth;
            this.panelWidth = borderSize + outerPadding + btnSize + innerPadding + textMaxWidth + innerPadding + btnSize + outerPadding;
        }

        private static int getVerticalPos(int lineId) {
            return 46 + 16 * lineId;
        }

        @Override
        public void addButtons(ButtonContainer container) {
            // Two buttons per line
            for (int i = 0; i < params.size(); ++i) {
                int iCopy = i;
                var line = params.get(i);
                int baseU = line.useArrows() ? 174 : 150;
                int v = 58;

                // Left button
                container.addButton(-panelWidth + borderSize + outerPadding, getVerticalPos(i), btnSize, btnSize, syncId -> {
                    new ChangeShapePacket(syncId, iCopy, true).sendToServer();
                }, List::of, (screen, button, guiGraphics, mouseX, mouseY, delta) -> {
                    if (data.get(iCopy) == 0) {
                        screen.blitButtonNoHighlight(button, guiGraphics, baseU, v + 12);
                    } else {
                        screen.blitButtonSmall(button, guiGraphics, baseU, v);
                    }
                }, () -> isPanelOpen);

                // Right button
                container.addButton(-btnSize - outerPadding, getVerticalPos(i), btnSize, btnSize, syncId -> {
                    new ChangeShapePacket(syncId, iCopy, false).sendToServer();
                }, List::of, (screen, button, guiGraphics, mouseX, mouseY, delta) -> {
                    if (data.get(iCopy) == line.numValues() - 1) {
                        screen.blitButtonNoHighlight(button, guiGraphics, baseU + 12, v + 12);
                    } else {
                        screen.blitButtonSmall(button, guiGraphics, baseU + 12, v);
                    }
                }, () -> isPanelOpen);
            }

            // Big button to open panel
            container.addButton(-14, 18, 12, 12, syncId -> isPanelOpen = !isPanelOpen,
                    () -> List.of(MIText.ShapeSelectionTitle.text(), MIText.ShapeSelectionDescription.text().setStyle(TextHelper.GRAY_TEXT)),
                    (screen, button, guiGraphics, mouseX, mouseY, delta) -> screen.blitButton12(button, guiGraphics, 24, isPanelOpen));
        }

        @Override
        public void renderBackground(GuiGraphics guiGraphics, int leftPos, int topPos) {
            var box = getBox(leftPos, topPos);

            guiGraphics.blit(MachineScreen.BACKGROUND, box.x(), box.y(), 0, 0, box.w(), box.h() - 4);
            guiGraphics.blit(MachineScreen.BACKGROUND, box.x(), box.y() + box.h() - 4, 0, 252, box.w(), 4);

            if (isPanelOpen) {
                RenderSystem.disableDepthTest();
                for (int i = 0; i < params.size(); ++i) {
                    var line = params.get(i);
                    var tooltip = line.translations().get(data.get(i));
                    var width = Minecraft.getInstance().font.width(tooltip);
                    guiGraphics.drawString(
                            Minecraft.getInstance().font, tooltip,
                            box.x() + borderSize + outerPadding + btnSize + innerPadding + (textMaxWidth - width) / 2,
                            topPos + getVerticalPos(i) + 2, 0x404040, false);
                }
                RenderSystem.enableDepthTest();
            }
        }

        public Rectangle getBox(int leftPos, int topPos) {
            if (isPanelOpen) {
                int topOffset = 10;
                return new Rectangle(leftPos - panelWidth, topPos + topOffset, panelWidth,
                        getVerticalPos(params.size() - 1) - topOffset + btnSize + outerPadding + borderSize);
            } else {
                return new Rectangle(leftPos - 31, topPos + 10, 31, 34);
            }
        }

        @Override
        public void addExtraBoxes(List<Rectangle> rectangles, int leftPos, int topPos) {
            rectangles.add(getBox(leftPos, topPos));
        }
    }
}
