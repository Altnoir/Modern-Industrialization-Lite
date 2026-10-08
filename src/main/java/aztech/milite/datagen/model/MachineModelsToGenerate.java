package aztech.milite.datagen.model;

import aztech.milite.machines.models.MachineCasing;
import aztech.milite.MI;

import java.util.HashMap;
import java.util.Map;

public class MachineModelsToGenerate {
    public static final Map<String, MachineModelProperties> props = new HashMap<>();

    public static void register(String machine, MachineModelProperties model) {
        props.put(machine, model);
    }

    public static void register(String machine, MachineCasing defaultCasing, String overlay, boolean front, boolean top, boolean side,
            boolean active) {
        var builder = new MachineModelProperties.Builder(defaultCasing);

        if (top) {
            builder.addOverlay("top", MI.id("block/machines/%s/overlay_top".formatted(overlay)));
            if (active) {
                builder.addOverlay("top_active", MI.id("block/machines/%s/overlay_top_active".formatted(overlay)));
            }
        }
        if (front) {
            builder.addOverlay("front", MI.id("block/machines/%s/overlay_front".formatted(overlay)));
            if (active) {
                builder.addOverlay("front_active", MI.id("block/machines/%s/overlay_front_active".formatted(overlay)));
            }
        }
        if (side) {
            builder.addOverlay("side", MI.id("block/machines/%s/overlay_side".formatted(overlay)));
            if (active) {
                builder.addOverlay("side_active", MI.id("block/machines/%s/overlay_side_active".formatted(overlay)));
            }
        }

        builder.addOverlay("output", MI.id("block/overlays/output"));
        builder.addOverlay("item_auto", MI.id("block/overlays/item_auto"));
        builder.addOverlay("fluid_auto", MI.id("block/overlays/fluid_auto"));

        register(machine, builder.build());
    }
}
