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

package aztech.milite.machines.components;

import aztech.milite.api.machine.component.InventoryAccess;
import aztech.milite.inventory.AbstractConfigurableStack;
import aztech.milite.inventory.ChangeListener;
import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.ConfigurableItemStack;
import aztech.milite.machines.multiblocks.HatchBlockEntity;
import aztech.milite.machines.multiblocks.ShapeMatcher;
import aztech.milite.thirdparty.fabrictransfer.api.storage.TransferVariant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

public class MultiblockInventoryComponent implements CrafterComponent.Inventory, InventoryAccess {
    private final List<ConfigurableItemStack> itemInputs = new ArrayList<>();
    private final List<ConfigurableItemStack> itemOutputs = new ArrayList<>();
    private final List<ConfigurableFluidStack> fluidInputs = new ArrayList<>();
    private final List<ConfigurableFluidStack> fluidOutputs = new ArrayList<>();

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

    public void rebuild(ShapeMatcher shapeMatcher) {
        // Sort the hatches by height for the distillation tower
        List<HatchBlockEntity> sortedHatches = new ArrayList<>(shapeMatcher.getMatchedHatches());
        sortedHatches.sort(Comparator.comparing(h -> h.getBlockPos().getY()));
        // Accumulate the slots
        rebuildList(sortedHatches, itemInputs, HatchBlockEntity::appendItemInputs);
        rebuildList(sortedHatches, itemOutputs, HatchBlockEntity::appendItemOutputs);
        rebuildList(sortedHatches, fluidInputs, HatchBlockEntity::appendFluidInputs);
        rebuildList(sortedHatches, fluidOutputs, HatchBlockEntity::appendFluidOutputs);
        // Update hash in case the slots have changed
        invHash++;
    }

    private <T, Stack extends AbstractConfigurableStack<T, ? extends TransferVariant<T>>> void rebuildList(
            List<HatchBlockEntity> sortedHatches, List<Stack> stacks, BiConsumer<HatchBlockEntity, List<Stack>> appender) {
        for (var stack : stacks) {
            stack.removeListener(listener);
        }

        stacks.clear();
        // Add all hatch slots
        for (HatchBlockEntity hatch : sortedHatches) {
            appender.accept(hatch, stacks);
        }

        listener.listenAll(stacks, null);
    }

    @Override
    public List<ConfigurableItemStack> getItemInputs() {
        return itemInputs;
    }

    @Override
    public List<ConfigurableItemStack> getItemOutputs() {
        return itemOutputs;
    }

    @Override
    public List<ConfigurableFluidStack> getFluidInputs() {
        return fluidInputs;
    }

    @Override
    public List<ConfigurableFluidStack> getFluidOutputs() {
        return fluidOutputs;
    }

    @Override
    public int hash() {
        return invHash;
    }
}
