package aztech.milite.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * BEP = Block entity parameters, because Mojang added too many of them for us
 * to type them all by hand.
 */
public record BEP(BlockEntityType<?> type, BlockPos pos, BlockState state) {}
