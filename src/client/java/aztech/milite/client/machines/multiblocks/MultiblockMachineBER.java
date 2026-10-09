package aztech.milite.client.machines.multiblocks;

import aztech.milite.client.machines.MachineBlockEntityRenderer;
import aztech.milite.config.MIClientConfig;
import aztech.milite.machines.MachineBlock;
import aztech.milite.machines.multiblocks.HatchBlockEntity;
import aztech.milite.machines.multiblocks.HatchFlags;
import aztech.milite.machines.multiblocks.HatchType;
import aztech.milite.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.milite.machines.multiblocks.ShapeMatcher;
import aztech.milite.MITags;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

public class MultiblockMachineBER extends MachineBlockEntityRenderer<MultiblockMachineBlockEntity> {
    public MultiblockMachineBER(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(MultiblockMachineBlockEntity be, float tickDelta, PoseStack matrices, MultiBufferSource vcp, int light, int overlay) {
        super.render(be, tickDelta, matrices, vcp, light, overlay);

        int maxHighlightDistance = MIClientConfig.INSTANCE.maxMultiblockHighlightOverlayRange.getAsInt();
        if (maxHighlightDistance == 0) {
            return;
        }

        boolean drawHighlights = isHoldingWrench() && !be.isShapeValid();
        HatchType hatchType = getHeldHatchType();
        if (drawHighlights || hatchType != null) {
            ShapeMatcher matcher = be.createShapeMatcher();
            var player = Minecraft.getInstance().player;

            for (BlockPos pos : matcher.getPositions()) {
                if (player != null && player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > maxHighlightDistance * maxHighlightDistance) {
                    // Skip blocks that are far from the player to mitigate FPS drops.
                    continue;
                }

                HatchFlags hatchFlag = matcher.getHatchFlags(pos);
                if (hatchType != null) {
                    if (MIClientConfig.INSTANCE.hatchPlacementOverlay.getAsBoolean() && hatchFlag != null && hatchFlag.allows(hatchType)) {
                        // Highlight placeable hatches in green
                        MultiblockHighlight.enqueueHatchHighlight(pos);
                    }
                }
                if (drawHighlights) {
                    if (!matcher.matches(pos, be.getLevel())) {
                        var existingState = be.getLevel().getBlockState(pos);
                        if (existingState.isAir() || /* approximate check for e.g. grass and snow */ existingState.canBeReplaced()) {
                            var member = matcher.getSimpleMember(pos);
                            var state = ShapeMatcher.toWorldState(be.getLevel(), pos, member.getPreviewState(), matcher.controllerDirection);
                            var blockEntity = member.newBlockEntity(be.getLevel().registryAccess(), be.getLevel(), pos, state);
                            MultiblockHighlight.enqueueErrorHighlight(pos, state, blockEntity);
                        } else {
                            // Enqueue red cube
                            MultiblockHighlight.enqueueErrorHighlight(pos, null, null);
                        }
                    }
                }
            }
        }
    }

    private static boolean isHoldingWrench() {
        Player player = Minecraft.getInstance().player;
        return player != null && (player.getMainHandItem().is(MITags.WRENCHES) || player.getOffhandItem().is(MITags.WRENCHES));
    }

    @Nullable
    private static HatchType getHeldHatchType() {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return null;
        }
        HatchType mainHand = getHatchType(player.getMainHandItem());
        HatchType offHand = getHatchType(player.getOffhandItem());
        return mainHand == null ? offHand : mainHand;
    }

    @Nullable
    private static HatchType getHatchType(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof MachineBlock block) {
                BlockEntity be = block.newBlockEntity(new BlockPos(0, 0, 0), block.defaultBlockState());
                if (be instanceof HatchBlockEntity hatch) {
                    return hatch.getHatchType();
                }
            }
        }
        return null;
    }

    @Override
    public boolean shouldRenderOffScreen(MultiblockMachineBlockEntity pBlockEntity) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(MultiblockMachineBlockEntity blockEntity) {
        return AABB.INFINITE;
    }
}
