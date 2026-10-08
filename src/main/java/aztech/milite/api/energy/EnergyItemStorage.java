/*
 * MIT License
 *
 * Copyright (c) 2026 Altnoir Null々
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
