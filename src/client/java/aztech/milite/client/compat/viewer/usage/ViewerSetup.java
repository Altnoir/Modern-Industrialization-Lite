package aztech.milite.client.compat.viewer.usage;

import aztech.milite.client.compat.viewer.abstraction.ViewerCategory;
import aztech.milite.compat.rei.machines.ReiMachineRecipes;

import java.util.ArrayList;
import java.util.List;

public class ViewerSetup {
    private ViewerSetup() {}

    public static List<ViewerCategory<?>> setup() {
        List<ViewerCategory<?>> registry = new ArrayList<>();

        registry.add(new FluidFuelsCategory());
        registry.add(new ForgeHammerCategory());
        registry.add(new ProcessingTableCategory());
        registry.add(new MultiblockCategory());
        registry.add(new NeutronInteractionCategory());
        registry.add(new ThermalInteractionCategory());

        for (var params : ReiMachineRecipes.categories.values()) {
            registry.add(MachineCategory.create(params));
        }

        return registry;
    }
}
