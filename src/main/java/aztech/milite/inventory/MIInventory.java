package aztech.milite.inventory;

import aztech.milite.machines.MachineComponent;
import aztech.milite.util.NbtHelper;
import aztech.milite.util.TransferHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MIInventory implements MachineComponent {
    public static final MIInventory EMPTY;

    private @Nullable BlockCapabilityCache<IItemHandler, @Nullable Direction> outputCache;

    public final MIItemStorage itemStorage;
    public final MIFluidStorage fluidStorage;
    public final SlotPositions itemPositions;
    public final SlotPositions fluidPositions;

    /**
     * Build a new MI inventory. If you need to access the stacks, make sure to
     * reference them through this inventory and not directly!
     */
    public MIInventory(List<ConfigurableItemStack> itemStacks, List<ConfigurableFluidStack> fluidStacks, SlotPositions itemPositions,
            SlotPositions fluidPositions) {
        // Must be array lists to allow using .set() in readNbt()
        this.itemStorage = new MIItemStorage(new ArrayList<>(itemStacks));
        this.fluidStorage = new MIFluidStorage(new ArrayList<>(fluidStacks));
        this.itemPositions = itemPositions;
        this.fluidPositions = fluidPositions;
        if (itemPositions.size() != itemStacks.size()) {
            throw new IllegalArgumentException(
                    "Mismatched item slots and positions. Slot count: " + itemStacks.size() + ", position count: " + itemPositions.size());
        }
        if (fluidPositions.size() != fluidStacks.size()) {
            throw new IllegalArgumentException(
                    "Mismatched fluid slots and positions. Slot count: " + fluidStacks.size() + ", position count: " + fluidPositions.size());
        }
    }

    public void addListener(ChangeListener listener, Object token) {
        listener.listenAll(getItemStacks(), token);
        listener.listenAll(getFluidStacks(), token);
    }

    public List<ConfigurableItemStack> getItemStacks() {
        return itemStorage.stacks;
    }

    public List<ConfigurableFluidStack> getFluidStacks() {
        return fluidStorage.stacks;
    }

    public void autoExtractItems(Level world, BlockPos pos, Direction direction) {
        // The second check is needed in case we change the output side...
        boolean updateCache = outputCache == null || outputCache.context() != direction.getOpposite();

        if (updateCache) {
            outputCache = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, (ServerLevel) world, pos.relative(direction),
                    direction.getOpposite());
        }

        var target = outputCache.getCapability();
        if (target != null && !FilledItemStorage.isFull(target)) {
            TransferHelper.moveAll(itemStorage.itemHandler, target, true);
        }
    }

    public void autoExtractFluids(Level world, BlockPos pos, Direction direction) {
        IFluidHandler target = world.getCapability(Capabilities.FluidHandler.BLOCK, pos.relative(direction), direction.getOpposite());

        if (target != null) {
            TransferHelper.tryFluidTransfer(target, fluidStorage.fluidHandler, Integer.MAX_VALUE, true);
        }
    }

    public void autoInsertItems(Level world, BlockPos pos, Direction direction) {
        IItemHandler target = world.getCapability(Capabilities.ItemHandler.BLOCK, pos.relative(direction), direction.getOpposite());

        if (target != null && !FilledItemStorage.isFull(itemStorage.itemHandler)) {
            TransferHelper.moveAll(target, itemStorage.itemHandler, false);
        }
    }

    public void autoInsertFluids(Level world, BlockPos pos, Direction direction) {
        IFluidHandler target = world.getCapability(Capabilities.FluidHandler.BLOCK, pos.relative(direction), direction.getOpposite());

        if (target != null) {
            TransferHelper.tryFluidTransfer(fluidStorage.fluidHandler, target, Integer.MAX_VALUE, true);
        }
    }

    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        NbtHelper.putList(tag, "items", itemStorage.stacks, configurableItemStack -> configurableItemStack.toNbt(registries));
        NbtHelper.putList(tag, "fluids", fluidStorage.stacks, configurableFluidStack -> configurableFluidStack.toNbt(registries));
    }

    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        List<ConfigurableItemStack> newItemStacks = new ArrayList<>();
        List<ConfigurableFluidStack> newFluidStacks = new ArrayList<>();

        NbtHelper.getList(tag, "items", newItemStacks, t -> new ConfigurableItemStack(t, registries));
        NbtHelper.getList(tag, "fluids", newFluidStacks, t -> new ConfigurableFluidStack(t, registries));

        if (isUpgradingMachine) {
            // Increase fluid slot capacities if upgrading
            for (int i = 0; i < newFluidStacks.size() && i < fluidStorage.stacks.size(); ++i) {
                newFluidStacks.get(i).setCapacity(fluidStorage.stacks.get(i).getCapacity());
            }
        }

        SlotConfig.readSlotList(itemStorage.stacks, newItemStacks);
        SlotConfig.readSlotList(fluidStorage.stacks, newFluidStacks);
    }

    static {
        EMPTY = new MIInventory(Collections.emptyList(), Collections.emptyList(), SlotPositions.empty(), SlotPositions.empty());
    }
}
