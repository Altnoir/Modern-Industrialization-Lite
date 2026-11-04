package aztech.milite.machines.helper;

import aztech.milite.MIBlock;
import aztech.milite.machines.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public final class OverclockUnitHelper {
    private static final int REACH = 1;

    private OverclockUnitHelper() {}

    public static int countAround(Level level, BlockPos pos) {
        return countAround(level, pos, null, false);
    }

    public static void rescanMachinesAround(Level level, BlockPos pos, boolean unitPresent) {
        BlockPos unitPos = pos.immutable();
        for (BlockPos neighbour : touching(pos)) {
            if (level.getBlockEntity(neighbour) instanceof MachineBlockEntity machine) {
                machine.setOverclockerUnits(countAround(level, neighbour.immutable(), unitPos, unitPresent));
            }
        }
    }

    private static int countAround(Level level, BlockPos centre, @Nullable BlockPos changedPos, boolean changedIsUnit) {
        int count = 0;
        for (BlockPos neighbour : touching(centre)) {
            boolean isUnit = neighbour.equals(changedPos) ? changedIsUnit
                    : level.getBlockState(neighbour).is(MIBlock.OVERCLOCKER_UNIT.get());
            if (isUnit) {
                count++;
            }
        }
        return count;
    }

    private static Iterable<BlockPos> touching(BlockPos pos) {
        return BlockPos.betweenClosed(pos.offset(-REACH, -REACH, -REACH), pos.offset(REACH, REACH, REACH));
    }
}
