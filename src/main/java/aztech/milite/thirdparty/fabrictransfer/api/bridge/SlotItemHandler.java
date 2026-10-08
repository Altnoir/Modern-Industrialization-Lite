package aztech.milite.thirdparty.fabrictransfer.api.bridge;

import aztech.milite.blocks.storage.AbstractStorageBlockEntity;
import aztech.milite.inventory.FilledItemStorage;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.milite.thirdparty.fabrictransfer.api.storage.base.SingleSlotStorage;
import aztech.milite.thirdparty.fabrictransfer.api.transaction.Transaction;
import com.google.common.primitives.Ints;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public final class SlotItemHandler implements IItemHandler, FilledItemStorage {
    private final AbstractStorageBlockEntity<ItemVariant> storage;

    public SlotItemHandler(AbstractStorageBlockEntity<ItemVariant> storage) {
        this.storage = storage;
    }

    public SingleSlotStorage<ItemVariant> storage() {
        return storage;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return storage.getItemStack();
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return stack;
        }
        try (var tx = Transaction.hackyOpen()) {
            var inserted = storage.insert(ItemVariant.of(stack), stack.getCount(), tx);
            if (!simulate) {
                tx.commit();
            }
            return inserted == 0 ? stack : stack.copyWithCount(stack.getCount() - (int) inserted);
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }
        try (var tx = Transaction.hackyOpen()) {
            var resource = storage.getResource();
            if (resource.isBlank()) {
                return ItemStack.EMPTY;
            }
            var extracted = storage.extract(resource, amount, tx);
            if (!simulate) {
                tx.commit();
            }
            return extracted == 0 ? ItemStack.EMPTY : resource.toStack((int) extracted);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return Ints.saturatedCast(storage.getCapacity());
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public boolean isFull() {
        return storage.getAmount() >= storage.getCapacity();
    }
}
