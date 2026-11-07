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
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(3, 5, 3, 13, 11, 13),
            Block.box(1, 0, 1, 15, 5, 15),
            Block.box(0, 11, 0, 16, 16, 16),
            Block.box(11, 0, 0, 16, 6, 5),
            Block.box(0, 0, 0, 5, 6, 5),
            Block.box(11, 0, 11, 16, 6, 16),
            Block.box(0, 0, 11, 5, 6, 16));

    public ForgeHammerBlock(Properties properties) {
        super(properties);
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
        return SHAPE;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}
