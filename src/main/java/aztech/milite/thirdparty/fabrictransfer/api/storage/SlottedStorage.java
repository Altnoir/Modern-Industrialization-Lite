package aztech.milite.thirdparty.fabrictransfer.api.storage;

import aztech.milite.thirdparty.fabrictransfer.api.storage.base.SingleSlotStorage;
import aztech.milite.thirdparty.fabrictransfer.impl.TransferApiImpl;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * A {@link Storage} implementation made of indexed slots.
 *
 * <p>
 * Please note that some storages may not implement this interface.
 * It is up to the storage implementation to decide whether to implement this interface or not.
 * Checking whether a storage is slotted can be done using {@code instanceof}.
 *
 * @param <T> The type of the stored resources.
 */
public interface SlottedStorage<T> extends Storage<T> {
    /**
     * Retrieve the number of slots in this storage.
     */
    int getSlotCount();

    /**
     * Retrieve a specific slot of this storage.
     *
     * @throws IndexOutOfBoundsException If the slot index is out of bounds.
     */
    SingleSlotStorage<T> getSlot(int slot);

    /**
     * Retrieve a list containing all the slots of this storage. <b>The list must not be modified.</b>
     *
     * <p>
     * This function can be used to interface with code that requires a slot list,
     * for example {@link StorageUtil#insertStacking} or {@link ContainerItemContext#getAdditionalSlots()}.
     *
     * <p>
     * It is guaranteed that calling this function is fast.
     * The default implementation returns a view over the storage that delegates to {@link #getSlotCount} and {@link #getSlot}.
     */
    @UnmodifiableView
    default List<SingleSlotStorage<T>> getSlots() {
        return TransferApiImpl.makeListView(this);
    }
}
