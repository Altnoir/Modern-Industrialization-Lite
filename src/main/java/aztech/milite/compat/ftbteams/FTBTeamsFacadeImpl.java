package aztech.milite.compat.ftbteams;

import dev.ftb.mods.ftbteams.api.event.TeamEvent;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FTBTeamsFacadeImpl implements FTBTeamsFacade {
    // cached to save on needless allocations and overhead...
    private final Map<UUID, Collection<UUID>> cachedPlayersInTeam = new HashMap<>();

    public FTBTeamsFacadeImpl() {
        // Reset cache if teams are modified
        TeamEvent.PLAYER_CHANGED.register(event -> cachedPlayersInTeam.clear());
        // Reset cache if the server stops
        NeoForge.EVENT_BUS.addListener(ServerStoppedEvent.class, event -> cachedPlayersInTeam.clear());
    }

    @Override
    public Collection<UUID> getOtherPlayersInTeam(UUID playerUuid) {
        return cachedPlayersInTeam.computeIfAbsent(playerUuid, uuid -> {
            var team = FTBTeamsAPI.api().getManager().getTeamForPlayerID(uuid);
            if (team.isEmpty()) {
                // Can happen if the uuid is unknown,
                // for example if ftb teams was installed after the fact and this player never logged in since
                return List.of();
            }
            var keys = new HashSet<>(team.get().getMembers());
            keys.remove(uuid);
            return keys;
        });
    }
}
