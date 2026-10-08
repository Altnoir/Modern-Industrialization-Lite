package aztech.milite.util;

import aztech.milite.inventory.ConfigurableItemStack;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;

public class ItemStackHelper {
    /**
     * Try to consume the fuel in the stack.
     *
     * @param stack    The fuel stack
     * @param simulate If true, the stack will not be modified
     * @return false if the fuel could not be consumed, true otherwise
     */
    public static boolean consumeFuel(ConfigurableItemStack stack, boolean simulate) {
        if (stack.isResourceBlank())
            return false;
        var itemStack = stack.toStack();
        if (itemStack.hasCraftingRemainingItem()) {
            var remainder = ItemVariant.of(itemStack.getCraftingRemainingItem());
            if (stack.getAmount() == 1 && stack.isResourceAllowedByLock(remainder)) {
                if (!simulate) {
                    stack.setAmount(1);
                    stack.setKey(remainder);
                }
                return true;
            }
        } else {
            if (!simulate) {
                stack.decrement(1);
            }
            return true;
        }
        return false;
    }
}
