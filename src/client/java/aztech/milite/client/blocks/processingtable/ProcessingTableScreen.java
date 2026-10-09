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

    /**
     * The slot sized X drawn over the locked side slots, and the crossed out arrow drawn over the background arrow.
     */
    private static final int LOCKED_SLOT_U = 176, LOCKED_SLOT_V = 21, SLOT_SIZE = 18;
    private static final int BLOCKED_ARROW_U = 179, BLOCKED_ARROW_V = 3, BLOCKED_ARROW_WIDTH = 37, BLOCKED_ARROW_HEIGHT = 15;
    private static final int ARROW_X = 87, ARROW_Y = 48;

    public ProcessingTableScreen(ProcessingTableScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.titleLabelX = 54;
        this.titleLabelY = 15;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(PROCESSING_TABLE_GUI, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        if (this.menu.isCraftBlocked()) {
            guiGraphics.blit(PROCESSING_TABLE_GUI, this.leftPos + ARROW_X, this.topPos + ARROW_Y, BLOCKED_ARROW_U, BLOCKED_ARROW_V, BLOCKED_ARROW_WIDTH, BLOCKED_ARROW_HEIGHT);
        }
    }

    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        super.renderSlot(guiGraphics, slot);
        // The slots are drawn with the pose already moved to the top left corner of the screen, so the raw slot
        // position is what the X has to be drawn at. Drawing it after super keeps it over the slot background.
        if (!this.menu.areSideSlotsUnlocked() && (slot == this.menu.leftInput || slot == this.menu.rightInput)) {
            guiGraphics.blit(PROCESSING_TABLE_GUI, slot.x - 1, slot.y - 1, LOCKED_SLOT_U, LOCKED_SLOT_V, SLOT_SIZE, SLOT_SIZE);
        }
    }
}
