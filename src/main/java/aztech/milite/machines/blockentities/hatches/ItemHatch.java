package aztech.milite.machines.blockentities.hatches;

import aztech.milite.inventory.ConfigurableItemStack;
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
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.List;

public class ItemHatch extends HatchBlockEntity {
    public ItemHatch(BEP bep, MachineGuiParameters guiParams, boolean input, boolean upgradesToSteel, MIInventory inventory) {
        super(bep, guiParams, OrientationComponent.Params.noFacing(true, false));

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
        return input ? HatchTypes.ITEM_INPUT : HatchTypes.ITEM_OUTPUT;
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
    public void appendItemInputs(List<ConfigurableItemStack> list) {
        if (input) {
            list.addAll(inventory.getItemStacks());
        }
    }

    @Override
    public void appendItemOutputs(List<ConfigurableItemStack> list) {
        if (!input) {
            list.addAll(inventory.getItemStacks());
        }
    }

    @Override
    protected void tickTransfer() {
        if (orientation.extractItems) {
            if (input) {
                inventory.autoInsertItems(level, worldPosition, orientation.outputDirection);
            } else {
                inventory.autoExtractItems(level, worldPosition, orientation.outputDirection);
            }
        }
    }

    @Override
    public List<Component> getTooltips(Item.TooltipContext context) {
        return List.of(MITooltips.line(MIText.HatchCapacityItem)
                .arg(inventory.getItemStacks().size())
                .build());
    }
}
