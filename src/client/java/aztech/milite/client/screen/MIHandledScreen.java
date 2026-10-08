package aztech.milite.client.screen;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

/**
 * Base class for MI screen handlers, grants access to a few protected fields to the REI plugin.
 */
public abstract class MIHandledScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    public MIHandledScreen(T handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    public Slot getFocusedSlot() {
        return hoveredSlot;
    }

    public int getX() {
        return leftPos;
    }

    public int getY() {
        return topPos;
    }
}
