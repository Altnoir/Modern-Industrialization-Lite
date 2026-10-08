package aztech.milite.compat.kubejs.machine;

import aztech.milite.machines.components.OverclockComponent;
import aztech.milite.machines.init.MultiblockMachines;
import aztech.milite.machines.init.SingleBlockCraftingMachines;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.List;

public class ExtraMachineConfig {
    public static class CraftingSingleBlock {
        private final SingleBlockCraftingMachines.Config config;

        public CraftingSingleBlock(SingleBlockCraftingMachines.Config config) {
            this.config = config;
        }

        public CraftingSingleBlock steamCustomOverclock(JsonObject object) {
            config.steamOverclockCatalysts = parseOverclockFromObject(object);
            return this;
        }
    }

    public static class CraftingMultiBlock {
        public List<OverclockComponent.Catalyst> steamOverclockCatalysts = OverclockComponent.getDefaultCatalysts();
        public List<Consumer<MultiblockMachines.Rei>> reiConfigs = new ArrayList<>();

        public CraftingMultiBlock steamCustomOverclock(JsonObject object) {
            steamOverclockCatalysts = parseOverclockFromObject(object);
            return this;
        }

        public CraftingMultiBlock reiExtra(Consumer<MultiblockMachines.Rei> consumer) {
            reiConfigs.add(consumer);
            return this;
        }
    }

    private static List<OverclockComponent.Catalyst> parseOverclockFromObject(JsonObject object) {
        var catalysts = new ArrayList<OverclockComponent.Catalyst>();

        for (var entry : object.entrySet()) {
            var catalystName = entry.getKey();

            var parameters = entry.getValue().getAsJsonObject();
            var multiplier = GsonHelper.getAsDouble(parameters, "multiplier");
            var ticks = GsonHelper.getAsInt(parameters, "ticks");

            catalysts.add(new OverclockComponent.Catalyst(multiplier, ResourceLocation.parse(catalystName), ticks));
        }

        return catalysts;
    }
}
