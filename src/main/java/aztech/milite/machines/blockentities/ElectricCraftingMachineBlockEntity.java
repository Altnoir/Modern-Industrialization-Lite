package aztech.milite.machines.blockentities;

import aztech.milite.api.energy.CableTier;
import aztech.milite.api.energy.CableTierHolder;
import aztech.milite.api.machine.holder.EnergyComponentHolder;
import aztech.milite.machines.BEP;
import aztech.milite.machines.components.*;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.guicomponents.EnergyBar;
import aztech.milite.machines.guicomponents.ProgressBar;
import aztech.milite.machines.guicomponents.RecipeEfficiencyBar;
import aztech.milite.machines.guicomponents.SlotPanel;
import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.init.MachineRegistrationHelper;
import aztech.milite.machines.init.MachineTier;
import aztech.milite.machines.models.MachineModelClientData;
import aztech.milite.machines.recipe.MachineRecipeType;
import aztech.milite.MICapabilities;
import aztech.milite.util.Simulation;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ElectricCraftingMachineBlockEntity extends AbstractCraftingMachineBlockEntity implements EnergyComponentHolder, CableTierHolder {
    public ElectricCraftingMachineBlockEntity(BEP bep, MachineRecipeType recipeType, MachineInventoryComponent inventory,
            MachineGuiParameters guiParams, EnergyBar.Params energyBarParams, ProgressBar.Params progressBarParams,
            RecipeEfficiencyBar.Params efficiencyBarParams, MachineTier tier, MachineLevel level, long euCapacity) {
        super(bep, recipeType, inventory, guiParams, progressBarParams, tier, level);
        this.redstoneControl = new RedstoneControlComponent();
        this.casing = new CasingComponent(MachineRegistrationHelper.casingTierOf(level));
        this.overdrive = new OverdriveComponent();
        this.energy = new EnergyComponent(this, casing::getEuCapacity);
        this.insertable = energy.buildInsertable();
        registerGuiComponent(new EnergyBar(energyBarParams, energy::getEu, energy::getCapacity));
        registerGuiComponent(new RecipeEfficiencyBar(efficiencyBarParams, crafter));
        registerGuiComponent(new SlotPanel(this)
                .withRedstoneControl(redstoneControl)
                .withOverdrive(overdrive));
        this.registerComponents(redstoneControl, casing, overdrive, energy);
    }

    private final RedstoneControlComponent redstoneControl;
    private final CasingComponent casing;
    private final OverdriveComponent overdrive;
    private final EnergyComponent energy;
    private final IEnergyStorage insertable;

    @Override
    public boolean isEnabled() {
        return redstoneControl.doAllowNormalOperation(this);
    }

    @Override
    public long consumeEu(long max, Simulation simulation) {
        return energy.consumeEu(max, simulation);
    }

    @Override
    public MachineModelClientData getMachineModelData() {
        MachineModelClientData data = new MachineModelClientData(casing.getCasing());
        orientation.writeModelData(data);
        data.isActive = isActiveComponent.isActive;
        return data;
    }

    public static void registerEnergyApi(BlockEntityType<?> bet) {
        MICapabilities.onEvent(event -> {
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, bet, (be, direction) -> ((ElectricCraftingMachineBlockEntity) be).insertable);
        });
    }

    @Override
    protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face) {
        var result = super.useItemOn(player, hand, face);
        if (!result.consumesAction()) {
            result = redstoneControl.onUse(this, player, hand);
        }
        if (!result.consumesAction()) {
            result = overdrive.onUse(this, player, hand);
        }
        return result;
    }

    @Override
    public long getMaxRecipeEu() {
        return tier.getMaxEu();
    }

    @Override
    public boolean isOverdriving() {
        return overdrive.shouldOverdrive();
    }

    @Override
    public EnergyComponent getEnergyComponent() {
        return energy;
    }

    @Override
    public CableTier getCableTier() {
        return casing.getCableTier();
    }
}
