package aztech.milite.compat.kubejs.material;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;

public interface MIMaterialKubeJSEvents {
    EventGroup EVENT_GROUP = EventGroup.of("MIMaterialEvents");

    EventHandler ADD_MATERIALS = EVENT_GROUP.startup("addMaterials", () -> AddMaterialsEventJS.class);
    TargetedEventHandler<String> MODIFY_MATERIAL = EVENT_GROUP.startup("modifyMaterial", () -> ModifyMaterialEventJS.class)
            .supportsTarget(EventTargetType.STRING);
}
