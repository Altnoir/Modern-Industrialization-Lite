package aztech.milite.inventory;

import net.neoforged.neoforge.items.IItemHandler;

public interface FilledItemStorage {
    static boolean isFull(IItemHandler handler) {
        if (handler instanceof FilledItemStorage filled) {
            return filled.isFull();
        }
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            var stack = handler.getStackInSlot(slot);
            if (stack.getCount() < handler.getSlotLimit(slot)) {
                return false;
            }
        }
        return true;
    }

    boolean isFull();
}
