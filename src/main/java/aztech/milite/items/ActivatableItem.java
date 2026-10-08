package aztech.milite.items;

import aztech.milite.MIComponents;
import net.minecraft.world.item.ItemStack;

public interface ActivatableItem {
    default boolean getDefaultActivatedState() {
        return false;
    }

    default boolean isActivated(ItemStack stack) {
        return stack.getOrDefault(MIComponents.ACTIVATED.get(), this.getDefaultActivatedState());
    }

    default void setActivated(ItemStack stack, boolean activated) {
        stack.set(MIComponents.ACTIVATED.get(), activated);
    }
}
