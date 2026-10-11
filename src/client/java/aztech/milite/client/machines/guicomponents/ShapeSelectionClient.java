package aztech.milite.client.machines.guicomponents;

import aztech.milite.MIText;
import aztech.milite.client.machines.gui.ClientComponentRenderer;
import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.machines.guicomponents.ShapeSelection;
import aztech.milite.network.machines.ChangeShapePacket;
import aztech.milite.util.TextHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Optional;

public class ShapeSelectionClient extends GuiComponentClient<List<ShapeSelection.LineInfo>, List<Integer>> {
    private Renderer renderer;

    public ShapeSelectionClient(List<ShapeSelection.LineInfo> params, List<Integer> data) {
        super(params, data);
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        return renderer = new Renderer(machineScreen);
    }

    Renderer getRenderer() {
        return renderer;
    }

    List<Integer> getData() {
        return data;
    }

    class Renderer implements ClientComponentRenderer {
        private static final int LABEL_WIDTH = 46;
        private static final int GAP = 4;
        private static final int STEP = 8;
        private static final int MAX_TRACK_WIDTH = 72;
        private static final int KNOB_WIDTH = 8;
        private static final int KNOB_HEIGHT = 12;
        private static final int TRACK_HEIGHT = 4;
        private static final int BOTTOM_OFFSET = 103;
        private static final int PENDING_TIMEOUT = 40;

        private final int backgroundWidth;
        private final int backgroundHeight;

        private int lineIndex;
        private int pendingLine = -1;
        private int pendingIndex = -1;
        private int pendingTimeout;
        private int draggingLine = -1;
        private int cursorX = -1;
        private int cursorY = -1;

        private Renderer(MachineScreen machineScreen) {
            this.backgroundWidth = machineScreen.getGuiParams().backgroundWidth;
            this.backgroundHeight = machineScreen.getGuiParams().backgroundHeight;
        }

        private int getStep(int numValues) {
            return Math.max(1, Math.min(STEP, MAX_TRACK_WIDTH / numValues));
        }

        private int getTrackWidth(int numValues) {
            return getStep(numValues) * numValues;
        }

        private int getTrackLeft(int leftPos, int numValues) {
            return leftPos + (backgroundWidth - getTrackWidth(numValues)) / 2;
        }

        private int getLabelX(int leftPos, int numValues) {
            return getTrackLeft(leftPos, numValues) - GAP - LABEL_WIDTH;
        }

        private int getRowCenterY(int topPos) {
            return topPos + backgroundHeight - BOTTOM_OFFSET;
        }

        private int getIndexAt(int leftPos, double mouseX, int numValues) {
            return Mth.clamp((int) Math.floor((mouseX - getTrackLeft(leftPos, numValues)) / getStep(numValues)), 0, numValues - 1);
        }

        private int getDisplayedIndex(int line) {
            if (line == pendingLine) {
                return pendingIndex;
            }
            return data.get(line);
        }

        private void sendIndex(MachineScreen screen, int line, int target) {
            int current = getDisplayedIndex(line);
            while (current != target) {
                int delta = target > current ? 1 : -1;
                new ChangeShapePacket(screen.getMenu().containerId, line, delta < 0).sendToServer();
                current += delta;
            }

            pendingLine = line;
            pendingIndex = target;
            pendingTimeout = PENDING_TIMEOUT;
        }

        int getLineCount() {
            return params.size();
        }

        int getLineIndex() {
            return lineIndex;
        }

        void setLineIndex(int line) {
            lineIndex = Mth.clamp(line, 0, params.size() - 1);
        }

        Component getLineText(int line) {
            return params.get(line).translations().get(getDisplayedIndex(line));
        }

        public boolean isRowHovered(int leftPos, int topPos, double cursorX, double cursorY) {
            int numValues = params.get(lineIndex).numValues();
            int rowLeft = getLabelX(leftPos, numValues);
            int rowRight = getTrackLeft(leftPos, numValues) + getTrackWidth(numValues);
            int rowCenterY = getRowCenterY(topPos);
            return cursorX >= rowLeft && cursorX < rowRight
                    && cursorY >= rowCenterY - KNOB_HEIGHT / 2 && cursorY < rowCenterY + KNOB_HEIGHT / 2;
        }

        @Override
        public boolean mouseClicked(MachineScreen screen, double mouseX, double mouseY, int button) {
            int leftPos = screen.x();
            int topPos = screen.y();
            if (button != 0 || !isRowHovered(leftPos, topPos, mouseX, mouseY)) {
                return false;
            }

            int numValues = params.get(lineIndex).numValues();
            if (mouseX < getTrackLeft(leftPos, numValues)) {
                return false;
            }

            draggingLine = lineIndex;
            sendIndex(screen, lineIndex, getIndexAt(leftPos, mouseX, numValues));
            return true;
        }

        @Override
        public boolean mouseDragged(MachineScreen screen, double mouseX, double mouseY, int button, double dragX, double dragY) {
            if (draggingLine < 0) {
                return false;
            }
            sendIndex(screen, draggingLine, getIndexAt(screen.x(), mouseX, params.get(draggingLine).numValues()));
            return true;
        }

        @Override
        public boolean mouseReleased(MachineScreen screen, double mouseX, double mouseY, int button) {
            if (draggingLine < 0) {
                return false;
            }
            draggingLine = -1;
            return true;
        }

        @Override
        public void renderBackground(GuiGraphics guiGraphics, int leftPos, int topPos) {
            if (pendingIndex >= 0 && (data.get(pendingLine) == pendingIndex || --pendingTimeout <= 0)) {
                pendingLine = -1;
                pendingIndex = -1;
            }

            int rowCenterY = getRowCenterY(topPos);
            var line = params.get(lineIndex);
            int numValues = line.numValues();
            int index = getDisplayedIndex(lineIndex);
            int trackLeft = getTrackLeft(leftPos, numValues);
            int trackWidth = getTrackWidth(numValues);
            int labelX = getLabelX(leftPos, numValues);
            int knobX = trackLeft + getStep(numValues) * index;
            int knobY = rowCenterY - KNOB_HEIGHT / 2;

            MachineScreen.blitStretched(guiGraphics, trackLeft, rowCenterY - TRACK_HEIGHT / 2, trackWidth, TRACK_HEIGHT, 54, 30, 4, 8);

            guiGraphics.blit(MachineScreen.SLOT_ATLAS, knobX, knobY, 54, 18, KNOB_WIDTH, KNOB_HEIGHT);
            if (draggingLine >= 0 || isRowHovered(leftPos, topPos, cursorX, cursorY)) {
                guiGraphics.blit(MachineScreen.SLOT_ATLAS, knobX, knobY, 62, 18, KNOB_WIDTH, KNOB_HEIGHT);
            }

            var font = Minecraft.getInstance().font;
            var text = line.translations().get(index);
            guiGraphics.drawString(font, text, labelX + LABEL_WIDTH - font.width(text), rowCenterY - 4, 0x404040, false);
        }

        @Override
        public boolean renderTooltip(MachineScreen screen, Font font, GuiGraphics guiGraphics, int leftPos, int topPos, int cursorX, int cursorY) {
            this.cursorX = cursorX;
            this.cursorY = cursorY;

            if (!isRowHovered(leftPos, topPos, cursorX, cursorY)) {
                return false;
            }

            guiGraphics.renderTooltip(font, List.of(MIText.ShapeSelectionTitle.text(),
                    MIText.ShapeSelectionDescription.text().setStyle(TextHelper.GRAY_TEXT)), Optional.empty(), cursorX, cursorY);
            return true;
        }
    }
}
