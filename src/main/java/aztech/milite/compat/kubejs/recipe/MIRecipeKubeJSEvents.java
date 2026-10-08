package aztech.milite.compat.kubejs.recipe;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface MIRecipeKubeJSEvents {
    EventGroup EVENT_GROUP = EventGroup.of("MIRecipeEvents");

    EventHandler CUSTOM_CONDITION = EVENT_GROUP.server("customCondition", () -> CustomConditionEventJS.class);
}
