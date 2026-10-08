package aztech.milite.client.compat.viewer.impl.rei;

import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.items.diesel_tools.DieselToolItem;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

@REIPluginClient
public class ManualPluginRei implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof DieselToolItem) {
                if (item.builtInRegistryHolder().is(ItemTags.AXES)) {
                    registry.addWorkstations(BuiltinPlugin.STRIPPING, EntryStacks.of(item));
                }
                if (item.builtInRegistryHolder().is(ItemTags.SHOVELS)) {
                    registry.addWorkstations(BuiltinPlugin.PATHING, EntryStacks.of(item));
                }
            }
        }
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(new MachineSlotLockingHandler());
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerDraggableStackVisitor(new MIDraggableStackVisitor());

        registry.registerClickArea(MachineScreen.class, new MachineClickAreaHandler());
        registry.registerFocusedStack(new MachineFocusedStackProvider());
        registry.exclusionZones().register(MachineScreen.class, screen -> {
            return screen.getExtraBoxes().stream().map(r -> new me.shedaniel.math.Rectangle(r.x(), r.y(), r.w(), r.h())).toList();
        });
    }
}
