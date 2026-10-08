package aztech.milite.client.compat.viewer.impl.emi;

import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.machines.gui.MachineMenuCommon;
import aztech.milite.MIRegistries;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.world.inventory.MenuType;

@EmiEntrypoint
public class ManualPluginEmi implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        registry.addRecipeHandler(MIRegistries.FORGE_HAMMER_MENU.get(), new ForgeHammerRecipeHandler());
        registry.addRecipeHandler(MIRegistries.PROCESSING_TABLE_MENU.get(), new ProcessingTableRecipeHandler());
        registry.addRecipeHandler((MenuType<MachineMenuCommon>) MIRegistries.MACHINE_MENU.get(), new MachineRecipeHandler());

        // We need a generic one because we want any subclass of MIHandledScreen
        registry.addGenericDragDropHandler(new MIDragDropHandler());

        // Clicking the machine progress bar displays the machine's recipes, like the JEI and REI plugins do.
        MachineClickAreaEmi.init();

        registry.addStackProvider(MachineScreen.class, new MachineStackProvider());
        registry.addExclusionArea(MachineScreen.class, (screen, bounds) -> {
            screen.getExtraBoxes().forEach(r -> bounds.accept(new Bounds(r.x(), r.y(), r.w(), r.h())));
        });
    }
}
