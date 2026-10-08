package aztech.milite.machines.multiblocks.world;

import net.minecraft.core.BlockPos;

public interface ChunkEventListener {
    void onBlockUpdate(BlockPos pos);

    void onUnload();

    void onLoad();
}
