package aztech.milite.machines.blockentities.multiblocks;

import aztech.milite.api.machine.holder.EnergyListComponentHolder;
import aztech.milite.api.machine.holder.MultiblockInventoryComponentHolder;
import aztech.milite.inventory.MIInventory;
import aztech.milite.machines.BEP;
import aztech.milite.machines.components.*;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.guicomponents.GeneratorMultiblockGui;
import aztech.milite.machines.guicomponents.SlotPanel;
import aztech.milite.machines.models.MachineModelClientData;
import aztech.milite.machines.multiblocks.HatchBlockEntity;
import aztech.milite.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.milite.machines.multiblocks.ShapeMatcher;
import aztech.milite.machines.multiblocks.ShapeTemplate;
import aztech.milite.util.Simulation;
import aztech.milite.util.Tickable;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class GeneratorMultiblockBlockEntity extends MultiblockMachineBlockEntity implements Tickable,
        EnergyListComponentHolder, MultiblockInventoryComponentHolder {
    public GeneratorMultiblockBlockEntity(BEP bep,
            String name,
            ShapeTemplate shapeTemplate,
            FluidItemConsumerComponent fluidConsumer) {
        super(bep, new MachineGuiParameters.Builder(name, false)
                .backgroundHeight(200).build(),
                new OrientationComponent.Params(false, false, false));

        this.activeShape = new ActiveShapeComponent(new ShapeTemplate[] { shapeTemplate });
        this.inventory = new MultiblockInventoryComponent();
        this.isActiveComponent = new IsActiveComponent();
        this.fluidConsumer = fluidConsumer;
        this.redstoneControl = new RedstoneControlComponent();

        this.registerComponents(activeShape, isActiveComponent, fluidConsumer, redstoneControl);
        registerGuiComponent(new SlotPanel(this).withRedstoneControl(redstoneControl));
        registerGuiComponent(new GeneratorMultiblockGui(() -> shapeValid.shapeValid, () -> lastRfProduction, fluidConsumer.maxRfProduction));
    }

    private boolean allowNormalOperation = false;

    private final ActiveShapeComponent activeShape;
    private final MultiblockInventoryComponent inventory;
    private final IsActiveComponent isActiveComponent;
    private final RedstoneControlComponent redstoneControl;
    private final List<EnergyComponent> energyOutputs = new ArrayList<>();
    private final FluidItemConsumerComponent fluidConsumer;
    private long lastRfProduction;

    public ShapeTemplate getActiveShape() {
        return activeShape.getActiveShape();
    }

    @Override
    public List<EnergyComponent> getEnergyComponents() {
        return energyOutputs;
    }

    @Override
    public MultiblockInventoryComponent getMultiblockInventoryComponent() {
        return inventory;
    }

    @Override
    public final MIInventory getInventory() {
        return MIInventory.EMPTY;
    }

    @Override
    public final MachineModelClientData getMachineModelData() {
        return new MachineModelClientData(null, orientation.facingDirection).active(isActiveComponent.isActive);
    }

    @Override
    public final void tick() {
        if (!level.isClientSide) {
            link();
            lastRfProduction = 0;
            if (allowNormalOperation) {
                if (this.redstoneControl.doAllowNormalOperation(this)) {
                    long rfProduced = fluidConsumer.getRfProduction(inventory.getFluidInputs(),
                            inventory.getItemInputs(),
                            insertEnergy(Long.MAX_VALUE, Simulation.SIMULATE));
                    lastRfProduction = rfProduced;
                    insertEnergy(rfProduced, Simulation.ACT);
                    isActiveComponent.updateActive(rfProduced != 0, this);
                } else {
                    isActiveComponent.updateActive(false, this);
                }
            } else {
                isActiveComponent.updateActive(false, this);
            }
            setChanged();
        }
    }

    public long insertEnergy(long value, Simulation simulation) {
        long rem = value;
        long inserted = 0;
        for (EnergyComponent e : energyOutputs) {
            if (rem > 0) {
                inserted += e.insertRf(rem, simulation);
                rem -= inserted;
            }
        }
        return inserted;
    }

    @Override
    protected void onRematch(ShapeMatcher shapeMatcher) {
        allowNormalOperation = false;
        if (shapeMatcher.isMatchSuccessful()) {
            inventory.rebuild(shapeMatcher);
            allowNormalOperation = true;

            energyOutputs.clear();
            for (HatchBlockEntity hatch : shapeMatcher.getMatchedHatches()) {
                hatch.appendEnergyOutputs(energyOutputs);
            }
        }
    }

    @Override
    public List<Component> getTooltips() {
        return fluidConsumer.getTooltips();
    }
}
