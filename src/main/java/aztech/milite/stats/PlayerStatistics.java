package aztech.milite.stats;

import aztech.milite.compat.argonauts.ArgonautsFacade;
import aztech.milite.compat.ftbquests.FTBQuestsFacade;
import aztech.milite.compat.ftbteams.FTBTeamsFacade;
import com.google.common.primitives.Ints;
import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import it.unimi.dsi.fastutil.objects.Reference2LongOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class PlayerStatistics {
    public static final PlayerStatistics DUMMY = new PlayerStatistics(null, null);

    private final PlayerStatisticsData data;
    @Nullable
    private final UUID uuid;
    private final Map<Item, StatisticValue> usedItems = new IdentityHashMap<>(), producedItems = new IdentityHashMap<>();
    private final Map<Fluid, StatisticValue> usedFluids = new IdentityHashMap<>(), producedFluids = new IdentityHashMap<>();

    private static final Set<UUID> uuidCache = new HashSet<>();

    // Items produced while the player was offline... this is used to award vanilla stats when the player comes back online.
    private final Reference2LongMap<Item> pendingCraftedStats = new Reference2LongOpenHashMap<>();

    PlayerStatistics(PlayerStatisticsData data, UUID uuid) {
        this.data = data;
        this.uuid = uuid;
    }

    PlayerStatistics(PlayerStatisticsData data, UUID uuid, CompoundTag nbt) {
        this(data, uuid);
        readNbt(BuiltInRegistries.ITEM, usedItems, nbt.getCompound("usedItems"));
        readNbt(BuiltInRegistries.ITEM, producedItems, nbt.getCompound("producedItems"));
        readNbt(BuiltInRegistries.FLUID, usedFluids, nbt.getCompound("usedFluids"));
        readNbt(BuiltInRegistries.FLUID, producedFluids, nbt.getCompound("producedFluids"));
        pendingReadNbt(pendingCraftedStats, nbt.getCompound("pendingCraftedStats"));
    }

    public CompoundTag toTag() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("usedItems", toNbt(BuiltInRegistries.ITEM, usedItems));
        nbt.put("producedItems", toNbt(BuiltInRegistries.ITEM, producedItems));
        nbt.put("usedFluids", toNbt(BuiltInRegistries.FLUID, usedFluids));
        nbt.put("producedFluids", toNbt(BuiltInRegistries.FLUID, producedFluids));
        nbt.put("pendingCraftedStats", pendingToNbt(pendingCraftedStats));
        return nbt;
    }

    public void addUsedItems(ItemLike what, long amount) {
        usedItems.computeIfAbsent(what.asItem(), i -> new StatisticValue()).add(amount);
    }

    public void addProducedItems(Level level, ItemLike what, long amount) {
        var item = what.asItem();
        producedItems.computeIfAbsent(item, i -> new StatisticValue()).add(amount);

        if (uuid != null) {
            FTBQuestsFacade.INSTANCE.addCompleted(uuid, item, amount);

            awardStat(level, what, amount);

            // Make sure we only award the stats to other players once even if they have both FTB Teams and Argonauts.
            uuidCache.clear();
            uuidCache.addAll(FTBTeamsFacade.INSTANCE.getOtherPlayersInTeam(uuid));
            uuidCache.addAll(ArgonautsFacade.INSTANCE.getOtherPlayersInGuild(level, uuid));

            for (var uuid : uuidCache) {
                data.get(uuid).awardStat(level, what, amount);
            }
            uuidCache.clear();
        }
    }

    public void addUsedFluids(Fluid what, long amount) {
        usedFluids.computeIfAbsent(what, i -> new StatisticValue()).add(amount);
    }

    public void addProducedFluids(Fluid what, long amount) {
        producedFluids.computeIfAbsent(what, i -> new StatisticValue()).add(amount);
    }

    private void awardStat(Level level, ItemLike what, long amount) {
        Objects.requireNonNull(uuid);

        var player = level.getPlayerByUUID(uuid);

        if (player != null) {
            player.awardStat(Stats.ITEM_CRAFTED.get(what.asItem()), Ints.saturatedCast(amount));
        } else {
            pendingCraftedStats.mergeLong(what.asItem(), amount, Long::sum);
        }
    }

    public void onPlayerJoin(ServerPlayer player) {
        if (!pendingCraftedStats.isEmpty()) {
            for (var entry : pendingCraftedStats.reference2LongEntrySet()) {
                player.awardStat(Stats.ITEM_CRAFTED.get(entry.getKey()), Ints.saturatedCast(entry.getLongValue()));
            }

            pendingCraftedStats.clear();
        }
    }

    private static <T> void readNbt(Registry<T> registry, Map<T, StatisticValue> map, CompoundTag tag) {
        for (var key : tag.getAllKeys()) {
            try {
                var val = registry.get(ResourceLocation.parse(key));
                if (val != Items.AIR && val != Fluids.EMPTY) {
                    map.put(val, new StatisticValue(tag.getCompound(key)));
                }
            } catch (Exception ignored) {}
        }
    }

    private static <T> CompoundTag toNbt(Registry<T> registry, Map<T, StatisticValue> map) {
        CompoundTag tag = new CompoundTag();
        for (var entry : map.entrySet()) {
            tag.put(registry.getKey(entry.getKey()).toString(), entry.getValue().toNbt());
        }
        return tag;
    }

    private static void pendingReadNbt(Reference2LongMap<Item> map, CompoundTag tag) {
        for (var key : tag.getAllKeys()) {
            try {
                var val = BuiltInRegistries.ITEM.get(ResourceLocation.parse(key));
                if (val != Items.AIR) {
                    map.put(val, tag.getLong(key));
                }
            } catch (Exception ignored) {}
        }
    }

    private static CompoundTag pendingToNbt(Reference2LongMap<Item> map) {
        CompoundTag tag = new CompoundTag();
        for (var entry : map.reference2LongEntrySet()) {
            tag.putLong(BuiltInRegistries.ITEM.getKey(entry.getKey()).toString(), entry.getLongValue());
        }
        return tag;
    }
}
