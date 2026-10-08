package aztech.milite.machines.blockentities.hatches;

import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.MIInventory;
import aztech.milite.machines.BEP;
import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.guicomponents.AutoExtract;
import aztech.milite.machines.multiblocks.HatchBlockEntity;
import aztech.milite.machines.multiblocks.HatchType;
import aztech.milite.machines.multiblocks.HatchTypes;
import aztech.milite.MIText;
import aztech.milite.MITooltips;
import aztech.milite.util.FluidHelper;
import net.minecraft.network.chat.Component;

import java.util.List;

public class FluidHatch extends HatchBlockEntity {
    public FluidHatch(BEP bep, MachineGuiParameters guiParams, boolean input, boolean upgradesToSteel, MIInventory inventory) {
        super(bep, guiParams, OrientationComponent.Params.noFacing(false, true));

        this.input = input;
        this.upgradesToSteel = upgradesToSteel;
        this.inventory = inventory;

        registerComponents(inventory);
        registerGuiComponent(new AutoExtract(orientation, input));
    }

    private final boolean input;
    private final boolean upgradesToSteel;
    private final MIInventory inventory;

    @Override
    public HatchType getHatchType() {
        return input ? HatchTypes.FLUID_INPUT : HatchTypes.FLUID_OUTPUT;
    }

    @Override
    public boolean upgradesToSteel() {
        return upgradesToSteel;
    }

    @Override
    public MIInventory getInventory() {
        return inventory;
    }

    @Override
    public void appendFluidInputs(List<ConfigurableFluidStack> list) {
        if (input) {
            list.addAll(inventory.getFluidStacks());
        }
    }

    @Override
    public void appendFluidOutputs(List<ConfigurableFluidStack> list) {
        if (!input) {
            list.addAll(inventory.getFluidStacks());
        }
    }

    @Override
    protected void tickTransfer() {
        if (orientation.extractFluids) {
            if (input) {
                inventory.autoInsertFluids(level, worldPosition, orientation.outputDirection);
            } else {
                inventory.autoExtractFluids(level, worldPosition, orientation.outputDirection);
            }
        }
    }

    @Override
    public List<Component> getTooltips() {
        return List.of(MITooltips.line(MIText.HatchCapacityFluid)
                .arg(FluidHelper.getFluidAmountLarge(inventory.getFluidStacks().getFirst().getCapacity()), t -> t.setStyle(MITooltips.NUMBER_TEXT))
                .build());
    }
}
