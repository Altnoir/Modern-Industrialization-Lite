package aztech.milite.machines.blockentities;

import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.ConfigurableItemStack;
import aztech.milite.inventory.MIFluidStorage;
import aztech.milite.inventory.MIInventory;
import aztech.milite.inventory.SlotPositions;
import aztech.milite.machines.BEP;
import aztech.milite.machines.components.*;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.guicomponents.AutoExtract;
import aztech.milite.machines.guicomponents.ProgressBar;
import aztech.milite.machines.guicomponents.TemperatureBar;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.models.MachineCasings;
import aztech.milite.machines.models.MachineModelClientData;
import aztech.milite.MIFluids;
import aztech.milite.util.Tickable;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.items.wrapper.PlayerInvWrapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BoilerMachineBlockEntity extends MachineBlockEntity implements Tickable {
    public static final int WATER_SLOT_X = 50;
    public static final int WATER_SLOT_Y = 32;

    public static final int INPUT_SLOT_X = 15;
    public static final int INPUT_SLOT_Y = 32;

    public static final int OUTPUT_SLOT_X = 134;
    public static final int OUTPUT_SLOT_Y = 32;

    private final MIInventory inventory;
    public final boolean bronze;

    private final SteamHeaterComponent steamHeater;
    private final FuelBurningComponent fuelBurning;

    protected IsActiveComponent isActiveComponent;

    public BoilerMachineBlockEntity(BEP bep, boolean bronze) {
        super(bep, new MachineGuiParameters.Builder(bronze ? "bronze_boiler" : "steel_boiler", true).backgroundHeight(180).build(),
                new OrientationComponent.Params(true, false, true));

        int capacity = FluidType.BUCKET_VOLUME * (bronze ? 8 : 16);

        List<ConfigurableItemStack> itemStacks = Collections.singletonList(ConfigurableItemStack.standardInputSlot());
        SlotPositions itemPositions = new SlotPositions.Builder().addSlot(INPUT_SLOT_X, INPUT_SLOT_Y).build();

        List<ConfigurableFluidStack> fluidStacks = Arrays.asList(ConfigurableFluidStack.lockedInputSlot(capacity, Fluids.WATER),
                ConfigurableFluidStack.lockedOutputSlot(capacity, MIFluids.STEAM.asFluid()));
        SlotPositions fluidPositions = new SlotPositions.Builder().addSlot(WATER_SLOT_X, WATER_SLOT_Y).addSlot(OUTPUT_SLOT_X, OUTPUT_SLOT_Y).build();
        inventory = new MIInventory(itemStacks, fluidStacks, itemPositions, fluidPositions);

        this.bronze = bronze;
        steamHeater = new SteamHeaterComponent(1500, bronze ? 8 : 16, 8, true, false, false);
        fuelBurning = new FuelBurningComponent(steamHeater, 1);
        this.isActiveComponent = new IsActiveComponent();

        registerGuiComponent(new AutoExtract(orientation));
        ProgressBar.Params progressParams = new ProgressBar.Params(135, 50, "furnace", 14, 14, true);
        TemperatureBar.Params temperatureParams = new TemperatureBar.Params(42, 69, 1500);
        registerGuiComponent(new ProgressBar(progressParams, () -> (float) fuelBurning.getBurningProgress()));
        registerGuiComponent(new TemperatureBar(temperatureParams, () -> (int) steamHeater.getTemperature()));

        this.registerComponents(inventory, isActiveComponent, steamHeater, fuelBurning);
    }

    @Override
    public MIInventory getInventory() {
        return inventory;
    }

    @Override
    public MachineModelClientData getMachineModelData() {
        MachineModelClientData data = new MachineModelClientData(bronze ? MachineCasings.BRICKED_BRONZE : MachineCasings.BRICKED_STEEL);
        data.isActive = isActiveComponent.isActive;
        orientation.writeModelData(data);
        return data;
    }

    @Override
    public void tick() {
        if (level.isClientSide)
            return;

        steamHeater.tick(Collections.singletonList(inventory.getFluidStacks().get(0)), Collections.singletonList(inventory.getFluidStacks().get(1)));
        fuelBurning.tick(Collections.singletonList(inventory.getItemStacks().get(0)), Collections.emptyList(), true);

        if (orientation.extractFluids) {
            getInventory().autoExtractFluids(level, worldPosition, orientation.outputDirection);
        }

        isActiveComponent.updateActive(fuelBurning.isBurning(), this);

        setChanged();
    }

    @Override
    public List<Component> getTooltips(Item.TooltipContext context) {
        return fuelBurning.getTooltips(false);
    }

    @Override
    protected ItemInteractionResult useItemOn(Player player, InteractionHand hand, Direction face) {
        var heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty()) {
            var waterSlotHandler = new MIFluidStorage(inventory.getFluidStacks().subList(0, 1)).fluidHandler;
            var result = FluidUtil.tryEmptyContainerAndStow(heldItem, waterSlotHandler, new PlayerInvWrapper(player.getInventory()),
                    Integer.MAX_VALUE, player, true);
            if (result.isSuccess()) {
                player.setItemInHand(hand, result.getResult());
                return ItemInteractionResult.sidedSuccess(player.level().isClientSide);
            }
        }
        return super.useItemOn(player, hand, face);
    }
}
