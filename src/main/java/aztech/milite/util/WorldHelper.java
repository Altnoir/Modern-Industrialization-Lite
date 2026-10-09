package aztech.milite.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class WorldHelper {
    public static boolean isChunkTicking(ServerLevel world, long packedChunkPos) {
        return world.getChunkSource().isPositionTicking(packedChunkPos) && world.areEntitiesLoaded(packedChunkPos);
    }

    public static void forceChunkRemesh(Level world, BlockPos pos) {
        world.sendBlockUpdated(pos, null, null, 0);
    }
}
