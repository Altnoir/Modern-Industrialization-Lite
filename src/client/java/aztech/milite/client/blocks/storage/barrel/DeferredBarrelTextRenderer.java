package aztech.milite.client.blocks.storage.barrel;

import aztech.milite.blocks.storage.barrel.BarrelBlockEntity;
import aztech.milite.client.util.RenderHelper;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

public class DeferredBarrelTextRenderer {
    private record Entry(BlockPos pos, int sideMask, int itemNameColor) {}

    private static final List<Entry> barrelsToRender = new ArrayList<>();
    private static final MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(new ByteBufferBuilder(128));

    public static void init() {
        NeoForge.EVENT_BUS.addListener(DeferredBarrelTextRenderer::render);
    }

    public static void enqueueBarrelForRendering(BlockPos pos, int sideMask, int itemNameColor) {
        barrelsToRender.add(new Entry(pos.immutable(), sideMask, itemNameColor));
    }

    private static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            return;
        }
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        var matrices = event.getPoseStack();
        matrices.pushPose();
        var camPos = event.getCamera().getPosition();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);

        for (var entry : barrelsToRender) {
            var pos = entry.pos();
            int sideMask = entry.sideMask();

            if (!(level.getBlockEntity(pos) instanceof BarrelBlockEntity entity)) {
                continue;
            }

            matrices.pushPose();
            matrices.translate(pos.getX(), pos.getY(), pos.getZ());

            String amount;
            if (entity.behaviour.isCreative()) {
                amount = "∞";
            } else {
                amount = String.valueOf(entity.getAmount());
            }
            ItemStack toRender = entity.getResource().toStack();

            for (int i = 0; i < 4; ++i) {
                if ((sideMask & (1 << i)) == 0) {
                    continue;
                }

                Font textRenderer = Minecraft.getInstance().font;
                String itemName = toRender.getHoverName().getString();
                matrices.pushPose();
                matrices.translate(0.5, 1.14, 0.5);
                matrices.mulPose(Axis.YP.rotationDegrees((2 - i) * 90F));
                matrices.translate(0, 0.15, -0.505);
                matrices.scale(-0.01f, -0.01F, -0.01f);

                // Adjust width
                final int maxWidth = 100;
                if (textRenderer.width(itemName) > maxWidth) {
                    itemName = textRenderer.plainSubstrByWidth(itemName, maxWidth - textRenderer.width("...")) + "...";
                }

                float xPosition = (float) (-textRenderer.width(itemName) / 2);
                textRenderer.drawInBatch(itemName, xPosition, -4f + 40, entry.itemNameColor(), false, matrices.last().pose(), immediate,
                        Font.DisplayMode.NORMAL, 0, RenderHelper.FULL_LIGHT);

                matrices.popPose();

                matrices.pushPose();
                matrices.translate(0.5, 0.5, 0.5);
                matrices.mulPose(Axis.YP.rotationDegrees((2 - i) * 90F));
                matrices.translate(0, 0.0875, -0.505);
                matrices.scale(-0.01f, -0.01F, -0.01f);

                xPosition = (float) (-textRenderer.width(amount) / 2);
                textRenderer.drawInBatch(amount, xPosition, -4f + 40, 0x000000, false, matrices.last().pose(), immediate,
                        Font.DisplayMode.NORMAL, 0, RenderHelper.FULL_LIGHT);

                matrices.popPose();
            }

            matrices.popPose();
        }

        matrices.popPose();

        barrelsToRender.clear();
        immediate.endBatch();
    }
}
