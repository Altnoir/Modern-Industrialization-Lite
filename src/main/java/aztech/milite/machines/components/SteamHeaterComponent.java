package aztech.milite.machines.components;

import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.MIFluidStorage;
import aztech.milite.MIFluids;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.transaction.Transaction;
import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import it.unimi.dsi.fastutil.objects.Reference2LongOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class SteamHeaterComponent extends TemperatureComponent {
    private static final int STEAM_TO_WATER = 16;

    /**
     * mb/t of steam produced at max heat, assuming enough water
     */
    public final long maxRfProduction;
    /**
     * How many rf in one degree of heat.
     */
    public final long rfPerDegree;

    public final boolean acceptHighPressure;
    public final boolean acceptLowPressure;

    public final boolean requiresContinuousOperation;
    public static final double INPUT_ENERGY_RATIO_FOR_STARTUP = 0.8; // only if requires continuous operation

    /**
     * Amount of steam for which we already consumed the water.
     */
    private final Reference2LongMap<Fluid> steamBuffer = new Reference2LongOpenHashMap<>();

    public SteamHeaterComponent(double temperatureMax, long maxRfProduction, long rfPerDegree) {
        this(maxRfProduction, maxRfProduction, rfPerDegree, true, false, false);
    }

    public SteamHeaterComponent(double temperatureMax, long maxRfProduction, long rfPerDegree, boolean acceptLowPressure,
            boolean acceptHighPressure, boolean requiresContinuousOperation) {
        super(temperatureMax);
        this.maxRfProduction = maxRfProduction;
        this.rfPerDegree = rfPerDegree;
        this.acceptLowPressure = acceptLowPressure;
        this.acceptHighPressure = acceptHighPressure;
        this.requiresContinuousOperation = requiresContinuousOperation;
    }

    // return rf produced
    public double tick(List<ConfigurableFluidStack> fluidInputs, List<ConfigurableFluidStack> fluidOutputs) {
        double rfProducedLowPressure = 0;
        if (acceptLowPressure) {
            rfProducedLowPressure = tryMakeSteam(fluidInputs, fluidOutputs, Fluids.WATER, MIFluids.STEAM.asFluid(), 1);
            if (rfProducedLowPressure == 0) {
                rfProducedLowPressure = tryMakeSteam(fluidInputs, fluidOutputs, MIFluids.HEAVY_WATER.asFluid(), MIFluids.HEAVY_WATER_STEAM.asFluid(),
                        1);
            }
        }

        double rfProducedHighPressure = 0;
        if (acceptHighPressure) {
            rfProducedHighPressure = tryMakeSteam(fluidInputs, fluidOutputs, MIFluids.HIGH_PRESSURE_WATER.asFluid(),
                    MIFluids.HIGH_PRESSURE_STEAM.asFluid(), 8);
            if (rfProducedHighPressure == 0) {
                rfProducedHighPressure = tryMakeSteam(fluidInputs, fluidOutputs, MIFluids.HIGH_PRESSURE_HEAVY_WATER.asFluid(),
                        MIFluids.HIGH_PRESSURE_HEAVY_WATER_STEAM.asFluid(), 8);
            }
        }

        double totalRfProduced = rfProducedLowPressure + rfProducedHighPressure;

        if (this.requiresContinuousOperation) {
            this.decreaseTemperature(INPUT_ENERGY_RATIO_FOR_STARTUP * (this.maxRfProduction - totalRfProduced) / this.rfPerDegree);
        }

        return totalRfProduced;
    }

    public void tickDisabled() {
        this.decreaseTemperature(INPUT_ENERGY_RATIO_FOR_STARTUP * this.maxRfProduction / this.rfPerDegree);
    }

    private double tryMakeSteam(List<ConfigurableFluidStack> input, List<ConfigurableFluidStack> output, Fluid water, Fluid steam, int rfPerSteamMb) {
        return tryMakeSteam(new MIFluidStorage(input), new MIFluidStorage(output), water, steam, rfPerSteamMb);
    }

    // Return true if any steam was made.
    private double tryMakeSteam(MIFluidStorage input, MIFluidStorage output, Fluid water, Fluid steam, int rfPerSteamMb) {
        FluidVariant waterKey = FluidVariant.of(water);
        FluidVariant steamKey = FluidVariant.of(steam);

        if (getTemperature() > 100d) {
            long steamProduction = (long) ((getTemperature() - 100d) / (temperatureMax - 100d) * maxRfProduction / rfPerSteamMb);

            try (Transaction tx = Transaction.openRoot()) {
                long inserted;
                try (Transaction simul = Transaction.open(tx)) { // insertion Simulation
                    inserted = output.insertAllSlot(steamKey, steamProduction, simul);
                }
                if (inserted > 0) {
                    // Round water consumption up
                    long waterToUse = (inserted - steamBuffer.getLong(steam) + STEAM_TO_WATER - 1) / STEAM_TO_WATER;
                    // Extract water
                    long extracted = input.extractAllSlot(waterKey, waterToUse, tx);
                    // Add to steam buffer
                    steamBuffer.mergeLong(steam, extracted * STEAM_TO_WATER, Long::sum);

                    // Produce steam
                    long producedSteam = output.insertAllSlot(steamKey, Math.min(steamProduction, steamBuffer.getLong(steam)), tx);
                    steamBuffer.mergeLong(steam, -producedSteam, Long::sum);

                    double rfProduced = producedSteam * rfPerSteamMb;
                    decreaseTemperature(rfProduced / rfPerDegree);
                    tx.commit();
                    return rfProduced;
                }
            }
        }
        return 0;
    }

    @Override
    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        super.writeNbt(tag, registries);

        var buffer = new CompoundTag();
        for (var entry : steamBuffer.reference2LongEntrySet()) {
            if (entry.getLongValue() != 0) {
                buffer.putLong(entry.getKey().toString(), entry.getLongValue());
            }
        }
        tag.put("steamBuffer", buffer);
    }

    @Override
    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        super.readNbt(tag, registries, isUpgradingMachine);

        var steamBuffer = tag.getCompound("steamBuffer");
        for (var key : steamBuffer.getAllKeys()) {
            var fluid = BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(key));
            if (fluid != Fluids.EMPTY) {
                this.steamBuffer.put(fluid, steamBuffer.getLong(key));
            }
        }
    }
}
