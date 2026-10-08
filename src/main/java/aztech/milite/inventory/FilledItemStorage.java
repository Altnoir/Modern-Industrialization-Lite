package aztech.milite.inventory;

import net.neoforged.neoforge.items.IItemHandler;

public interface FilledItemStorage {
    static boolean isFull(IItemHandler handler) {
        return handler instanceof FilledItemStorage filled && filled.isFull();
    }

    boolean isFull();
}
