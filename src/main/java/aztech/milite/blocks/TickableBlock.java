package aztech.milite.blocks;

import aztech.milite.util.Tickable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public interface TickableBlock extends EntityBlock {
    @Override
    default @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return (w, p, s, be) -> {
            if (be instanceof Tickable) {
                ((Tickable) be).tick();
            }
        };
    }
}
