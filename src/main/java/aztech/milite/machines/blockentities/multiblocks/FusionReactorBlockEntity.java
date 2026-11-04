package aztech.milite.machines.blockentities.multiblocks;

import aztech.milite.api.machine.holder.EnergyListComponentHolder;
import aztech.milite.machines.BEP;
import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.guicomponents.RedstoneControlGui;
import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.multiblocks.ShapeTemplate;
import aztech.milite.machines.recipe.MachineRecipeType;

public class FusionReactorBlockEntity extends AbstractElectricCraftingMultiblockBlockEntity implements EnergyListComponentHolder {
    public FusionReactorBlockEntity(BEP bep, String name, ShapeTemplate shapeTemplate) {
        super(bep, name, new OrientationComponent.Params(false, false, false), new ShapeTemplate[] { shapeTemplate });

        registerGuiComponent(new RedstoneControlGui(redstoneControl));
    }

    @Override
    public MachineRecipeType recipeType() {
        return MIMachineRecipeTypes.FUSION_REACTOR;
    }

    /**
     * The reactor is the end of the mod's chain, so it runs the highest level of recipe there is.
     */
    @Override
    public MachineLevel getMachineLevel() {
        return MachineLevel.IRIDIUM;
    }


    @Override
    public boolean oneFluidInputPerStack() {
        return true;
    }
}
