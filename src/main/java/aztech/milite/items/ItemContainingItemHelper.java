package aztech.milite.items;

import aztech.milite.blocks.storage.ResourceStorage;
import aztech.milite.MIComponents;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

public interface ItemContainingItemHelper extends ContainerItem<ItemVariant> {
    default boolean handleStackedOnOther(ItemStack stackBarrel, Slot slot, ClickAction clickType, Player player) {
        if (clickType == ClickAction.SECONDARY && slot.allowModification(player)) {
            Mutable<ItemStack> ref = new MutableObject<>(slot.getItem());
            boolean result = handleClick(player, stackBarrel, ref);
            slot.set(ref.getValue());
            return result;
        } else {
            return false;
        }
    }

    default boolean handleOtherStackedOnMe(ItemStack stackBarrel, ItemStack itemStack, Slot slot, ClickAction clickType, Player player,
            SlotAccess cursorStackReference) {
        if (clickType == ClickAction.SECONDARY && slot.allowModification(player)) {
            Mutable<ItemStack> ref = new MutableObject<>(itemStack);
            boolean result = handleClick(player, stackBarrel, ref);
            cursorStackReference.set(ref.getValue());
            slot.set(stackBarrel);
            return result;
        } else {
            return false;
        }
    }

    default boolean handleClick(Player player, ItemStack barrelLike, Mutable<ItemStack> otherStack) {
        if (!(barrelLike.getItem() instanceof ItemContainingItemHelper)) {
            throw new AssertionError("This method should only be called on a ItemContainingItemHelper.");
        }

        if (barrelLike.getCount() != 1) {
            return false;
        }

        var barrelHandler = new ItemHandler(barrelLike, this);

        // Try to fill barrel with otherStack
        int otherCount = otherStack.getValue().getCount();
        var leftover = barrelHandler.insertItem(0, otherStack.getValue(), false);
        int insertedCount = otherCount - leftover.getCount();
        otherStack.setValue(leftover);
        if (insertedCount != 0) {
            return true;
        }

        // Try to empty barrel into otherStack
        var heldStack = barrelHandler.getStackInSlot(0);
        if (!heldStack.isEmpty()) {
            if (otherStack.getValue().isEmpty() || ItemStack.isSameItemSameComponents(heldStack, otherStack.getValue())) {
                var extracted = barrelHandler.extractItem(0, heldStack.getMaxStackSize() - otherStack.getValue().getCount(), false);
                if (extracted.getCount() > 0) {
                    extracted.grow(otherStack.getValue().getCount()); // Grow by existing items
                    otherStack.setValue(extracted);
                    return true;
                }
            }
        }

        return !isEmpty(barrelLike) || !otherStack.getValue().isEmpty();
    }

    @Override
    default DataComponentType<ResourceStorage<ItemVariant>> getComponentType() {
        return MIComponents.ITEM_STORAGE.get();
    }

    @Override
    default ResourceStorage<ItemVariant> getDefaultComponent() {
        return ResourceStorage.ITEM_EMPTY;
    }
}
