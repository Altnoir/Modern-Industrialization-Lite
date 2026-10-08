package aztech.milite.machines.gui;

import aztech.milite.inventory.MIInventory;
import aztech.milite.inventory.SlotGroup;
import net.minecraft.world.inventory.Slot;

/**
 * Common class for the client and the server side of gui components.
 */
public interface GuiComponent {
    default void setupMenu(MenuFacade menu) {}

    interface MenuFacade {
        void addSlotToMenu(Slot slot, SlotGroup slotGroup);

        MachineGuiParameters getGuiParams();

        /**
         * <b>WARNING!</b> Do not modify the inventory on the client. This must be treated as read only.
         */
        MIInventory getMachineInventory();
    }
}
