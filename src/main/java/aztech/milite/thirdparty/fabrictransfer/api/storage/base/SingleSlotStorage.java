package aztech.milite.thirdparty.fabrictransfer.api.storage.base;

import aztech.milite.thirdparty.fabrictransfer.api.storage.SlottedStorage;
import aztech.milite.thirdparty.fabrictransfer.api.storage.StorageView;
import aztech.milite.thirdparty.fabrictransfer.impl.TransferApiImpl;

import java.util.Iterator;

/**
 * A storage that is also its only storage view.
 * It can be used in APIs for storages that are wrappers around a single "slot", or for slightly more convenient implementation.
 *
 * @param <T> The type of the stored resource.
 */
public interface SingleSlotStorage<T> extends SlottedStorage<T>, StorageView<T> {
    @Override
    default Iterator<StorageView<T>> iterator() {
        return TransferApiImpl.singletonIterator(this);
    }

    @Override
    default int getSlotCount() {
        return 1;
    }

    @Override
    default SingleSlotStorage<T> getSlot(int slot) {
        if (slot != 0) {
            throw new IndexOutOfBoundsException("Slot " + slot + " does not exist in a single-slot storage.");
        }

        return this;
    }
}
