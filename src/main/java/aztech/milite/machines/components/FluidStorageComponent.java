package aztech.milite.machines.components;

import aztech.milite.api.machine.component.FluidAccess;
import aztech.milite.machines.MachineComponent;
import aztech.milite.thirdparty.fabrictransfer.api.bridge.SlotFluidHandler;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.storage.base.ResourceAmount;
import aztech.milite.thirdparty.fabrictransfer.api.storage.base.SingleVariantStorage;
import com.google.common.base.Preconditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidStorageComponent implements MachineComponent, FluidAccess {
    private final Runnable changeCallback;
    private long capacity;

    SingleVariantStorage<FluidVariant> singleStorageVariant = new SingleVariantStorage<>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return capacity;
        }

        @Override
        protected void onRootCommit(ResourceAmount<FluidVariant> originalState) {
            if (!originalState.resource().equals(variant) || originalState.amount() != amount) {
                changeCallback.run();
            }
        }
    };
    private final IFluidHandler fluidHandler = new SlotFluidHandler(singleStorageVariant);

    public FluidStorageComponent(Runnable changeCallback) {
        this.changeCallback = changeCallback;
    }

    public SingleVariantStorage<FluidVariant> getFluidStorage() {
        return singleStorageVariant;
    }

    public IFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        Preconditions.checkArgument(capacity >= 0, "Fluid Capacity must be > 0");
        this.capacity = capacity;
        singleStorageVariant.amount = Math.min(singleStorageVariant.amount, capacity);
    }

    public FluidVariant getFluid() {
        return singleStorageVariant.variant;
    }

    @Override
    public long getAmount() {
        return singleStorageVariant.amount;
    }

    @Override
    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("fluid", singleStorageVariant.variant.toNbt(registries));
        tag.putLong("amount", singleStorageVariant.amount);
        tag.putLong("capacity", capacity);
    }

    @Override
    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        singleStorageVariant.variant = FluidVariant.fromNbt(tag.getCompound("fluid"), registries);
        singleStorageVariant.amount = tag.getLong("amount");
        capacity = tag.getLong("capacity");
    }

    @Override
    public FluidVariant getVariant() {
        return getFluid();
    }
}
