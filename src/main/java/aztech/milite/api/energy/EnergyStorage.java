package aztech.milite.api.energy;

import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Small helpers for MI's FE-based energy handling. MI Lite does not ship its own cables or energy network:
 * machines simply expose NeoForge's standard {@code Capabilities.EnergyStorage.BLOCK} capability, so any
 * FE cable, energy pipe or storage block from another mod can power them.
 */
public final class EnergyStorage {
    private EnergyStorage() {}

    /**
     * An energy storage that can supply an infinite amount of FE, but cannot receive any.
     */
    public static final IEnergyStorage CREATIVE = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return maxExtract;
        }

        @Override
        public int getEnergyStored() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getMaxEnergyStored() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    };
}
