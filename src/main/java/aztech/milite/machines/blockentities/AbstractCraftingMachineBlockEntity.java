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

package aztech.milite.machines.blockentities;

import aztech.milite.api.machine.holder.CrafterComponentHolder;
import aztech.milite.inventory.MIInventory;
import aztech.milite.machines.BEP;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.components.CrafterComponent;
import aztech.milite.machines.components.IsActiveComponent;
import aztech.milite.machines.components.MachineInventoryComponent;
import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.guicomponents.AutoExtract;
import aztech.milite.machines.guicomponents.MachineProblemsDisplay;
import aztech.milite.machines.guicomponents.ProgressBar;
import aztech.milite.machines.guicomponents.ReiSlotLocking;
import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.init.MachineTier;
import aztech.milite.machines.recipe.MachineRecipeType;
import aztech.milite.util.Tickable;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.Nullable;

public abstract class AbstractCraftingMachineBlockEntity extends MachineBlockEntity implements CrafterComponent.Behavior, Tickable,
        CrafterComponentHolder {
    public AbstractCraftingMachineBlockEntity(BEP bep, MachineRecipeType recipeType, MachineInventoryComponent inventory,
            MachineGuiParameters guiParams, ProgressBar.Params progressBarParams, MachineTier tier, MachineLevel level) {
        super(bep, guiParams, new OrientationComponent.Params(true, inventory.itemOutputCount > 0, inventory.fluidOutputCount > 0));
        this.inventory = inventory;
        this.crafter = new CrafterComponent(this, inventory, this);
        this.type = recipeType;
        this.tier = tier;
        this.machineLevel = level;
        this.isActiveComponent = new IsActiveComponent();
        registerGuiComponent(new AutoExtract(orientation));
        registerGuiComponent(new ProgressBar(progressBarParams, crafter::getProgress));
        registerGuiComponent(new MachineProblemsDisplay(crafter::matchesMultipleRecipes));
        registerGuiComponent(new ReiSlotLocking(crafter::lockRecipe, () -> true));
        this.registerComponents(crafter, this.inventory, isActiveComponent);
    }

    private final MachineInventoryComponent inventory;
    protected final CrafterComponent crafter;

    private final MachineRecipeType type;
    public final MachineTier tier;
    private final MachineLevel machineLevel;
    protected IsActiveComponent isActiveComponent;

    @Override
    public MachineLevel getMachineLevel() {
        return machineLevel;
    }

    @Override
    public MachineRecipeType recipeType() {
        return type;
    }

    @Override
    public CrafterComponent getCrafterComponent() {
        return crafter;
    }

    @Override
    public long getBaseRecipeEu() {
        return tier.getBaseEu();
    }

    @Override
    public long getMaxRecipeEu() {
        return tier.getMaxEu();
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            boolean newActive = crafter.tickRecipe();
            isActiveComponent.updateActive(newActive, this);
            if (orientation.extractItems) {
                inventory.inventory.autoExtractItems(level, worldPosition, orientation.outputDirection);
            }
            if (orientation.extractFluids) {
                inventory.inventory.autoExtractFluids(level, worldPosition, orientation.outputDirection);
            }
            setChanged();
        }
    }

    @Override
    public MIInventory getInventory() {
        return inventory.inventory;
    }

    @Override
    public ServerLevel getCrafterWorld() {
        return (ServerLevel) level;
    }

    @Override
    @Nullable
    public UUID getOwnerUuid() {
        return placedBy.placerId;
    }
}
