package aztech.milite.client.blocks.storage.barrel;

import aztech.milite.blocks.storage.barrel.BarrelBlockEntity;
import aztech.milite.client.util.RenderHelper;
import aztech.milite.config.MIClientConfig;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BarrelRenderer implements BlockEntityRenderer<BarrelBlockEntity> {
    private static final ThreadLocal<Integer> barrelNesting = ThreadLocal.withInitial(() -> 0);

    private final int itemNameColor;

    public BarrelRenderer(int itemNameColor) {
        this.itemNameColor = itemNameColor;
    }

    @Override
    public void render(BarrelBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers,
            int light, int overlay) {
        if (entity.isLocked()) {
            RenderHelper.drawLockedTexture(entity, matrices, vertexConsumers, itemNameColor);
        }

        if (!MIClientConfig.INSTANCE.barrelContentRendering.getAsBoolean()) {
            return;
        }
        int nesting = barrelNesting.get();
        if (nesting >= 4) {
            return;
        }

        barrelNesting.set(nesting + 1);

        try {
            var state = entity.getBlockState();
            var pos = entity.getBlockPos();

            ItemVariant item = entity.getResource();

            if (!item.isBlank()) {
                ItemStack toRender = item.toStack();

                int sideMask = 0;

                for (int i = 0; i < 4; i++) {
                    var direction = Direction.from2DDataValue(i);
                    // Note: level can be null from builtin item renderer
                    if (entity.getLevel() != null
                            && !Block.shouldRenderFace(state, entity.getLevel(), pos, direction, pos.relative(direction))) {
                        continue;
                    }

                    sideMask |= 1 << i;
                    // Thanks TechReborn for rendering code

                    matrices.pushPose();
                    matrices.translate(0.5, 0, 0.5);
                    matrices.mulPose(Axis.YP.rotationDegrees(-i * 90F));
                    matrices.scale(0.5F, 0.5F, 0.5F);
                    matrices.translate(0, 1.125, 1.01);

                    matrices.last().pose().scale(1, 1, 0.01f);
                    matrices.last().normal().rotate(Mth.HALF_PI / 2, -1, 0, 0);

                    Minecraft.getInstance().getItemRenderer().renderStatic(toRender, ItemDisplayContext.GUI, RenderHelper.FULL_LIGHT,
                            OverlayTexture.NO_OVERLAY, matrices, vertexConsumers, entity.getLevel(), 0);

                    matrices.popPose();
                }

                DeferredBarrelTextRenderer.enqueueBarrelForRendering(pos, sideMask, itemNameColor);
            }
        } finally {
            barrelNesting.set(nesting);
        }
    }
}
