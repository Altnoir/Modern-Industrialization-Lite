package aztech.milite.compat.ftbquests;

import aztech.milite.advancement.BuiltMultiblockContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.ModList;

import java.util.UUID;

public interface FTBQuestsFacade {
    FTBQuestsFacade INSTANCE = getInstance();

    private static FTBQuestsFacade getInstance() {
        if (ModList.get().isLoaded("ftbquests")) {
            try {
                return Class.forName("aztech.milite.compat.ftbquests.FTBQuestsFacadeImpl")
                        .asSubclass(FTBQuestsFacade.class).getConstructor().newInstance();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        return (uuid, item, amount) -> {};
    }

    default void init() {}

    void addCompleted(UUID uuid, Item item, long amount);

    default void addCompleted(UUID uuid, Fluid fluid, long amount) {}

    default void builtMultiblock(ServerPlayer player, BuiltMultiblockContext context) {}
}
