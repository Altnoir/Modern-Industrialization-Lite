package aztech.milite.client.compat.viewer.impl;

import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.client.machines.guicomponents.CraftingMultiblockGuiClient;
import aztech.milite.compat.rei.machines.ReiMachineRecipes;

public class MachineScreenPredicateTest {
    public static boolean test(ReiMachineRecipes.MachineScreenPredicate predicate, MachineScreen screen) {
        return switch (predicate) {
            case ANY -> true;
            case MULTIBLOCK -> {
                for (GuiComponentClient<?, ?> client : screen.getMenu().components) {
                    if (client instanceof CraftingMultiblockGuiClient cmGui && cmGui.isShapeValid()) {
                        yield true;
                    }
                }
                yield false;
            }
        };
    }
}
