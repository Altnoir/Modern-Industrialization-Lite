package aztech.milite.blocks.processingtable;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The Processing Table, an early game crafting station used to turn a machine casing and a characterising item into a
 * machine.
 */
public class ProcessingTableBlock extends Block {
    public ProcessingTableBlock(Properties properties) {
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
                    return new ProcessingTableScreenHandler(syncId, inv, ContainerLevelAccess.create(world, pos));
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable(MIBlock.PROCESSING_TABLE.asBlock().getDescriptionId());
                }
            });
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}
