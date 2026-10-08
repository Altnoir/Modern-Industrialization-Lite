package aztech.milite.machines.blockentities.multiblocks;

import aztech.milite.api.machine.holder.EnergyListComponentHolder;
import aztech.milite.machines.BEP;
import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.guicomponents.SlotPanel;
import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.init.MIMachineRecipeTypes;
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
