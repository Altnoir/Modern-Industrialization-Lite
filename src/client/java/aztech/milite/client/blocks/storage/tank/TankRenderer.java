package aztech.milite.client.blocks.storage.tank;

import aztech.milite.blocks.storage.tank.AbstractTankBlockEntity;
import aztech.milite.client.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;

public class TankRenderer implements BlockEntityRenderer<AbstractTankBlockEntity> {
    private final int lockIconColor;

    public TankRenderer(int lockIconColor) {
        this.lockIconColor = lockIconColor;
    }

    @Override
    public void render(AbstractTankBlockEntity tank,
            float tickDelta,
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            int light, int overlay) {
        if (!tank.getResource().isBlank()) {
            if (tank.behaviour.isCreative()) {
                RenderHelper.drawFluidInTank(tank, matrices, vertexConsumers, tank.getResource(), 1);
            } else {
                if (tank.getAmount() > 0) {
                    RenderHelper.drawFluidInTank(tank, matrices, vertexConsumers, tank.getResource(), (float) tank.getAmount() / tank.getCapacity());
                } else if (tank.isLocked()) {
                    RenderHelper.drawFluidInTank(tank, matrices, vertexConsumers, tank.getResource(), 0.01f);
                }
            }
        }

        if (tank.isLocked()) {
            RenderHelper.drawLockedTexture(tank, matrices, vertexConsumers, lockIconColor);
        }
    }
}
