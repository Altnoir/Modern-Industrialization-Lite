package aztech.milite.datagen.model;

import aztech.milite.machines.models.MachineCasing;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record MachineModelProperties(
        MachineCasing casing,
        Map<String, ResourceLocation> defaultOverlays,
        boolean noOverlayOnOutputSide) {
    public MachineModelProperties(MachineCasing casing, Map<String, ResourceLocation> defaultOverlays, boolean noOverlayOnOutputSide) {
        this.casing = casing;
        this.defaultOverlays = Map.copyOf(defaultOverlays);
        this.noOverlayOnOutputSide = noOverlayOnOutputSide;
    }

    public void addToMachineJson(JsonObject obj) {
        obj.addProperty("casing", casing.key.getPath());

        var defaultOverlays = new JsonObject();
        for (var entry : this.defaultOverlays.entrySet()) {
            defaultOverlays.addProperty(entry.getKey(), entry.getValue().toString());
        }
        obj.add("default_overlays", defaultOverlays);

        if (noOverlayOnOutputSide) {
            obj.addProperty("no_overlay_on_output_side", true);
        }
    }

    public static class Builder {
        private final MachineCasing casing;
        private final Map<String, ResourceLocation> defaultOverlays;
        private boolean noOverlayOnOutputSide = false;

        public Builder(MachineCasing casing) {
            this.casing = casing;
            this.defaultOverlays = new HashMap<>();
        }

        public Builder addOverlay(String name, ResourceLocation texture) {
            defaultOverlays.put(name, texture);
            return this;
        }

        public Builder noOverlayOnOutputSide() {
            noOverlayOnOutputSide = true;
            return this;
        }

        public MachineModelProperties build() {
            return new MachineModelProperties(casing, defaultOverlays, noOverlayOnOutputSide);
        }
    }
}
