package aztech.milite.client.machines.guicomponents;

import aztech.milite.client.machines.gui.ClientComponentRenderer;
import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.client.util.RenderHelper;
import aztech.milite.machines.blockentities.multiblocks.LargeTankMultiblockBlockEntity;
import aztech.milite.machines.guicomponents.LargeTankFluidDisplay;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.util.FluidHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Unit;

import java.util.List;
import java.util.Optional;

public class LargeTankFluidDisplayClient extends GuiComponentClient<Unit, LargeTankFluidDisplay.Data> {
    private static final int TANK_WIDTH = 46;
    private static final int TANK_HEIGHT = 62;
    private static final int TANK_Y = 12;
    private static final int BUTTON_WIDTH = 46;
    private static final int BUTTON_HEIGHT = 12;
    private static final int BUTTON_SPACING = 2;

    public LargeTankFluidDisplayClient(Unit params, LargeTankFluidDisplay.Data data) {
        super(params, data);
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        int posX = (machineScreen.getGuiParams().backgroundWidth - TANK_WIDTH) / 2;

        return new ClientComponentRenderer() {
            @Override
            public void addButtons(ButtonContainer container) {
                var shapeSelection = machineScreen.getMenu().getComponent(ShapeSelectionClient.class);
                if (shapeSelection == null) {
                    return;
                }
                var renderer = shapeSelection.getRenderer();
                int lineCount = renderer.getLineCount();
                if (lineCount < 2) {
                    return;
                }

                int buttonX = posX + TANK_WIDTH + 4;
                int buttonY = TANK_Y + (TANK_HEIGHT - lineCount * BUTTON_HEIGHT - (lineCount - 1) * BUTTON_SPACING) / 2;
                for (int i = 0; i < lineCount; i++) {
                    int line = i;
                    container.addButton(buttonX, buttonY + i * (BUTTON_HEIGHT + BUTTON_SPACING), BUTTON_WIDTH, BUTTON_HEIGHT,
                            syncId -> renderer.setLineIndex(line),
                            () -> List.of(),
                            (screen, button, guiGraphics, mouseX, mouseY, partialTicks) -> {
                                MachineScreen.blitStretched(guiGraphics, button.getX(), button.getY(), button.getWidth(), button.getHeight(),
                                        54, 6, 2, 14);
                                if (renderer.getLineIndex() == line) {
                                    MachineScreen.blitStretched(guiGraphics, button.getX(), button.getY(), button.getWidth(), button.getHeight(),
                                            70, 18, 2, 14);
                                }
                                var font = Minecraft.getInstance().font;
                                var text = renderer.getLineText(line);
                                guiGraphics.drawString(font, text, button.getX() + (button.getWidth() - font.width(text)) / 2, button.getY() + 2,
                                        0xFFFFFF, true);
                            });
                }
            }

            @Override
            public void renderBackground(GuiGraphics guiGraphics, int leftPos, int topPos) {
                FluidVariant fluid = data.fluid();
                float fracFull = (float) data.amount() / data.capacity();

                guiGraphics.blit(MachineScreen.SLOT_ATLAS, leftPos + posX, topPos + TANK_Y, 92, 38, TANK_WIDTH, TANK_HEIGHT);

                RenderSystem.disableBlend();
                if (!fluid.isBlank()) {
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 3; j++) {
                            float localFullness = Math.min(Math.max(3 * fracFull - (2 - j), 0), 1);
                            RenderHelper.drawFluidInGui(guiGraphics, fluid, leftPos + posX + 7 + i * 16,
                                    topPos + TANK_Y + 7 + j * 16 + (1 - localFullness) * 16, 16, localFullness);
                        }
                    }
                }
                RenderSystem.enableBlend();

                guiGraphics.blit(MachineScreen.SLOT_ATLAS, leftPos + posX + 7, topPos + TANK_Y + 7, 60, 38, 32, 48);
            }

            @Override
            public boolean renderTooltip(MachineScreen screen, Font font, GuiGraphics guiGraphics, int x, int y, int cursorX, int cursorY) {
                if (RenderHelper.isPointWithinRectangle(posX + 7, TANK_Y + 7, 32, 48, cursorX - x, cursorY - y)) {
                    guiGraphics.renderTooltip(font,
                            FluidHelper.getTooltipForFluidStorage(data.fluid(), data.amount(), data.capacity()),
                            Optional.empty(),
                            cursorX, cursorY);
                    return true;
                }

                var shapeSelection = screen.getMenu().getComponent(ShapeSelectionClient.class);
                if (shapeSelection != null && shapeSelection.getRenderer().isRowHovered(x, y, cursorX, cursorY)) {
                    var selectedShape = shapeSelection.getData();
                    long capacity = LargeTankMultiblockBlockEntity.getCapacityFromComponents(
                            selectedShape.get(0), selectedShape.get(1), selectedShape.get(2));
                    guiGraphics.renderTooltip(font, List.of(FluidHelper.getFluidAmountLarge(capacity)), Optional.empty(), cursorX, cursorY);
                    return true;
                }
                return false;
            }
        };
    }
}
