package aztech.milite.client.machines.guicomponents;

import aztech.milite.client.machines.gui.ClientComponentRenderer;
import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.inventory.AbstractConfigurableStack;
import aztech.milite.inventory.MIInventory;
import aztech.milite.inventory.SlotPositions;
import aztech.milite.machines.guicomponents.MachineProblemsDisplay;
import aztech.milite.MI;
import aztech.milite.MIText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class MachineProblemsDisplayClient extends GuiComponentClient<Unit, MachineProblemsDisplay.Data> {
    private static final ResourceLocation SLOT_PROBLEM = MI.id("textures/gui/container/slot_problem.png");

    @Nullable
    private MIInventory inventory;

    public MachineProblemsDisplayClient(Unit params, MachineProblemsDisplay.Data data) {
        super(params, data);
    }

    @Override
    public void setupMenu(MenuFacade menu) {
        this.inventory = menu.getMachineInventory();
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        return new Renderer();
    }

    public class Renderer implements ClientComponentRenderer {
        private static void renderSlotProblems(GuiGraphics guiGraphics, int x, int y, List<? extends AbstractConfigurableStack> stacks,
                SlotPositions positions) {
            for (int index = 0; index < positions.size(); index++) {
                // Only display the border on output slots
                if (!stacks.get(index).canPlayerInsert()) {
                    int sx = positions.getX(index);
                    int sy = positions.getY(index);
                    int px = x + sx - 2;
                    int py = y + sy - 2;
                    guiGraphics.blit(SLOT_PROBLEM, px, py, sx, sy, 20, 20, 20, 20);
                }
            }
        }

        private static boolean renderSlotProblemTooltip(Font font, GuiGraphics guiGraphics, int x, int y,
                List<? extends AbstractConfigurableStack> stacks, SlotPositions positions, int cursorX, int cursorY) {
            for (int index = 0; index < positions.size(); index++) {
                // Only display tooltips on output slots
                if (!stacks.get(index).canPlayerInsert()) {
                    int sx = positions.getX(index);
                    int sy = positions.getY(index);
                    int px = x + sx - 1;
                    int py = y + sy - 1;
                    if (cursorX >= px && cursorX < px + 18 && cursorY >= py && cursorY < py + 18) {
                        guiGraphics.renderComponentTooltip(font, List.of(MIText.MachineMultipleRecipes1.text().withStyle(ChatFormatting.RED),
                                MIText.MachineMultipleRecipes2.text().withStyle(ChatFormatting.RED)), cursorX, cursorY);
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void renderBackground(GuiGraphics guiGraphics, int x, int y) {
            if (inventory != null && data.hasProblems()) {
                renderSlotProblems(guiGraphics, x, y, inventory.getItemStacks(), inventory.itemPositions);
                renderSlotProblems(guiGraphics, x, y, inventory.getFluidStacks(), inventory.fluidPositions);
            }
        }

        @Override
        public boolean renderTooltip(MachineScreen screen, Font font, GuiGraphics guiGraphics, int leftPos, int topPos, int cursorX, int cursorY) {
            if (inventory != null && data.hasProblems()) {
                return renderSlotProblemTooltip(font, guiGraphics, leftPos, topPos, inventory.getItemStacks(), inventory.itemPositions, cursorX,
                        cursorY) ||
                        renderSlotProblemTooltip(font, guiGraphics, leftPos, topPos, inventory.getFluidStacks(), inventory.fluidPositions, cursorX,
                                cursorY);
            }
            return false;
        }
    }
}
