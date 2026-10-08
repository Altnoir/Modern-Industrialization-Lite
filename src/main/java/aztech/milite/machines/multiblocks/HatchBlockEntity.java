package aztech.milite.machines.multiblocks;

import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.ConfigurableItemStack;
import aztech.milite.machines.BEP;
import aztech.milite.machines.components.EnergyComponent;
import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.MachineComponent;
import aztech.milite.machines.models.MachineCasing;
import aztech.milite.machines.models.MachineCasings;
import aztech.milite.machines.models.MachineModelClientData;
import aztech.milite.util.Tickable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class HatchBlockEntity extends MachineBlockEntity implements Tickable {
    public HatchBlockEntity(BEP bep, MachineGuiParameters guiParams, OrientationComponent.@Nullable Params orientationParams) {
        super(bep, guiParams, orientationParams);

        registerComponents(new MachineComponent.ClientOnly() {
            @Override
            public void writeClientNbt(CompoundTag tag, HolderLookup.Provider registries) {
                if (matchedCasing != null) {
                    tag.putString("matchedCasing", matchedCasing.toString());
                }
            }

            @Override
            public void readClientNbt(CompoundTag tag, HolderLookup.Provider registries) {
                matchedCasing = tag.contains("matchedCasing") ? ResourceLocation.tryParse(tag.getString("matchedCasing")) : null;
            }
        });
    }

    @Nullable
    private ResourceLocation lastSyncedMachineCasing = null;
    @Nullable
    private ResourceLocation matchedCasing = null;

    public abstract HatchType getHatchType();

    /**
     * Return true if this hatch upgrades steam multiblocks to steel tier.
     */
    public abstract boolean upgradesToSteel();

    public boolean isMatched() {
        return matchedCasing != null;
    }

    // This amazing function is called when the block entity is loaded.
    @Override
    public void clearRemoved() {
        super.clearRemoved();
        clearMachineLock();
    }

    public void unlink() {
        matchedCasing = null;
        clearMachineLock();
    }

    public void link(MachineCasing casing) {
        matchedCasing = casing.key;
    }

    protected void clearMachineLock() {
        for (ConfigurableItemStack itemStack : getInventory().getItemStacks()) {
            itemStack.disableMachineLock();
        }
        for (ConfigurableFluidStack fluidStack : getInventory().getFluidStacks()) {
            fluidStack.disableMachineLock();
        }
    }

    @Override
    public MachineModelClientData getMachineModelData() {
        MachineCasing casing = isMatched() ? MachineCasings.get(matchedCasing) : null;
        MachineModelClientData data = new MachineModelClientData(casing);
        orientation.writeModelData(data);
        return data;
    }

    @Override
    public void onPlaced(@Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(placer, itemStack);
        if (orientation.params.hasOutput) {
            orientation.outputDirection = orientation.outputDirection.getOpposite();
        }
    }

    @Override
    public void tick() {
        if (level.isClientSide()) {
            return;
        }

        if (!Objects.equals(lastSyncedMachineCasing, matchedCasing)) {
            lastSyncedMachineCasing = matchedCasing;
            sync();
        }

        tickTransfer();
        setChanged();
    }

    protected void tickTransfer() {}

    public void appendItemInputs(List<ConfigurableItemStack> list) {}

    public void appendItemOutputs(List<ConfigurableItemStack> list) {}

    public void appendFluidInputs(List<ConfigurableFluidStack> list) {}

    public void appendFluidOutputs(List<ConfigurableFluidStack> list) {}

    public void appendEnergyInputs(List<EnergyComponent> list) {}

    public void appendEnergyOutputs(List<EnergyComponent> list) {}
}
