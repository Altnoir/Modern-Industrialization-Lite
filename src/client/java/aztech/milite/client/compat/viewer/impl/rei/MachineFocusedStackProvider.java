package aztech.milite.client.compat.viewer.impl.rei;

import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.ConfigurableItemStack;
import dev.architectury.event.CompoundEventResult;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.registry.screen.FocusedStackProvider;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.material.Fluid;

class MachineFocusedStackProvider implements FocusedStackProvider {
    @Override
    public CompoundEventResult<EntryStack<?>> provide(Screen screen, Point mouse) {
        if (screen instanceof MachineScreen) {
            Slot slot = ((MachineScreen) screen).getFocusedSlot();
            if (slot instanceof ConfigurableFluidStack.ConfigurableFluidSlot) {
                ConfigurableFluidStack stack = ((ConfigurableFluidStack.ConfigurableFluidSlot) slot).getConfStack();
                if (stack.getAmount() > 0) {
                    Fluid fluid = stack.getResource().getFluid();
                    return CompoundEventResult.interruptTrue(EntryStacks.of(fluid));
                } else if (stack.getLockedInstance() != null) {
                    Fluid fluid = stack.getLockedInstance();
                    if (fluid != null) {
                        return CompoundEventResult.interruptTrue(EntryStacks.of(fluid));
                    }
                }
            } else if (slot instanceof ConfigurableItemStack.ConfigurableItemSlot) {
                ConfigurableItemStack stack = ((ConfigurableItemStack.ConfigurableItemSlot) slot).getConfStack();
                if (stack.getLockedInstance() != null) {
                    return CompoundEventResult.interruptTrue(EntryStacks.of(stack.getLockedInstance()));
                }
            }
        }
        return CompoundEventResult.pass();
    }
}
