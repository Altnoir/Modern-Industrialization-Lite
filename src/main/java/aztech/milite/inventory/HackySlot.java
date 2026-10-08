package aztech.milite.inventory;

import aztech.milite.util.UnsupportedOperationInventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * Hacky base slot to work around vanilla mutating stacks directly.
 */
public abstract class HackySlot extends Slot {
    // Vanilla MC code modifies the stack returned by `getStack()` directly, but it
    // calls `markDirty()` when that happens, so we just cache the returned stack,
    // and set it when `markDirty()` is called.
    @Nullable
    private ItemStack cachedReturnedStack = null;

    public HackySlot(int x, int y) {
        super(new UnsupportedOperationInventory(), 0, x, y);
    }

    protected abstract ItemStack getRealStack();

    protected abstract void setRealStack(ItemStack stack);

    @Override
    public final ItemStack getItem() {
        return cachedReturnedStack = getRealStack();
    }

    @Override
    public final void set(ItemStack stack) {
        setRealStack(stack);
        cachedReturnedStack = stack;
    }

    @Override
    public final void setChanged() {
        if (cachedReturnedStack != null) {
            set(cachedReturnedStack);
        }
    }

    @Override
    public final ItemStack remove(int amount) {
        var stack = getRealStack().copy();
        var ret = stack.split(amount);
        set(stack);
        cachedReturnedStack = null;
        return ret;
    }
}
