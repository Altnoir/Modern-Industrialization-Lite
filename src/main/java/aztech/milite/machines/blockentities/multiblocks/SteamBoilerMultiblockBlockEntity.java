package aztech.milite.machines.blockentities.multiblocks;

import aztech.milite.inventory.MIInventory;
import aztech.milite.machines.BEP;
import aztech.milite.machines.components.*;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.guicomponents.ProgressBar;
import aztech.milite.machines.guicomponents.SlotPanel;
import aztech.milite.machines.guicomponents.TemperatureBar;
import aztech.milite.machines.models.MachineModelClientData;
import aztech.milite.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.milite.machines.multiblocks.ShapeMatcher;
import aztech.milite.machines.multiblocks.ShapeTemplate;
import aztech.milite.MIFluids;
import aztech.milite.MIText;
import aztech.milite.MITooltips;
import aztech.milite.util.Tickable;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class SteamBoilerMultiblockBlockEntity extends MultiblockMachineBlockEntity implements Tickable {
    private final ShapeTemplate shapeTemplate;
    private final IsActiveComponent isActiveComponent;
    private final RedstoneControlComponent redstoneControl;

    private final MultiblockInventoryComponent inventory;

    private final SteamHeaterComponent steamHeater;
    private final FuelBurningComponent fuelBurning;

    public final boolean highPressure;

    public SteamBoilerMultiblockBlockEntity(BEP bep, ShapeTemplate shapeTemplate, String name, long maxFeProduction, boolean highPressure) {
        super(bep, new MachineGuiParameters.Builder(name, false).build(), new OrientationComponent.Params(false, false, false));

        this.highPressure = highPressure;

        this.isActiveComponent = new IsActiveComponent();
        this.shapeTemplate = shapeTemplate;
        this.inventory = new MultiblockInventoryComponent();
        this.redstoneControl = new RedstoneControlComponent();

        this.steamHeater = new SteamHeaterComponent(2500, maxFeProduction,
                maxFeProduction / 32, !highPressure, highPressure, true);
        this.fuelBurning = new FuelBurningComponent(steamHeater, 2);

        this.registerComponents(isActiveComponent, steamHeater, fuelBurning, redstoneControl);

        ProgressBar.Params PROGRESS_BAR = new ProgressBar.Params(82, 30, "furnace", 14, 14, true);
        TemperatureBar.Params TEMPERATURE_BAR = new TemperatureBar.Params(42, 55, 2500);

        registerGuiComponent(new ProgressBar(PROGRESS_BAR, () -> (float) fuelBurning.getBurningProgress()));
        registerGuiComponent(new TemperatureBar(TEMPERATURE_BAR, () -> (int) steamHeater.getTemperature()));
        registerGuiComponent(new SlotPanel(this).withRedstoneControl(redstoneControl));
    }

    @Override
    protected void onRematch(ShapeMatcher shapeMatcher) {
        if (shapeMatcher.isMatchSuccessful()) {
            inventory.rebuild(shapeMatcher);
        }
    }

    @Override
    public ShapeTemplate getActiveShape() {
        return shapeTemplate;
    }

    @Override
    public MIInventory getInventory() {
        return MIInventory.EMPTY;
    }

    @Override
    public MachineModelClientData getMachineModelData() {
        return new MachineModelClientData(null, orientation.facingDirection).active(isActiveComponent.isActive);
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            link();

            if (shapeValid.shapeValid) {
                if (redstoneControl.doAllowNormalOperation(this)) {
                    steamHeater.tick(inventory.getFluidInputs(), inventory.getFluidOutputs());
                    fuelBurning.tick(inventory.getItemInputs(), inventory.getFluidInputs(), true);
                    this.isActiveComponent.updateActive(fuelBurning.isBurning(), this);
                } else {
                    steamHeater.tickDisabled();
                    fuelBurning.tick(inventory.getItemInputs(), inventory.getFluidInputs(), false);
                    this.isActiveComponent.updateActive(false, this);
                }
            } else {
                steamHeater.tickDisabled();
                fuelBurning.clearActiveFuel();
                this.isActiveComponent.updateActive(false, this);
            }
            setChanged();
        }
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
    public List<Component> getTooltips() {
        List<Component> tooltips = fuelBurning.getTooltips(true);

        tooltips.add(new MITooltips.Line(MIText.ContinuousOperation).build());

        if (highPressure) {
            tooltips.add(new MITooltips.Line(MIText.AcceptLowOrHighPressure).arg(MIFluids.HIGH_PRESSURE_WATER).arg(MIFluids.HIGH_PRESSURE_HEAVY_WATER)
                    .build());
        } else {
            tooltips.add(new MITooltips.Line(MIText.AcceptLowOrHighPressure).arg(Fluids.WATER).arg(MIFluids.HEAVY_WATER).build());
        }

        return tooltips;
    }
}
