package aztech.milite.machines.components;

import aztech.milite.api.machine.component.EnergyAccess;
import aztech.milite.machines.MachineComponent;
import aztech.milite.util.Simulation;
import com.google.common.base.Preconditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.function.Supplier;

public class EnergyComponent implements MachineComponent.ServerOnly, EnergyAccess {
    private long storedRf;
    private final Supplier<Long> capacity;
    private final BlockEntity blockEntity; // used to call setChanged()

    public EnergyComponent(BlockEntity blockEntity, Supplier<Long> capacity) {
        this.capacity = capacity;
        this.blockEntity = blockEntity;
    }

    public EnergyComponent(BlockEntity blockEntity, long capacity) {
        this.capacity = () -> capacity;
        this.blockEntity = blockEntity;
    }

    @Override
    public long getRf() {
        return Math.min(storedRf, capacity.get());
    }

    @Override
    public long getCapacity() {
        return capacity.get();
    }

    public long getRemainingCapacity() {
        return capacity.get() - getRf();
    }

    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLong("storedEu", getRf());
    }

    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        setRf(tag.getLong("storedEu"), false);
    }

    private void setRf(long rf, boolean update) {
        this.storedRf = Math.min(rf, capacity.get());

        if (update) {
            blockEntity.setChanged();
        }
    }

    public long consumeRf(long max, Simulation simulation) {
        Preconditions.checkArgument(max >= 0, "May not consume < 0 energy.");
        long ext = Math.min(max, getRf());
        if (simulation.isActing()) {
            setRf(getRf() - ext, true);
        }
        return ext;
    }

    public long insertRf(long max, Simulation simulation) {
        Preconditions.checkArgument(max >= 0, "May not insert < 0 energy.");
        long ext = Math.min(max, capacity.get() - getRf());
        if (simulation.isActing()) {
            setRf(getRf() + ext, true);
        }
        return ext;
    }

    private abstract class EnergyStorage implements IEnergyStorage {
        protected long longAmount() {
            return storedRf;
        }

        protected long longCapacity() {
            return capacity.get();
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return (int) Math.min(Integer.MAX_VALUE, insertRf(maxReceive, simulate ? Simulation.SIMULATE : Simulation.ACT));
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return (int) Math.min(Integer.MAX_VALUE, consumeRf(maxExtract, simulate ? Simulation.SIMULATE : Simulation.ACT));
        }

        @Override
        public int getEnergyStored() {
            return (int) Math.min(Integer.MAX_VALUE, storedRf);
        }

        @Override
        public int getMaxEnergyStored() {
            return (int) Math.min(Integer.MAX_VALUE, capacity.get());
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    }

    public IEnergyStorage buildInsertable() {
        return new EnergyStorage() {
            @Override
            public boolean canReceive() {
                return true;
            }
        };
    }

    public IEnergyStorage buildExtractable() {
        return new EnergyStorage() {
            @Override
            public boolean canExtract() {
                return true;
            }
        };
    }

    /**
     * Moves as much energy as possible from {@code source} to {@code target}, using the NeoForge energy API.
     *
     * @return the amount of RF that was actually moved.
     */
    public static long moveEnergy(IEnergyStorage source, IEnergyStorage target) {
        if (!source.canExtract() || !target.canReceive()) {
            return 0;
        }

        int accepted = target.receiveEnergy(Integer.MAX_VALUE, true);
        if (accepted <= 0) {
            return 0;
        }

        int extracted = source.extractEnergy(accepted, true);
        if (extracted <= 0) {
            return 0;
        }

        int received = target.receiveEnergy(extracted, false);
        source.extractEnergy(received, false);
        return received;
    }
}
