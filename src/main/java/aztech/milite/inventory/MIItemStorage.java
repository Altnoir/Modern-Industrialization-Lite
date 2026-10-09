package aztech.milite.inventory;

import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.milite.thirdparty.fabrictransfer.api.transaction.Transaction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;
import java.util.Set;

public class MIItemStorage extends MIStorage<Item, ItemVariant, ConfigurableItemStack> {
    public final IItemHandler itemHandler = new ItemHandler();

    public MIItemStorage(List<ConfigurableItemStack> stacks) {
        super(stacks, false);
    }

    public class ItemHandler implements IItemHandler, WhitelistedItemStorage, FilledItemStorage {
        @Override
        public int getSlots() {
            return stacks.size();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return stacks.get(slot).toStack();
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack item, boolean simulate) {
            if (item.isEmpty()) {
                return ItemStack.EMPTY;
            }

            var stack = stacks.get(slot);
            if (!stack.pipesInsert ||
                    stack.getAmount() >= stack.getCapacity()) {
                return item;
            }

            // A bit messy, but lets us avoid always getting an ItemVariant which takes unnecessary time with a map lookup.
            ItemVariant resource = null;
            boolean canInsert;
            if (stack.getAmount() == 0) {
                // If the amount is 0, we check if the lock allows it.
                canInsert = stack.isResourceAllowedByLock(item.getItem());
            } else if ((item.isEmpty() || item.isComponentsPatchEmpty()) && (stack.isEmpty() || stack.getResource().getComponentsPatch().isEmpty())) {
                // If both items don't have components, we check if they are the same item.
                canInsert = item.is(stack.getResource().getItem());
            } else {
                // Otherwise we check that the resources match exactly.
                resource = ItemVariant.of(item);
                canInsert = stack.getResource().equals(resource);
            }

            if (canInsert) {
                if (resource == null) {
                    resource = ItemVariant.of(item);
                }
                long inserted = Math.min(item.getCount(), stack.getRemainingCapacityFor(resource));

                if (inserted > 0 && !simulate) {
                    stack.setKey(resource);
                    stack.increment(inserted);
                }

                return inserted == item.getCount() ? ItemStack.EMPTY : resource.toStack((int) (item.getCount() - inserted));
            }

            return item;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount <= 0) {
                return ItemStack.EMPTY;
            }
            try (var tx = Transaction.hackyOpen()) {
                var variant = stacks.get(slot).getVariant();
                if (variant.isBlank()) {
                    return ItemStack.EMPTY;
                }

                long result = stacks.get(slot).extract(variant, amount, tx);
                if (result > 0 && !simulate) {
                    tx.commit();
                }
                return result == 0 ? ItemStack.EMPTY : variant.toStack((int) result);
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return (int) stacks.get(slot).getCapacity();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stacks.get(slot).isResourceAllowedByLock(stack.getItem());
        }

        @Override
        public boolean currentlyWhitelisted() {
            for (ConfigurableItemStack stack : stacks) {
                if (stack.pipesInsert && stack.getLockedInstance() == null) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void getWhitelistedItems(Set<Item> whitelist) {
            for (ConfigurableItemStack stack : stacks) {
                if (stack.pipesInsert && !stack.isLockedTo(Items.AIR)) {
                    whitelist.add(stack.getLockedInstance());
                }
            }
        }

        @Override
        public boolean isFull() {
            for (var stack : stacks) {
                if (!stack.isLockedTo(Items.AIR) && stack.getAmount() < stack.getCapacity()) {
                    return false;
                }
            }
            return true;
        }
    }
}
