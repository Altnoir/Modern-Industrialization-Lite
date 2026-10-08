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

/**
 * Implemented by MI items that can store RF, such as the portable storage unit or the gravi chestplate.
 *
 * <p>
 * MI Lite exposes these through NeoForge's standard {@code Capabilities.EnergyStorage.ITEM} capability so that
 * chargers and energy pipes from other mods can fill them up.
 */
public interface EnergyItem {
    long getEnergyCapacity(ItemStack stack);

    /**
     * @return the maximum amount of RF that can be pushed into this item every time it is charged.
     */
    long getEnergyMaxInput(ItemStack stack);

    /**
     * @return the maximum amount of RF that can be pulled out of this item.
     */
    long getEnergyMaxOutput(ItemStack stack);

    long getStoredEnergy(ItemStack stack);

    void setStoredEnergy(ItemStack stack, long amount);
}
