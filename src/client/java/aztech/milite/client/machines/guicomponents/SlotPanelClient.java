package aztech.milite.client.machines.guicomponents;

import aztech.milite.client.machines.gui.ClientComponentRenderer;
import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.inventory.BackgroundRenderedSlot;
import aztech.milite.machines.guicomponents.SlotPanel;
import aztech.milite.MITooltips;
import aztech.milite.util.Rectangle;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;

import java.util.List;

public class SlotPanelClient extends GuiComponentClient<List<SlotPanel.SlotType>, Unit> {
    public SlotPanelClient(List<SlotPanel.SlotType> params, Unit data) {
        super(params, data);
    }

    @Override
    public void setupMenu(MenuFacade menu) {
        for (int i = 0; i < params.size(); ++i) {
            var type = params.get(i);

            class ClientSlot extends SlotWithBackground implements SlotTooltip {
                public ClientSlot(int i) {
                    super(new SimpleContainer(1), 0, SlotPanel.getSlotX(menu.getGuiParams()), SlotPanel.getSlotY(i));
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return type.mayPlace(stack);
                }

                @Override
                public int getMaxStackSize() {
                    return type.slotLimit;
                }

                @Override
                public int getBackgroundU() {
                    return !hasItem() ? type.u : 0;
                }

                @Override
                public int getBackgroundV() {
                    return !hasItem() ? type.v : 0;
                }

                @Override
                public Component getTooltip() {
                    return MITooltips.line(type.tooltip).build();
                }
            }

            menu.addSlotToMenu(new ClientSlot(i), type.group);
        }
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        return new ClientComponentRenderer() {
            private Rectangle getBox(int leftPos, int topPos) {
                return new Rectangle(leftPos + machineScreen.getGuiParams().backgroundWidth, topPos + 10, 31, 14 + 20 * params.size());
            }

            @Override
            public void addExtraBoxes(List<Rectangle> rectangles, int leftPos, int topPos) {
                rectangles.add(getBox(leftPos, topPos));
            }

            @Override
            public void renderBackground(GuiGraphics guiGraphics, int x, int y) {
                var box = getBox(x, y);

                int textureX = box.x() - x - box.w();
                guiGraphics.blit(MachineScreen.BACKGROUND, box.x(), box.y(), textureX, 0, box.w(), box.h() - 4);
                guiGraphics.blit(MachineScreen.BACKGROUND, box.x(), box.y() + box.h() - 4, textureX, 252, box.w(), 4);
            }

            @Override
            public boolean renderTooltip(MachineScreen screen, Font font, GuiGraphics guiGraphics, int x, int y, int cursorX, int cursorY) {
                if (screen.getFocusedSlot() instanceof SlotTooltip st && !screen.getFocusedSlot().hasItem()) {
                    guiGraphics.renderTooltip(font, st.getTooltip(), cursorX, cursorY);
                    return true;
                }
                return false;
            }
        };
    }

    interface SlotTooltip {
        Component getTooltip();
    }

    public static class SlotWithBackground extends Slot implements BackgroundRenderedSlot {
        public SlotWithBackground(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }
    }
}
