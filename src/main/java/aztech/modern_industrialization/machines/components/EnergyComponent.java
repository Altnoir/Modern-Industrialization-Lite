/*
 * MIT License
 *
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package aztech.modern_industrialization.machines.components;

import aztech.modern_industrialization.api.machine.component.EnergyAccess;
import aztech.modern_industrialization.machines.MachineComponent;
import aztech.modern_industrialization.util.Simulation;
import com.google.common.base.Preconditions;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyComponent implements MachineComponent.ServerOnly, EnergyAccess {
    private long storedEu;
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
    public long getEu() {
        return Math.min(storedEu, capacity.get());
    }

    @Override
    public long getCapacity() {
        return capacity.get();
    }

    public long getRemainingCapacity() {
        return capacity.get() - getEu();
    }

    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLong("storedEu", getEu());
    }

    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        setEu(tag.getLong("storedEu"), false);
    }

    private void setEu(long eu, boolean update) {
        this.storedEu = Math.min(eu, capacity.get());

        if (update) {
            blockEntity.setChanged();
        }
    }

    public long consumeEu(long max, Simulation simulation) {
        Preconditions.checkArgument(max >= 0, "May not consume < 0 energy.");
        long ext = Math.min(max, getEu());
        if (simulation.isActing()) {
            setEu(getEu() - ext, true);
        }
        return ext;
    }

    public long insertEu(long max, Simulation simulation) {
        Preconditions.checkArgument(max >= 0, "May not insert < 0 energy.");
        long ext = Math.min(max, capacity.get() - getEu());
        if (simulation.isActing()) {
            setEu(getEu() + ext, true);
        }
        return ext;
    }

    private abstract class EnergyStorage implements IEnergyStorage {
        protected long longAmount() {
            return storedEu;
        }

        protected long longCapacity() {
            return capacity.get();
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return (int) Math.min(Integer.MAX_VALUE, insertEu(maxReceive, simulate ? Simulation.SIMULATE : Simulation.ACT));
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return (int) Math.min(Integer.MAX_VALUE, consumeEu(maxExtract, simulate ? Simulation.SIMULATE : Simulation.ACT));
        }

        @Override
        public int getEnergyStored() {
            return (int) Math.min(Integer.MAX_VALUE, storedEu);
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
