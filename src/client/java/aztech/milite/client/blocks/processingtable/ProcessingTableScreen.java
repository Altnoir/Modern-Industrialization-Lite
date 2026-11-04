package aztech.milite.client.blocks.processingtable;

import aztech.milite.MI;
import aztech.milite.blocks.processingtable.ProcessingTableScreenHandler;
import aztech.milite.client.screen.MIHandledScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class ProcessingTableScreen extends MIHandledScreen<ProcessingTableScreenHandler> {
    public static final ResourceLocation PROCESSING_TABLE_GUI = MI.id("textures/gui/container/processing_table.png");

    public static final int ARROW_CLICK_WIDTH = 20, ARROW_CLICK_HEIGHT = 16;
    private static final int SLOT_U = 14, SLOT_V = 46, SLOT_SIZE = 18;
    private static final int BLOCKED_ARROW_U = 176, BLOCKED_ARROW_V = 0, BLOCKED_ARROW_WIDTH = 26, BLOCKED_ARROW_HEIGHT = 21;
    private static final int ARROW_X = 103, ARROW_Y = 48;
    public static final int ARROW_CLICK_X = ARROW_X, ARROW_CLICK_Y = ARROW_Y;
    private static final int BLOCKED_ARROW_OFFSET_X = -3, BLOCKED_ARROW_OFFSET_Y = -3;

    public ProcessingTableScreen(ProcessingTableScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.titleLabelX = 43;
        this.titleLabelY = 18;
        this.imageHeight = 184;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public boolean isOverArrow(double mouseX, double mouseY) {
        double x = mouseX - this.leftPos;
        double y = mouseY - this.topPos;
        return x >= ARROW_CLICK_X && x < ARROW_CLICK_X + ARROW_CLICK_WIDTH && y >= ARROW_CLICK_Y
                && y < ARROW_CLICK_Y + ARROW_CLICK_HEIGHT;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(PROCESSING_TABLE_GUI, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        for (Slot slot : this.menu.slots) {
            if (slot.index >= ProcessingTableScreenHandler.FIRST_GRID_INPUT_INDEX
                    && slot.index <= ProcessingTableScreenHandler.LAST_GRID_INPUT_INDEX
                    && slot.isActive()) {
                guiGraphics.blit(PROCESSING_TABLE_GUI, this.leftPos + slot.x - 1, this.topPos + slot.y - 1,
                        SLOT_U, SLOT_V, SLOT_SIZE, SLOT_SIZE);
            }
        }
        if (this.menu.isCraftBlocked()) {
            guiGraphics.blit(PROCESSING_TABLE_GUI, this.leftPos + ARROW_X + BLOCKED_ARROW_OFFSET_X,
                    this.topPos + ARROW_Y + BLOCKED_ARROW_OFFSET_Y, BLOCKED_ARROW_U, BLOCKED_ARROW_V,
                    BLOCKED_ARROW_WIDTH, BLOCKED_ARROW_HEIGHT);
        }
    }
}
