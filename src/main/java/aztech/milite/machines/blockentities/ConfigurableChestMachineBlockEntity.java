package aztech.milite.machines.blockentities;

import aztech.milite.inventory.ConfigurableItemStack;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigurableChestMachineBlockEntity extends MachineBlockEntity implements Tickable {
    private final MIInventory inventory;

    public ConfigurableChestMachineBlockEntity(BEP bep) {
        super(bep, new MachineGuiParameters.Builder("configurable_chest", true).backgroundHeight(180).build(),
                new OrientationComponent.Params(true, true, false));

        List<ConfigurableItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < 27; i++) {
            stacks.add(ConfigurableItemStack.standardIOSlot(true));
        }
        SlotPositions itemPositions = new SlotPositions.Builder().addSlots(8, 30, 9, 3).build();
        inventory = new MIInventory(stacks, Collections.emptyList(), itemPositions, SlotPositions.empty());

        registerGuiComponent(new AutoExtract(orientation));
        registerComponents(inventory);
    }

    @Override
    public MIInventory getInventory() {
        return inventory;
    }

    @Override
    public MachineModelClientData getMachineModelData() {
        MachineModelClientData data = new MachineModelClientData(MachineCasings.STEEL_CRATE);
        orientation.writeModelData(data);
        return data;
    }

    @Override
    public void tick() {
        if (!level.isClientSide()) {
            if (orientation.extractItems) {
                inventory.autoExtractItems(level, worldPosition, orientation.outputDirection);
            }
        }
    }
}
