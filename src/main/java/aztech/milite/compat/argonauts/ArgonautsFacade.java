package aztech.milite.compat.argonauts;

import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ArgonautsFacade {
    ArgonautsFacade INSTANCE = getInstance();

    private static ArgonautsFacade getInstance() {
        if (ModList.get().isLoaded("argonauts")) {
            try {
                return Class.forName("aztech.milite.compat.argonauts.ArgonautsFacadeImpl")
                        .asSubclass(ArgonautsFacade.class).getConstructor().newInstance();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        return (ms, uuid) -> List.of();
    }

    Collection<UUID> getOtherPlayersInGuild(Level level, UUID playerUuid);
}
