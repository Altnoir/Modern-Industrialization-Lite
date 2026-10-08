package aztech.milite.blocks.creativestorageunit;

import aztech.milite.blocks.TickableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class CreativeStorageUnitBlock extends Block implements TickableBlock {
    public CreativeStorageUnitBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeStorageUnitBlockEntity(pos, state);
    }
}
