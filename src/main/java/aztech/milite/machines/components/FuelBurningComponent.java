package aztech.milite.machines.components;

import aztech.milite.api.datamaps.FluidFuel;
import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.ConfigurableItemStack;
import aztech.milite.machines.MachineComponent;
import aztech.milite.MIFluids;
import aztech.milite.MIText;
import aztech.milite.MITooltips;
import aztech.milite.util.ItemStackHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class FuelBurningComponent implements MachineComponent {
    /**
     * How many EUs worth of heat can be produced every tick at most.
     */
    public final long maxRfProduction;
    /**
     * How many EUs one degree of heat is worth.
     */
    public final long rfPerDegree;
    public final TemperatureComponent temperature;
    /**
     * Multiplier on the efficiency of the boiler, i.e. the number of EUs produced
     * per burn tick.
     */
    private final double burningItemRfMultiplier;

    /**
     * How many EUs one furnace burn tick is worth. Remembering that 1 furnace
     * recipe = 2 EU/t for 10 seconds, EU_PER_BURN_TICK/2 is the efficiency
     * multiplier.
     */
    public static final long EU_PER_BURN_TICK = 20;

    /**
     * Buffer of EU that was burnt already, and is awaiting to be turned into heat.
     */
    private long burningRfBuffer;
    /**
     * Total EU of the currently burning item. If 0 it means that we are burning fluid instead.
     */
    private long burningItemTotalRf;

    public FuelBurningComponent(TemperatureComponent temperature, long maxRfProduction, long rfPerDegree, double burningItemRfMultiplier) {
        this.temperature = temperature;
        this.maxRfProduction = maxRfProduction;
        this.rfPerDegree = rfPerDegree;
        this.burningItemRfMultiplier = burningItemRfMultiplier;
    }

    public FuelBurningComponent(TemperatureComponent temperature, long maxRfProduction, long rfPerDegree) {
        this(temperature, maxRfProduction, rfPerDegree, 1);
    }

    public FuelBurningComponent(SteamHeaterComponent steamHeater, double burningItemRfMultiplier) {
        this(steamHeater, steamHeater.maxRfProduction, steamHeater.rfPerDegree, burningItemRfMultiplier);
    }

    public FuelBurningComponent(SteamHeaterComponent steamHeater) {
        this(steamHeater, steamHeater.maxRfProduction, steamHeater.rfPerDegree, 1);
    }

    public boolean isBurning() {
        return burningRfBuffer > 0;
    }

    public void clearActiveFuel() {
        burningRfBuffer = 0;
        burningItemTotalRf = 0;
    }

    public double getBurningProgress() {
        if (burningItemTotalRf == 0) {
            return Math.min(1.0, (double) burningRfBuffer / (5 * 20 * maxRfProduction));
        } else {
            return Math.min(1.0, (double) burningRfBuffer / burningItemTotalRf);
        }
    }

    public void tick(List<ConfigurableItemStack> itemInputs, List<ConfigurableFluidStack> fluidInputs, boolean canConsumeNewFuel) {
        // Turn buffer into heat
        long maxRfInsertion = Math.min(burningRfBuffer, maxRfProduction);

        // Use ceil here to be able to reach the max temperature exactly, even if it wastes a fraction of an EU.
        maxRfInsertion = Math.min(maxRfInsertion, (long) Math.ceil(rfPerDegree * (temperature.temperatureMax - temperature.getTemperature())));
        if (maxRfInsertion > 0) {
            burningRfBuffer -= maxRfInsertion;
            temperature.increaseTemperature((double) maxRfInsertion / rfPerDegree);
        } else if (burningRfBuffer == 0) {
            temperature.decreaseTemperature(1);
        }

        if (!canConsumeNewFuel) {
            return;
        }

        // Refill buffer with item fuel
        outer:
        while (burningRfBuffer < maxRfProduction) {
            // Find first item fuel
            for (ConfigurableItemStack stack : itemInputs) {
                var fuel = stack.getResource().toStack((int) stack.getAmount());
                if (ItemStackHelper.consumeFuel(stack, true)) {
                    int fuelTime = fuel.getBurnTime(null);
                    if (fuelTime > 0) {
                        long fuelTotalRf = (long) (fuelTime * EU_PER_BURN_TICK * burningItemRfMultiplier);
                        burningRfBuffer += fuelTotalRf;
                        burningItemTotalRf = fuelTotalRf;
                        ItemStackHelper.consumeFuel(stack, false);
                        continue outer;
                    }
                }
            }
            // Break if not found
            break;
        }
        // Refill buffer with fluid fuel
        outer:
        while (burningRfBuffer < 5 * 20 * maxRfProduction) {
            for (ConfigurableFluidStack stack : fluidInputs) {
                if (!stack.isEmpty()) {
                    long rfPerMb = FluidFuel.getRf(stack.getResource().getFluid());
                    if (rfPerMb != 0) {
                        long mbConsumedMax = (5 * 20 * maxRfProduction - burningRfBuffer) / rfPerMb;
                        long mbConsumed = Math.min(mbConsumedMax, stack.getAmount());
                        if (mbConsumed > 0) {
                            stack.decrement(mbConsumed);
                            burningRfBuffer += mbConsumed * rfPerMb;
                            burningItemTotalRf = 0;
                            continue outer;
                        }
                    }
                }
            }
            break;
        }
    }

    @Override
    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLong("burningEuBuffer", burningRfBuffer);
        if (burningItemTotalRf != 0) {
            tag.putLong("burningItemTotalEu", burningItemTotalRf);
        }
    }

    @Override
    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        burningRfBuffer = tag.getLong("burningEuBuffer");
        burningItemTotalRf = tag.getLong("burningItemTotalEu");
    }

    public List<Component> getTooltips(boolean acceptsFluid) {
        List<Component> returnList = new ArrayList<>();
        returnList.add(new MITooltips.Line(MIText.MaxRfProductionSteam).arg(
                this.maxRfProduction,
                MITooltips.EU_PER_TICK_PARSER)
                .arg(MIFluids.STEAM)
                .build());

        if (burningItemRfMultiplier == 1) {
            returnList.add(new MITooltips.Line(MIText.FuelEfficiencyItemNormal).build());
        } else {
            returnList.add(new MITooltips.Line(MIText.FuelEfficiencyItem).arg(burningItemRfMultiplier, MITooltips.MULTIPLIER_PARSER).arg(burningItemRfMultiplier, MITooltips.MULTIPLIER_PARSER).build());
        }
        if (acceptsFluid) {
            returnList.add(new MITooltips.Line(MIText.FuelEfficiencyFluidNormal).build());
        }

        return returnList;
    }
}
