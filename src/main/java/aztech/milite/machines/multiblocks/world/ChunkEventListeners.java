package aztech.milite.machines.multiblocks.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class ChunkEventListeners {
    public static ChunkPosMultiMap<ChunkEventListener> listeners = new ChunkPosMultiMap<>();

    public static void init() {
        NeoForge.EVENT_BUS.addListener(ServerStoppedEvent.class, event -> serverStopCleanup());

        NeoForge.EVENT_BUS.addListener(ChunkEvent.Load.class, event -> {
            if (event.getLevel().isClientSide()) {
                return;
            }

            ensureServerThread(event.getLevel().getServer());
            Set<ChunkEventListener> cels = listeners.get(event.getLevel(), event.getChunk().getPos());
            if (cels != null) {
                for (ChunkEventListener cel : cels) {
                    cel.onLoad();
                }
            }
        });
        NeoForge.EVENT_BUS.addListener(ChunkEvent.Unload.class, event -> {
            if (event.getLevel().isClientSide()) {
                return;
            }

            ensureServerThread(event.getLevel().getServer());
            Set<ChunkEventListener> cels = listeners.get(event.getLevel(), event.getChunk().getPos());
            if (cels != null) {
                for (ChunkEventListener cel : cels) {
                    cel.onUnload();
                }
            }
        });
        NeoForge.EVENT_BUS.addListener(BlockEvent.NeighborNotifyEvent.class, event -> {
            if (event.getLevel() instanceof Level level) {
                onBlockStateChange(level, new ChunkPos(event.getPos()), event.getPos());
            }
        });
    }

    public static void onBlockStateChange(Level world, ChunkPos chunkPos, BlockPos pos) {
        // We skip block state changes that happen outside of the server thread.
        // Hopefully that won't cause problems.
        if (world instanceof ServerLevel serverLevel && serverLevel.getServer().isSameThread()) {
            Set<ChunkEventListener> cels = listeners.get(world, chunkPos);
            if (cels != null) {
                for (ChunkEventListener cel : cels) {
                    cel.onBlockUpdate(pos);
                }
            }
        }
    }

    private static void ensureServerThread(@Nullable MinecraftServer server) {
        if (server == null) {
            throw new RuntimeException("Null server!");
        }

        if (!server.isSameThread()) {
            throw new RuntimeException("Thread is not server thread!");
        }
    }

    private static void serverStopCleanup() {
        if (listeners.size() != 0) {
            listeners = new ChunkPosMultiMap<>();
        }
    }
}
