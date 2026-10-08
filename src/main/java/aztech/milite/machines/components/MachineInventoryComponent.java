package aztech.milite.machines.components;

import aztech.milite.inventory.ChangeListener;
import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.ConfigurableItemStack;
import aztech.milite.inventory.MIInventory;
import aztech.milite.inventory.SlotPositions;
import aztech.milite.machines.MachineComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class MachineInventoryComponent implements CrafterComponent.Inventory, MachineComponent.ServerOnly {
    public final int itemInputCount;
    public final int itemOutputCount;
    public final int fluidInputCount;
    public final int fluidOutputCount;

    public final MIInventory inventory;

    private int invHash = 0;
    private final ChangeListener listener = new ChangeListener() {
        @Override
        protected void onChange() {
            invHash++;
        }

        @Override
        protected boolean isValid(Object token) {
            return true;
        }
    };

    public MachineInventoryComponent(List<ConfigurableItemStack> itemInputs, List<ConfigurableItemStack> itemOutputs,
            List<ConfigurableFluidStack> fluidInputs, List<ConfigurableFluidStack> fluidOutputs, SlotPositions itemPositions,
            SlotPositions fluidPositions) {
        this.itemInputCount = itemInputs.size();
        this.itemOutputCount = itemOutputs.size();
        this.fluidInputCount = fluidInputs.size();
        this.fluidOutputCount = fluidOutputs.size();

        List<ConfigurableItemStack> itemStacks = new ArrayList<>();
        itemStacks.addAll(itemInputs);
        itemStacks.addAll(itemOutputs);
        List<ConfigurableFluidStack> fluidStacks = new ArrayList<>();
        fluidStacks.addAll(fluidInputs);
        fluidStacks.addAll(fluidOutputs);

        this.inventory = new MIInventory(itemStacks, fluidStacks, itemPositions, fluidPositions);
        this.inventory.addListener(listener, null);
    }

    @Override
    public List<ConfigurableItemStack> getItemInputs() {
        return inventory.getItemStacks().subList(0, itemInputCount);
    }

    @Override
    public List<ConfigurableItemStack> getItemOutputs() {
        return inventory.getItemStacks().subList(itemInputCount, itemInputCount + itemOutputCount);
    }

    @Override
    public List<ConfigurableFluidStack> getFluidInputs() {
        return inventory.getFluidStacks().subList(0, fluidInputCount);
    }

    @Override
    public List<ConfigurableFluidStack> getFluidOutputs() {
        return inventory.getFluidStacks().subList(fluidInputCount, fluidInputCount + fluidOutputCount);
    }

    @Override
    public int hash() {
        return invHash;
    }

    @Override
    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        this.inventory.writeNbt(tag, registries);
    }

    @Override
    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        this.inventory.readNbt(tag, registries, isUpgradingMachine);
        this.inventory.addListener(listener, null);
    }
}
