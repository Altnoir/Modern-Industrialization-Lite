package aztech.milite.client.compat.viewer.impl.emi;

import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.ConfigurableItemStack;
import dev.emi.emi.api.EmiStackProvider;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.material.Fluid;

class MachineStackProvider implements EmiStackProvider<MachineScreen> {
    @Override
    public EmiStackInteraction getStackAt(MachineScreen screen, int x, int y) {
        Slot slot = screen.getFocusedSlot();
        if (slot instanceof ConfigurableFluidStack.ConfigurableFluidSlot) {
            ConfigurableFluidStack stack = ((ConfigurableFluidStack.ConfigurableFluidSlot) slot).getConfStack();
            if (stack.getAmount() > 0) {
                Fluid fluid = stack.getResource().getFluid();
                return new EmiStackInteraction(EmiStack.of(fluid), null, false);
            } else if (stack.getLockedInstance() != null) {
                Fluid fluid = stack.getLockedInstance();
                if (fluid != null) {
                    return new EmiStackInteraction(EmiStack.of(fluid), null, false);
                }
            }
        } else if (slot instanceof ConfigurableItemStack.ConfigurableItemSlot) {
            ConfigurableItemStack stack = ((ConfigurableItemStack.ConfigurableItemSlot) slot).getConfStack();
            if (stack.getLockedInstance() != null) {
                return new EmiStackInteraction(EmiStack.of(stack.getLockedInstance()), null, false);
            }
        }
        return EmiStackInteraction.EMPTY;
    }
}
