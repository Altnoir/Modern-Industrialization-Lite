package aztech.milite.machines.blockentities;

import aztech.milite.MICapabilities;
import aztech.milite.api.energy.CableTier;
import aztech.milite.api.energy.CableTierHolder;
import aztech.milite.api.machine.holder.EnergyComponentHolder;
import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.MIInventory;
import aztech.milite.inventory.SlotPositions;
import aztech.milite.machines.BEP;
import aztech.milite.machines.components.CasingComponent;
import aztech.milite.machines.components.EnergyComponent;
import aztech.milite.machines.components.RedstoneControlComponent;
import aztech.milite.machines.guicomponents.EnergyBar;
import aztech.milite.machines.guicomponents.SlotPanel;
import aztech.milite.machines.models.MachineModelClientData;
import aztech.milite.util.Simulation;
import java.util.Collections;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidType;

public class ElectricWaterPumpBlockEntity extends AbstractWaterPumpBlockEntity implements EnergyComponentHolder, CableTierHolder {
    public ElectricWaterPumpBlockEntity(BEP bep) {
        super(bep, "electric_water_pump");

        long capacity = FluidType.BUCKET_VOLUME * 32;
        this.inventory = new MIInventory(Collections.emptyList(),
                Collections.singletonList(ConfigurableFluidStack.lockedOutputSlot(capacity, Fluids.WATER)), SlotPositions.empty(),
                new SlotPositions.Builder().addSlot(OUTPUT_SLOT_X, OUTPUT_SLOT_Y).build());
        this.redstoneControl = new RedstoneControlComponent();
        this.casing = new CasingComponent();
        this.energy = new EnergyComponent(this, casing::getFeCapacity);
        this.insertable = energy.buildInsertable();

        this.registerComponents(inventory, redstoneControl, casing, energy);

        registerGuiComponent(new EnergyBar(new EnergyBar.Params(18, 32), energy::getFe, energy::getCapacity));
        registerGuiComponent(new SlotPanel(this)
                .withRedstoneControl(redstoneControl));
    }

    private final MIInventory inventory;
    private final RedstoneControlComponent redstoneControl;
    private final CasingComponent casing;
    private final EnergyComponent energy;
    private final IEnergyStorage insertable;

    @Override
    protected long consumeFe(long max) {
        if (redstoneControl.doAllowNormalOperation(
                this)) {
            return energy.consumeFe(max, Simulation.ACT);
        } else {
            return 0;
        }
    }

    @Override
    protected int getWaterMultiplier() {
        return 16;
    }

    @Override
    public MIInventory getInventory() {
        return inventory;
    }

    @Override
    public MachineModelClientData getMachineModelData() {
        MachineModelClientData data = new MachineModelClientData(casing.getCasing());
        data.isActive = isActiveComponent.isActive;
        orientation.writeModelData(data);
        return data;
    }

    @Override
    public EnergyComponent getEnergyComponent() {
        return energy;
    }

    public static void registerEnergyApi(BlockEntityType<?> bet) {
        MICapabilities.onEvent(event -> {
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, bet, (be, direction) -> ((ElectricWaterPumpBlockEntity) be).insertable);
        });
    }

    @Override
    protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face) {
        var result = super.useItemOn(player, hand, face);
        if (!result.consumesAction()) {
            result = redstoneControl.onUse(this, player, hand);
        }
        return result;
    }

    @Override
    public CableTier getCableTier() {
        return casing.getCableTier();
    }
}
