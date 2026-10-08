package aztech.milite.machines.blockentities;

import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.MIInventory;
import aztech.milite.inventory.SlotPositions;
import aztech.milite.machines.BEP;
import aztech.milite.machines.helper.SteamHelper;
import aztech.milite.machines.models.MachineModelClientData;
import aztech.milite.MIFluids;
import aztech.milite.util.Simulation;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SteamWaterPumpBlockEntity extends AbstractWaterPumpBlockEntity {
    public SteamWaterPumpBlockEntity(BEP bep, boolean bronze) {
        super(bep, bronze ? "bronze_water_pump" : "steel_water_pump");
        this.bronze = bronze;

        long capacity = FluidType.BUCKET_VOLUME * (bronze ? 8 : 16);
        List<ConfigurableFluidStack> fluidStacks = Arrays.asList(ConfigurableFluidStack.lockedInputSlot(capacity, MIFluids.STEAM.asFluid()),
                ConfigurableFluidStack.lockedOutputSlot(capacity, Fluids.WATER));
        SlotPositions fluidPositions = new SlotPositions.Builder().addSlot(21, 30).addSlot(OUTPUT_SLOT_X, OUTPUT_SLOT_Y).build();
        this.inventory = new MIInventory(Collections.emptyList(), fluidStacks, SlotPositions.empty(), fluidPositions);
        this.registerComponents(inventory);
    }

    public final boolean bronze;
    private final MIInventory inventory;

    @Override
    protected long consumeRf(long max) {
        return SteamHelper.consumeSteamRf(inventory.getFluidStacks(), max, Simulation.ACT);
    }

    @Override
    protected int getWaterMultiplier() {
        return bronze ? 1 : 2;
    }

    @Override
    public MIInventory getInventory() {
        return inventory;
    }

    @Override
    public MachineModelClientData getMachineModelData() {
        MachineModelClientData data = new MachineModelClientData();
        data.isActive = isActiveComponent.isActive;
        orientation.writeModelData(data);
        return data;
    }
}
