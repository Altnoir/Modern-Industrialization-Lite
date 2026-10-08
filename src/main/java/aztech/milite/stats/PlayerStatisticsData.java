package aztech.milite.stats;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerStatisticsData extends SavedData {
    private final Map<UUID, PlayerStatistics> stats = new HashMap<>();
    private final Function<UUID, PlayerStatistics> statsFactory = uuid -> new PlayerStatistics(this, uuid);

    private PlayerStatisticsData(CompoundTag tag, HolderLookup.Provider registries) {
        for (var key : tag.getAllKeys()) {
            var uuid = UUID.fromString(key);
            stats.put(uuid, new PlayerStatistics(this, uuid, tag.getCompound(key)));
        }
    }

    private PlayerStatisticsData() {}

    public PlayerStatistics get(UUID uuid) {
        Objects.requireNonNull(uuid);
        return stats.computeIfAbsent(uuid, statsFactory);
    }

    public PlayerStatistics get(Player player) {
        return get(player.getUUID());
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        for (var entry : stats.entrySet()) {
            tag.put(entry.getKey().toString(), entry.getValue().toTag());
        }
        return tag;
    }

    private static final String NAME = "milite_player_stats";
    private static final Factory<PlayerStatisticsData> FACTORY = new Factory<>(PlayerStatisticsData::new, PlayerStatisticsData::new);

    public static PlayerStatisticsData get(MinecraftServer server) {
        var overworld = server.getLevel(ServerLevel.OVERWORLD);
        Objects.requireNonNull(overworld, "Couldn't find overworld");
        return overworld.getDataStorage().computeIfAbsent(FACTORY, NAME);
    }
}
