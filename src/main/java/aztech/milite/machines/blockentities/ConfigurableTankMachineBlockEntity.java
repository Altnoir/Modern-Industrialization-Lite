package aztech.milite.machines.blockentities;

import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.MIInventory;
import aztech.milite.inventory.SlotPositions;
import aztech.milite.machines.BEP;
import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.guicomponents.AutoExtract;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.models.MachineCasings;
import aztech.milite.machines.models.MachineModelClientData;
import aztech.milite.util.Tickable;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigurableTankMachineBlockEntity extends MachineBlockEntity implements Tickable {
    private final MIInventory inventory;

    public ConfigurableTankMachineBlockEntity(BEP bep) {
        super(bep, new MachineGuiParameters.Builder("configurable_tank", true).backgroundHeight(173).build(),
                new OrientationComponent.Params(true, false, true));

        List<ConfigurableFluidStack> stacks = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            stacks.add(ConfigurableFluidStack.standardIOSlot(16 * FluidType.BUCKET_VOLUME, true));
        }
        SlotPositions fluidPositions = new SlotPositions.Builder().addSlots(62, 18, 3, 3).build();
        inventory = new MIInventory(Collections.emptyList(), stacks, SlotPositions.empty(), fluidPositions);

        registerGuiComponent(new AutoExtract(orientation));
        registerComponents(inventory);
    }

    @Override
    public MIInventory getInventory() {
        return inventory;
    }

    @Override
    public MachineModelClientData getMachineModelData() {
        MachineModelClientData data = new MachineModelClientData(MachineCasings.CONFIGURABLE_TANK);
        orientation.writeModelData(data);
        return data;
    }

    @Override
    public void tick() {
        if (!level.isClientSide()) {
            if (orientation.extractFluids) {
                inventory.autoExtractFluids(level, worldPosition, orientation.outputDirection);
            }
        }
    }
}
