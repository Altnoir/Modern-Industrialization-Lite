package aztech.milite;

import aztech.milite.advancement.BuiltMultiblockTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class MIAdvancementTriggers {
    private MIAdvancementTriggers() {}

    private static final DeferredRegister<CriterionTrigger<?>> DR = DeferredRegister.create(Registries.TRIGGER_TYPE, MI.ID);

    public static final Supplier<PlayerTrigger> PLAYER_LOGGED_IN = DR.register("player_logged_in", PlayerTrigger::new);
    public static final Supplier<PlayerTrigger> USED_STEEL_UPGRADE = DR.register("used_steel_upgrade", PlayerTrigger::new);
    public static final Supplier<BuiltMultiblockTrigger> BUILT_MULTIBLOCK = DR.register("built_multiblock", BuiltMultiblockTrigger::new);

    public static void init(IEventBus modBus) {
        DR.register(modBus);
    }
}
