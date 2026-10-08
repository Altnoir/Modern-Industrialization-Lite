package aztech.milite.util;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Otherwise closing screen handler slots with a null inventory throws an
 * exception due to copySharedSlots.
 */
public class UnsupportedOperationInventory implements Container {
    @Override
    public int getContainerSize() {
        // Don't crash so mouse wheelie can have a chance at noticing this is a peculiar
        // slot.
        return 0;
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack getItem(int slot) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChanged() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean stillValid(Player player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearContent() {
        throw new UnsupportedOperationException();
    }
}
