package aztech.milite.thirdparty.fabrictransfer.impl.item;

import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.Map;
import java.util.Objects;

public class ItemVariantImpl implements ItemVariant {
    private static final Map<Item, ItemVariant> noTagCache = new ConcurrentHashMap<>();

    public static ItemVariant of(Item item) {
        Objects.requireNonNull(item, "Item may not be null.");

        return noTagCache.computeIfAbsent(item, i -> new ItemVariantImpl(new ItemStack(i)));
    }

    public static ItemVariant of(ItemStack stack) {
        Objects.requireNonNull(stack);

        // Only tag-less or empty item variants are cached for now.
        if (stack.isComponentsPatchEmpty() || stack.isEmpty()) {
            return of(stack.getItem());
        } else {
            return new ItemVariantImpl(stack.copyWithCount(1)); // defensive copy
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger("fabric-transfer-api-v1/item");

    private final ItemStack stack;
    private @Nullable Integer maxStackSize;
    private final int hashCode;

    private ItemVariantImpl(ItemStack stack) {
        this.stack = stack;
        hashCode = ItemStack.hashItemAndComponents(stack);
    }

    @Override
    public Item getObject() {
        return stack.getItem();
    }

    @Override
    public DataComponentPatch getComponentsPatch() {
        return this.stack.getComponentsPatch();
    }

    @Override
    public boolean matches(ItemStack stack) {
        return ItemStack.isSameItemSameComponents(this.stack, stack);
    }

    @Override
    public boolean test(Predicate<ItemStack> predicate) {
        return predicate.test(this.stack);
    }

    @Override
    public ItemStack toStack(int count) {
        return this.stack.copyWithCount(count);
    }

    @Override
    public int getMaxStackSize() {
        if (maxStackSize == null) {
            maxStackSize = stack.getMaxStackSize();
        }
        return maxStackSize;
    }

    @Override
    public boolean isBlank() {
        return this.stack.isEmpty();
    }

    @Override
    public String toString() {
        return "ItemVariant{stack=" + stack + '}';
    }

    @Override
    public boolean equals(Object o) {
        // succeed fast with == check
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ItemVariantImpl itemVariant = (ItemVariantImpl) o;
        // fail fast with hash code
        return hashCode == itemVariant.hashCode && matches(itemVariant.stack);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
