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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Unit;

import java.util.Objects;
import java.util.Optional;

public class LargeTankFluidDisplayClient extends GuiComponentClient<Unit, LargeTankFluidDisplay.Data> {
    public LargeTankFluidDisplayClient(Unit params, LargeTankFluidDisplay.Data data) {
        super(params, data);
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        return new ClientComponentRenderer() {
            private static final int posX = 70, posY = 12;

            @Override
            public void renderBackground(GuiGraphics guiGraphics, int leftPos, int topPos) {
                FluidVariant fluid = data.fluid();
                float fracFull = (float) data.amount() / data.capacity();

                guiGraphics.blit(MachineScreen.SLOT_ATLAS, leftPos + posX, topPos + posY, 92, 38, 46, 62);

                RenderSystem.disableBlend();
                if (!fluid.isBlank()) {
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 3; j++) {
                            float localFullness = Math.min(Math.max(3 * fracFull - (2 - j), 0), 1);
                            RenderHelper.drawFluidInGui(guiGraphics, fluid, leftPos + posX + 7 + i * 16,
                                    topPos + posY + 7 + j * 16 + (1 - localFullness) * 16, 16, localFullness);
                        }
                    }
                }
                RenderSystem.enableBlend();

                guiGraphics.blit(MachineScreen.SLOT_ATLAS, leftPos + posX + 7, topPos + posY + 7, 60, 38, 32, 48);

                // A bit hacky: draw the capacity corresponding to the shape in the shape selection GUI if it's open. ;)
                var shapeSelection = machineScreen.getMenu().getComponent(ShapeSelectionClient.class);
                var renderer = Objects.requireNonNull(shapeSelection).getRenderer();
                if (renderer.isPanelOpen) {
                    var shapePanelBox = renderer.getBox(leftPos, topPos);
                    var selectedShape = shapeSelection.getData();
                    long capacity = LargeTankMultiblockBlockEntity.getCapacityFromComponents(
                            selectedShape.get(0), selectedShape.get(1), selectedShape.get(2));
                    var capacityText = FluidHelper.getFluidAmountLarge(capacity);

                    guiGraphics.drawString(Minecraft.getInstance().font, capacityText, shapePanelBox.x() + 14, shapePanelBox.y() + 14, 0x404040,
                            false);
                }
            }

            @Override
            public boolean renderTooltip(MachineScreen screen, Font font, GuiGraphics guiGraphics, int x, int y, int cursorX, int cursorY) {
                if (RenderHelper.isPointWithinRectangle(posX + 7, posY + 7, 32, 48, cursorX - x, cursorY - y)) {
                    guiGraphics.renderTooltip(font,
                            FluidHelper.getTooltipForFluidStorage(data.fluid(), data.amount(), data.capacity()),
                            Optional.empty(),
                            cursorX, cursorY);
                    return true;
                }
                return false;
            }
        };
    }
}
