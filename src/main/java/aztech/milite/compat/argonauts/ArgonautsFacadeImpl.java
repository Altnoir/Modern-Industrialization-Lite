package aztech.milite.compat.argonauts;

import earth.terrarium.argonauts.api.teams.guild.GuildApi;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ArgonautsFacadeImpl implements ArgonautsFacade {
    @Override
    public Collection<UUID> getOtherPlayersInGuild(Level level, UUID playerUuid) {
        var guild = GuildApi.API.getPlayerGuild(level, playerUuid);
        if (guild.isEmpty()) {
            return List.of();
        }
        List<UUID> out = new ArrayList<>();
        for (var memberUuid : guild.get().members().keySet()) {
            if (!memberUuid.equals(playerUuid)) {
                out.add(memberUuid);
            }
        }
        return out;
    }
}
