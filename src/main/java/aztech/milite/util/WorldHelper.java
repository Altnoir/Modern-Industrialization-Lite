package aztech.milite.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class WorldHelper {
    public static void forceChunkRemesh(Level world, BlockPos pos) {
        world.sendBlockUpdated(pos, null, null, 0);
    }
}
