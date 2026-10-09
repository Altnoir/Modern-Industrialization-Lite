package aztech.milite.inventory;

import aztech.milite.api.machine.component.ItemAccess;
import aztech.milite.compat.viewer.ReiDraggable;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.milite.thirdparty.fabrictransfer.api.storage.base.ResourceAmount;
import aztech.milite.util.Simulation;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.List;
import java.util.Objects;

public class ConfigurableItemStack extends AbstractConfigurableStack<Item, ItemVariant> implements ItemAccess {
    private int adjustedCapacity = 64;

    private @Nullable ItemStack cachedItemStack;

    public ConfigurableItemStack() {}

    public ConfigurableItemStack(CompoundTag compound, HolderLookup.Provider registries) {
        super(compound, registries);
        this.adjustedCapacity = compound.getInt("adjCap");
    }

    @Override
    public CompoundTag toNbt(HolderLookup.Provider registries) {
        CompoundTag nbt = super.toNbt(registries);
        nbt.putInt("adjCap", this.adjustedCapacity);
        return nbt;
    }

    public static ConfigurableItemStack standardInputSlot() {
        ConfigurableItemStack stack = new ConfigurableItemStack();
        stack.playerInsert = true;
        stack.pipesInsert = true;
        return stack;
    }

    public static ConfigurableItemStack standardOutputSlot() {
        ConfigurableItemStack stack = new ConfigurableItemStack();
        stack.pipesExtract = true;
        return stack;
    }

    public static ConfigurableItemStack standardIOSlot(boolean pipeIO) {
        ConfigurableItemStack stack = new ConfigurableItemStack();
        stack.playerInsert = true;
        if (pipeIO) {
            stack.pipesInsert = true;
            stack.pipesExtract = true;
        }
        return stack;
    }

    public static ConfigurableItemStack lockedInputSlot(Item item) {
        ConfigurableItemStack stack = new ConfigurableItemStack();
        stack.key = ItemVariant.of(item);
        stack.lockedInstance = item;
        stack.playerInsert = true;
        stack.playerLockable = false;
        stack.playerLocked = true;
        stack.pipesInsert = true;
        return stack;
    }

    public ConfigurableItemStack(ConfigurableItemStack other) {
        super(other);
        this.adjustedCapacity = other.adjustedCapacity;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        ConfigurableItemStack that = (ConfigurableItemStack) o;
        return adjustedCapacity == that.adjustedCapacity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), adjustedCapacity);
    }

    @Override
    protected ItemVariant getBlankVariant() {
        return ItemVariant.blank();
    }

    @Override
    protected Item getEmptyInstance() {
        return Items.AIR;
    }

    @Override
    protected Registry<Item> getRegistry() {
        return BuiltInRegistries.ITEM;
    }

    @Override
    protected ItemVariant readVariantFromNbt(CompoundTag compound, HolderLookup.Provider registries) {
        return ItemVariant.fromNbt(compound, registries);
    }

    @Override
    public long getCapacity() {
        return key.isBlank() ? adjustedCapacity : Math.min(adjustedCapacity, key.getMaxStackSize());
    }

    @Override
    public long getRemainingCapacityFor(ItemVariant key) {
        if (adjustedCapacity < amount) {
            return 0; // Make sure we don't get negative counts if this happens!
        }
        return Math.min(key.getMaxStackSize(), adjustedCapacity) - amount;
    }

    @Override
    public long getTotalCapacityFor(Item instance) {
        return Math.min(ItemVariant.of(instance).getMaxStackSize(), adjustedCapacity);
    }

    public static ArrayList<ConfigurableItemStack> copyList(List<ConfigurableItemStack> list) {
        ArrayList<ConfigurableItemStack> copy = new ArrayList<>(list.size());
        for (ConfigurableItemStack stack : list) {
            copy.add(new ConfigurableItemStack(stack));
        }
        return copy;
    }

    public boolean isValid(ItemStack stack) {
        return isResourceAllowedByLock(stack.getItem());
    }

    public void adjustCapacity(boolean isIncrease, boolean isShiftDown) {
        int delta = isShiftDown ? 8 : 1;
        if (!isIncrease) {
            delta = -delta;
        }
        adjustedCapacity = Mth.clamp(adjustedCapacity + delta, 0, 64);
        notifyListeners();
    }

    public int getAdjustedCapacity() {
        return adjustedCapacity;
    }

    @Override
    public ItemVariant getVariant() {
        return getResource();
    }

    @Override
    public ItemStack toStack() {
        if (cachedItemStack == null) {
            cachedItemStack = ItemAccess.super.toStack();
        }
        return cachedItemStack;
    }

    @Override
    public void setAmount(long amount) {
        cachedItemStack = null;
        super.setAmount(amount);
    }

    @Override
    public void setKey(ItemVariant key) {
        cachedItemStack = null;
        super.setKey(key);
    }

    @Override
    public void revertToSnapshot(ResourceAmount<ItemVariant> ra) {
        cachedItemStack = null;
        super.revertToSnapshot(ra);
    }

    public class ConfigurableItemSlot extends HackySlot implements ReiDraggable, BackgroundRenderedSlot {
        private final Predicate<ItemStack> insertPredicate;
        private final Runnable markDirty;

        public ConfigurableItemSlot(ConfigurableItemSlot other) {
            this(other.markDirty, other.x, other.y, other.insertPredicate);

            this.index = other.index;
        }

        public ConfigurableItemSlot(Runnable markDirty, int x, int y, Predicate<ItemStack> insertPredicate) {
            super(x, y);

            this.insertPredicate = insertPredicate;
            this.markDirty = markDirty;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return playerInsert && ConfigurableItemStack.this.isValid(stack) && insertPredicate.test(stack);
        }

        @Override
        public boolean mayPickup(Player playerEntity) {
            return playerExtract;
        }

        public ConfigurableItemStack getConfStack() {
            return ConfigurableItemStack.this;
        }

        @Override
        protected ItemStack getRealStack() {
            return key.toStack((int) amount);
        }

        @Override
        protected void setRealStack(ItemStack stack) {
            key = ItemVariant.of(stack);
            amount = stack.getCount();
            cachedItemStack = null;
            notifyListeners();
            markDirty.run();
        }

        @Override
        public int getMaxStackSize() {
            return adjustedCapacity;
        }

        @Override
        public boolean dragFluid(FluidVariant fluidKey, Simulation simulation) {
            return false;
        }

        @Override
        public boolean dragItem(ItemVariant itemKey, Simulation simulation) {
            return playerLock(itemKey.getItem(), simulation);
        }

        @Override
        public int getBackgroundU() {
            return isPlayerLocked() ? 72 : isMachineLocked() ? 108 : 0;
        }
    }
}
