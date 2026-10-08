package aztech.milite.blocks.forgehammer;

import aztech.milite.MIBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ForgeHammerBlock extends Block {
    private VoxelShape shape;
    private final int[] part_height = { 4, 1, 5, 5 };
    private final int[] part_width = { 14, 10, 8, 14 };

    public ForgeHammerBlock(Properties properties) {
        super(properties);

        VoxelShape[] parts = new VoxelShape[part_height.length];
        float currentY = 0;
        for (int i = 0; i < part_height.length; i++) {
            float o = (16 - part_width[i]) / 32.0f;
            float e = o + part_width[i] / 16.0f;
            parts[i] = Shapes.box(o, currentY, o, e, currentY + part_height[i] / 16.0f, e);
            currentY += part_height[i] / 16.0f;
        }
        shape = parts[0];
        for (int i = 1; i < part_height.length; i++) {
            shape = Shapes.or(shape, parts[i]);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(new MenuProvider() {
                @Override
                public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                    return new ForgeHammerScreenHandler(syncId, inv, ContainerLevelAccess.create(world, pos));
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable(MIBlock.FORGE_HAMMER.asBlock().getDescriptionId());
                }
            });
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}
