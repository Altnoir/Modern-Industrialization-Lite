package aztech.milite.compat.almostunified;

import aztech.milite.config.MIStartupConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.ModList;
import org.jspecify.annotations.Nullable;

public interface AlmostUnifiedFacade {
    AlmostUnifiedFacade INSTANCE = getInstance();

    private static AlmostUnifiedFacade getInstance() {
        if (ModList.get().isLoaded("almostunified") && MIStartupConfig.INSTANCE.almostUnifiedIntegration.getAsBoolean()) {
            try {
                return Class.forName("aztech.milite.compat.almostunified.AlmostUnifiedFacadeImpl")
                        .asSubclass(AlmostUnifiedFacade.class).getConstructor().newInstance();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        return item -> null;
    }

    @Nullable
    Item getTargetItem(ItemLike item);
}
