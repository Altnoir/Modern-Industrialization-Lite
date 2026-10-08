package aztech.milite.compat.ftbteams;

import net.neoforged.fml.ModList;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface FTBTeamsFacade {
    FTBTeamsFacade INSTANCE = getInstance();

    private static FTBTeamsFacade getInstance() {
        if (ModList.get().isLoaded("ftbteams")) {
            try {
                return Class.forName("aztech.milite.compat.ftbteams.FTBTeamsFacadeImpl")
                        .asSubclass(FTBTeamsFacade.class).getConstructor().newInstance();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        return uuid -> List.of();
    }

    Collection<UUID> getOtherPlayersInTeam(UUID playerUuid);
}
