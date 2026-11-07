package aztech.milite.client.machines.guicomponents;

import aztech.milite.MIText;
import aztech.milite.client.machines.gui.ClientComponentRenderer;
import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.machines.guicomponents.CraftingMultiblockGui;
import aztech.milite.machines.guicomponents.MultiblockBuild;
import aztech.milite.network.machines.BuildMultiblockPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class MultiblockBuildClient extends GuiComponentClient<Unit, MultiblockBuild.Data> {
    private static final int BUTTON_WIDTH = 46;
    private static final int BUTTON_HEIGHT = 14;
    private static final int SLIDER_ROW_BOTTOM_OFFSET = 96;
    private static final int ROW_HEIGHT = 16;
    private static final int LINE_HEIGHT = 11;
    private static final int ICON_SIZE = 16;
    private static final int ICON_GAP = 2;
    private static final int TANK_WIDTH = 46;

    public MultiblockBuildClient(Unit params, MultiblockBuild.Data data) {
        super(params, data);
    }

    /**
     * Top of the nuclear reactor panel, which starts with the shape status text drawn by
     * {@link NuclearReactorGuiClient}. The panel is centered on where that text sits on its own.
     */
    static int getReactorStatusTop(MachineScreen screen, Font font) {
        var build = screen.getMenu().getComponent(MultiblockBuildClient.class);
        int rows = build == null ? 0 : build.getMaterialRowCount();
        int panelHeight = rows == 0 ? 0 : LINE_HEIGHT + ROW_HEIGHT * rows;
        return NuclearReactorGuiClient.GRID_CENTER_Y + font.lineHeight / 2 - (font.lineHeight + panelHeight) / 2;
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        return new Renderer(machineScreen);
    }

    private int getMaterialRowCount() {
        return data.materials().size();
    }

    private class Renderer implements ClientComponentRenderer {
        private final MachineScreen screen;
        private final int backgroundWidth;
        private final int backgroundHeight;
        private final int tankPosX;

        private Renderer(MachineScreen screen) {
            this.screen = screen;
            this.backgroundWidth = screen.getGuiParams().backgroundWidth;
            this.backgroundHeight = screen.getGuiParams().backgroundHeight;
            this.tankPosX = (backgroundWidth - TANK_WIDTH) / 2;
        }

        private boolean isLargeTank() {
            return screen.getMenu().getComponent(LargeTankFluidDisplayClient.class) != null;
        }

        private boolean isNuclearReactor() {
            return screen.getMenu().getComponent(NuclearReactorGuiClient.class) != null;
        }

        private boolean hasStatusText() {
            var menu = screen.getMenu();
            return menu.getComponent(CraftingMultiblockGuiClient.class) != null
                    || menu.getComponent(GeneratorMultiblockGuiClient.class) != null;
        }

        private boolean hasSlider() {
            return screen.getMenu().getComponent(ShapeSelectionClient.class) != null;
        }

        private int getRowCenterY() {
            return backgroundHeight - SLIDER_ROW_BOTTOM_OFFSET;
        }

        @Override
        public void addButtons(ButtonContainer container) {
            int buttonX = (backgroundWidth - BUTTON_WIDTH) / 2;
            int buttonY = getRowCenterY() - 8 - BUTTON_HEIGHT;

            if (isLargeTank()) {
                buttonX = tankPosX + TANK_WIDTH + 4;
                buttonY = getRowCenterY() - BUTTON_HEIGHT / 2;
            } else if (!hasStatusText() && !hasSlider()) {
                // GUIs without the info box and without a shape slider (steam boilers) keep the row free.
                buttonY = getRowCenterY() - BUTTON_HEIGHT / 2;
            }

            container.addButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT,
                    syncId -> {
                        if (data.shapeValid()) {
                            return;
                        }
                        new BuildMultiblockPacket(syncId).sendToServer();
                    },
                    () -> List.of(),
                    (s, button, guiGraphics, mouseX, mouseY, partialTicks) -> {
                        MachineScreen.blitStretched(guiGraphics, button.getX(), button.getY(), button.getWidth(), button.getHeight(),
                                36, 18, 2, 14);
                        if (button.isHovered()) {
                            MachineScreen.blitStretched(guiGraphics, button.getX(), button.getY(), button.getWidth(), button.getHeight(),
                                    54, 0, 2, 14);
                        }
                        var font = Minecraft.getInstance().font;
                        var text = MIText.Build.text().getString();
                        guiGraphics.drawString(font, text, button.getX() + (button.getWidth() - font.width(text)) / 2, button.getY() + 3,
                                0x505050, false);
                    }, () -> !data.shapeValid());
        }

        @Override
        public void renderBackground(GuiGraphics guiGraphics, int leftPos, int topPos) {}

        @Override
        public void renderForeground(GuiGraphics guiGraphics, int leftPos, int topPos) {
            if (data.shapeValid() || data.materials().isEmpty()) {
                return;
            }

            Font font = Minecraft.getInstance().font;
            if (isLargeTank()) {
                renderLargeTankPanel(guiGraphics, font, leftPos, topPos);
                return;
            }
            if (isNuclearReactor()) {
                renderReactorPanel(guiGraphics, font, leftPos, topPos);
                return;
            }

            int panelY = getStatusBottom(font) + 2;
            // Skip the panel rather than draw it over the player inventory.
            if (panelY + getPanelHeight() > backgroundHeight - SLIDER_ROW_BOTTOM_OFFSET) {
                return;
            }
            renderPanel(guiGraphics, font, leftPos + 10, topPos + panelY);
        }

        private int getPanelHeight() {
            return LINE_HEIGHT + ROW_HEIGHT * data.materials().size();
        }

        /**
         * Bottom of the shape status text drawn by
         * {@link CraftingMultiblockGuiClient.BaseScreenRenderer#renderScreenAndStatus}.
         */
        private int getStatusBottom(Font font) {
            var menu = screen.getMenu();
            var craftingGui = menu.getComponent(CraftingMultiblockGuiClient.class);
            var generatorGui = menu.getComponent(GeneratorMultiblockGuiClient.class);
            if (craftingGui == null && generatorGui == null) {
                return 21;
            }

            int lineWidth = CraftingMultiblockGui.W - 10;
            int lines = font.split(MIText.MultiblockShapeInvalid.text(), lineWidth).size();
            if (craftingGui != null && craftingGui.matchesMultipleRecipes()) {
                lines += font.split(MIText.MachineMultipleRecipes1.text(), lineWidth).size();
            }
            return 21 + lines * LINE_HEIGHT;
        }

        private void renderPanel(GuiGraphics guiGraphics, Font font, int x, int y) {
            guiGraphics.drawString(font, MIText.MissingMaterials.text(), x, y, 0xFFFFFF, false);
            y += LINE_HEIGHT;

            for (var material : data.materials()) {
                guiGraphics.renderItem(new ItemStack(material.item()), x, y);
                guiGraphics.drawString(font, getCountText(material), x + ICON_SIZE + ICON_GAP, y + 4, getCountColor(material), false);
                y += ROW_HEIGHT;
            }
        }

        private void renderLargeTankPanel(GuiGraphics guiGraphics, Font font, int leftPos, int topPos) {
            int minX = leftPos + 2;
            int right = leftPos + tankPosX - 4;
            int iconX = right - ICON_SIZE - ICON_GAP;
            int y = topPos + LargeTankFluidDisplayClient.TANK_Y;

            var materialsTitle = MIText.MissingMaterials.text();
            guiGraphics.drawString(font, materialsTitle, Math.max(minX, right - font.width(materialsTitle)), y, 0xFFFFFF, false);
            y += LINE_HEIGHT;

            for (var material : data.materials()) {
                guiGraphics.renderItem(new ItemStack(material.item()), iconX, y);
                var count = getCountText(material);
                guiGraphics.drawString(font, count, Math.max(minX, iconX - ICON_GAP - font.width(count)), y + 4, getCountColor(material), false);
                y += ROW_HEIGHT;
            }
        }

        private void renderReactorPanel(GuiGraphics guiGraphics, Font font, int leftPos, int topPos) {
            int centerX = leftPos + NuclearReactorGuiClient.GRID_CENTER_X;
            int y = topPos + getReactorStatusTop(screen, font) + font.lineHeight;

            int rowWidth = 0;
            for (var material : data.materials()) {
                rowWidth = Math.max(rowWidth, ICON_SIZE + ICON_GAP + font.width(getCountText(material)));
            }
            int rowX = centerX - rowWidth / 2;

            var materialsTitle = MIText.MissingMaterials.text();
            guiGraphics.drawString(font, materialsTitle, centerX - font.width(materialsTitle) / 2, y, 0xFFFFFF, false);
            y += LINE_HEIGHT;

            for (var material : data.materials()) {
                guiGraphics.renderItem(new ItemStack(material.item()), rowX, y);
                guiGraphics.drawString(font, getCountText(material), rowX + ICON_SIZE + ICON_GAP, y + 4, getCountColor(material), false);
                y += ROW_HEIGHT;
            }
        }

        private String getCountText(MultiblockBuild.Material material) {
            return material.have() + "/" + material.need();
        }

        private int getCountColor(MultiblockBuild.Material material) {
            return material.have() >= material.need() ? 0xFFFFFF : 0xFF5555;
        }
    }
}
