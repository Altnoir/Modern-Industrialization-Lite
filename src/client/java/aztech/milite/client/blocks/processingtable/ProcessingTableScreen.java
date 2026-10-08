package aztech.milite.client.blocks.processingtable;

import aztech.milite.blocks.processingtable.ProcessingTableScreenHandler;
import aztech.milite.client.screen.MIHandledScreen;
import aztech.milite.MI;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ProcessingTableScreen extends MIHandledScreen<ProcessingTableScreenHandler> {
    public static final ResourceLocation PROCESSING_TABLE_GUI = MI.id("textures/gui/container/processing_table.png");

    public ProcessingTableScreen(ProcessingTableScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
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
    }
}
