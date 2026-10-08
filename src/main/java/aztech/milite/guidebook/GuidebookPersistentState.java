package aztech.milite.guidebook;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class GuidebookPersistentState extends SavedData {
    private static final Factory<GuidebookPersistentState> FACTORY = new Factory<>(GuidebookPersistentState::new, GuidebookPersistentState::fromNbt);
    private static final String NAME = "milite_guidebook";
    private final Set<String> receivedPlayers;

    private GuidebookPersistentState(Set<String> receivedPlayers) {
        this.receivedPlayers = receivedPlayers;
    }

    private GuidebookPersistentState() {
        this(new HashSet<>());
    }

    public boolean hasPlayerReceivedGuidebook(Player player) {
        return receivedPlayers.contains(player.getStringUUID());
    }

    public void addPlayerReceivedGuidebook(Player player) {
        receivedPlayers.add(player.getStringUUID());
        setDirty();
    }

    public static GuidebookPersistentState fromNbt(CompoundTag tag, HolderLookup.Provider registries) {
        Set<String> receivedPlayers = new HashSet<>();
        ListTag list = tag.getList("receivedPlayers", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); ++i) {
            receivedPlayers.add(list.getString(i));
        }
        return new GuidebookPersistentState(receivedPlayers);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (String receivedPlayer : receivedPlayers) {
            list.add(StringTag.valueOf(receivedPlayer));
        }
        tag.put("receivedPlayers", list);
        return tag;
    }

    public static GuidebookPersistentState get(MinecraftServer server) {
        ServerLevel world = server.getLevel(ServerLevel.OVERWORLD);
        return world.getDataStorage().computeIfAbsent(FACTORY, NAME);
    }
}
