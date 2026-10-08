package aztech.milite.blocks.storage.barrel;

import aztech.milite.MIRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeBarrelBlockEntity extends BarrelBlockEntity {
    public CreativeBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(MIRegistries.CREATIVE_BARREL_BE.get(), pos, state);
    }
}
