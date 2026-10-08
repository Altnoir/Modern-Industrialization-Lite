package aztech.milite.machines.multiblocks.world;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChunkPosMultiMap<T> {
    private final Map<LevelAccessor, HashMap<ChunkPos, Set<T>>> storage = new HashMap<>();

    public final void add(LevelAccessor world, ChunkPos chunkPos, T t) {
        storage.computeIfAbsent(world, w -> new HashMap<>()).computeIfAbsent(chunkPos, p -> new HashSet<>()).add(t);
    }

    public final void remove(LevelAccessor world, ChunkPos chunkPos, T t) {
        Map<ChunkPos, Set<T>> chunkPosMap = storage.get(world);
        Set<T> tSet = chunkPosMap.get(chunkPos);

        if (!tSet.remove(t)) {
            throw new RuntimeException("Could not remove element at position " + chunkPos + " as it does not exist.");
        }

        if (tSet.size() == 0) {
            chunkPosMap.remove(chunkPos);

            if (chunkPosMap.size() == 0) {
                storage.remove(world);
            }
        }
    }

    @Nullable
    public final Set<T> get(LevelAccessor world, ChunkPos chunkPos) {
        Map<ChunkPos, Set<T>> chunkPosSetMap = storage.get(world);
        if (chunkPosSetMap == null) {
            return null;
        }
        return chunkPosSetMap.get(chunkPos);
    }

    public final int size() {
        return storage.size();
    }
}
