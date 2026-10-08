package aztech.milite.api.energy;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Exposes an {@link EnergyItem} stack through NeoForge's int-based energy API.
 */
public class EnergyItemStorage implements IEnergyStorage {
    private final ItemStack stack;
    private final EnergyItem item;

    public EnergyItemStorage(ItemStack stack, EnergyItem item) {
        this.stack = stack;
        this.item = item;
    }

    private static int clamp(long value) {
        return (int) Math.max(0, Math.min(Integer.MAX_VALUE, value));
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        long capacity = item.getEnergyCapacity(stack);
        long stored = item.getStoredEnergy(stack);
        long accepted = Math.min(Math.min(maxReceive, item.getEnergyMaxInput(stack)), capacity - stored);
        if (accepted <= 0) {
            return 0;
        }
        if (!simulate) {
            item.setStoredEnergy(stack, stored + accepted);
        }
        return (int) accepted;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        long stored = item.getStoredEnergy(stack);
        long extracted = Math.min(Math.min(maxExtract, item.getEnergyMaxOutput(stack)), stored);
        if (extracted <= 0) {
            return 0;
        }
        if (!simulate) {
            item.setStoredEnergy(stack, stored - extracted);
        }
        return (int) extracted;
    }

    @Override
    public int getEnergyStored() {
        return clamp(item.getStoredEnergy(stack));
    }

    @Override
    public int getMaxEnergyStored() {
        return clamp(item.getEnergyCapacity(stack));
    }

    @Override
    public boolean canExtract() {
        return item.getEnergyMaxOutput(stack) > 0;
    }

    @Override
    public boolean canReceive() {
        return item.getEnergyMaxInput(stack) > 0;
    }
}
