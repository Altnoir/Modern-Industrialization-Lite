package aztech.milite.inventory;

import aztech.milite.thirdparty.fabrictransfer.api.storage.Storage;
import aztech.milite.thirdparty.fabrictransfer.api.storage.StoragePreconditions;
import aztech.milite.thirdparty.fabrictransfer.api.storage.StorageView;
import aztech.milite.thirdparty.fabrictransfer.api.storage.TransferVariant;
import aztech.milite.thirdparty.fabrictransfer.api.transaction.TransactionContext;

import java.util.function.Predicate;
import java.util.Iterator;
import java.util.List;

public class MIStorage<T, K extends TransferVariant<T>, S extends AbstractConfigurableStack<T, K>> implements Storage<K> {
    final List<S> stacks;
    private final boolean oneSlotPerResource; // true for fluids, false for items

    protected MIStorage(List<S> stacks, boolean oneSlotPerResource) {
        this.stacks = stacks;
        this.oneSlotPerResource = oneSlotPerResource;
    }

    /**
     * @param filter    Return false to skip some configurable stacks.
     * @param lockSlots Whether to lock slots or not.
     */
    public long insert(K resource, long maxAmount, TransactionContext tx, Predicate<? super S> filter, boolean lockSlots) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        boolean containsResourceAlready = false;
        long totalInserted = 0;

        for (int iter = 0; iter < 2; ++iter) {
            for (S stack : stacks) {
                if (!filter.test(stack))
                    continue;
                boolean isSlotEmpty = stack.getAmount() == 0 && stack.getLockedInstance() == null;
                boolean canInsert;

                if (isSlotEmpty) {
                    // Always check for the second iteration.
                    if (oneSlotPerResource) {
                        // Additionally check that the resource is not contained yet.
                        canInsert = iter == 1 && !containsResourceAlready;
                    } else {
                        canInsert = iter == 1;
                    }
                } else if (stack.getAmount() == 0) {
                    // If the amount is 0, we check if the lock allows it.
                    canInsert = stack.isResourceAllowedByLock(resource);
                } else {
                    // Otherwise we check that the resources match exactly.
                    canInsert = stack.getResource().equals(resource);
                }

                if (canInsert) {
                    long inserted = Math.min(maxAmount - totalInserted, stack.getRemainingCapacityFor(resource));

                    if (inserted > 0) {
                        stack.updateSnapshots(tx);
                        stack.setKey(resource);
                        stack.increment(inserted);

                        if (lockSlots) {
                            stack.enableMachineLock(resource.getObject());
                        }
                    }

                    totalInserted += inserted;
                }

                containsResourceAlready = containsResourceAlready || stack.getResource().equals(resource);
            }
        }

        return totalInserted;
    }

    public long insertAllSlot(K resource, long maxAmount, TransactionContext tx) {
        return insert(resource, maxAmount, tx, (slot) -> true, false);
    }

    @Override
    public long insert(K resource, long maxAmount, TransactionContext transaction) {
        return insert(resource, maxAmount, transaction, AbstractConfigurableStack::canPipesInsert, false);
    }

    public long extract(K resource, long maxAmount, TransactionContext transaction, Predicate<? super S> filter) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        long amount = 0;
        for (int i = 0; i < stacks.size() && amount < maxAmount; ++i) {
            if (!filter.test(stacks.get(i))) {
                continue;
            }
            amount += stacks.get(i).extract(resource, maxAmount - amount, transaction);
        }
        return amount;
    }

    @Override
    public long extract(K resource, long maxAmount, TransactionContext transaction) {
        return extract(resource, maxAmount, transaction, (slot) -> true);
    }

    /*
     * Ignore requirement for slot to have pipeExtract = true
     */
    public long extractAllSlot(K resource, long maxAmount, TransactionContext transaction, Predicate<? super S> filter) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        long amount = 0;
        for (int i = 0; i < stacks.size() && amount < maxAmount; ++i) {
            if (!filter.test(stacks.get(i))) {
                continue;
            }
            amount += stacks.get(i).extractDirect(resource, maxAmount - amount, transaction);
        }
        return amount;
    }

    public long extractAllSlot(K resource, long maxAmount, TransactionContext transaction) {
        return extractAllSlot(resource, maxAmount, transaction, (slot) -> true);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Iterator<StorageView<K>> iterator() {
        return (Iterator) stacks.iterator();
    }
}
