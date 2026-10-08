package aztech.milite.compat.ftbquests;

import aztech.milite.config.MIStartupConfig;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;

import java.util.UUID;

public interface FTBQuestsFacade {
    FTBQuestsFacade INSTANCE = getInstance();

    private static FTBQuestsFacade getInstance() {
        if (ModList.get().isLoaded("ftbquests") && MIStartupConfig.INSTANCE.ftbQuestsIntegration.getAsBoolean()) {
            try {
                return Class.forName("aztech.milite.compat.ftbquests.FTBQuestsFacadeImpl")
                        .asSubclass(FTBQuestsFacade.class).getConstructor().newInstance();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        return (uuid, item, amount) -> {};
    }

    void addCompleted(UUID uuid, Item item, long amount);
}
