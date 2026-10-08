package aztech.milite.compat.kubejs.machine;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;

public interface MIMachineKubeJSEvents {
    EventGroup EVENT_GROUP = EventGroup.of("MIMachineEvents");

    EventHandler REGISTER_RECIPE_TYPES = EVENT_GROUP.startup("registerRecipeTypes", () -> RegisterRecipeTypesEventJS.class);
    EventHandler REGISTER_CASINGS = EVENT_GROUP.startup("registerCasings", () -> RegisterCasingsEventJS.class);
    EventHandler REGISTER_MACHINES = EVENT_GROUP.startup("registerMachines", () -> RegisterMachinesEventJS.class);
    EventHandler REGISTER_HATCHES = EVENT_GROUP.startup("registerHatches", () -> RegisterHatchesEventJS.class);
    TargetedEventHandler<String> ADD_MULTIBLOCK_SLOTS = EVENT_GROUP.startup("addMultiblockSlots", () -> AddMultiblockSlotsEventJS.class)
            .requiredTarget(EventTargetType.STRING);
    EventHandler ADD_EBF_TIERS = EVENT_GROUP.startup("addEbfTiers", () -> AddEbfTiersEventJS.class);
}
