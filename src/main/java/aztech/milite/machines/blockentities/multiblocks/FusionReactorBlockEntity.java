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

package aztech.milite.machines.blockentities.multiblocks;

import aztech.milite.api.machine.holder.EnergyListComponentHolder;
import aztech.milite.machines.BEP;
import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.guicomponents.SlotPanel;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.multiblocks.ShapeTemplate;
import aztech.milite.machines.recipe.MachineRecipeType;

public class FusionReactorBlockEntity extends AbstractElectricCraftingMultiblockBlockEntity implements EnergyListComponentHolder {
    public FusionReactorBlockEntity(BEP bep, String name, ShapeTemplate shapeTemplate) {
        super(bep, name, new OrientationComponent.Params(false, false, false), new ShapeTemplate[] { shapeTemplate });

        registerGuiComponent(new SlotPanel(this).withRedstoneControl(redstoneControl));
    }

    @Override
    public MachineRecipeType recipeType() {
        return MIMachineRecipeTypes.FUSION_REACTOR;
    }

    /**
     * The casing of the structure is the highly advanced machine hull, which is the titanium level.
     */
    @Override
    public MachineLevel getMachineLevel() {
        return MachineLevel.TITANIUM;
    }

    @Override
    public long getBaseRecipeEu() {
        return 128000;
    }

    @Override
    public long getMaxRecipeEu() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean oneFluidInputPerStack() {
        return true;
    }
}
