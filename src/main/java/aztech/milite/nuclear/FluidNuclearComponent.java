package aztech.milite.nuclear;

import aztech.milite.compat.kubejs.KubeJSProxy;
import aztech.milite.MIFluids;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

public record FluidNuclearComponent(
        FluidVariant variant,
        double heatConduction,
        NeutronBehaviour neutronBehaviour,
        FluidVariant neutronProduct,
        long neutronProductAmount,
        double neutronProductProbability) implements NuclearComponent<FluidVariant> {
    public FluidNuclearComponent(
            Fluid fluid,
            double heatConduction,
            double density,
            NuclearConstant.ScatteringType type,
            IsotopeParams params,
            FluidVariant neutronProduct,
            long neutronProductAmount,
            double neutronProductProbability) {
        this(
                FluidVariant.of(fluid),
                heatConduction * density,
                NeutronBehaviour.of(type, params, density),
                neutronProduct,
                neutronProductAmount,
                neutronProductProbability);
    }

    @Override
    public FluidVariant getVariant() {
        return variant;
    }

    @Override
    public double getHeatConduction() {
        return heatConduction;
    }

    @Override
    public NeutronBehaviour getNeutronBehaviour() {
        return neutronBehaviour;
    }

    @Override
    public FluidVariant getNeutronProduct() {
        return neutronProduct;
    }

    @Override
    public long getNeutronProductAmount() {
        return neutronProductAmount;
    }

    @Override
    public double getNeutronProductProbability() {
        return neutronProductProbability;
    }

    private static final Map<Fluid, FluidNuclearComponent> registry = new IdentityHashMap<>();

    public static void register(FluidNuclearComponent component) {
        Fluid fluid = component.getVariant().getFluid();
        if (registry.containsKey(fluid)) {
            throw new IllegalArgumentException("Already registered fluid-neutron interaction for " + fluid);
        }

        registry.put(fluid, component);
    }

    public static void remove(Fluid fluid) {
        registry.remove(fluid);
    }

    @Nullable
    public static FluidNuclearComponent get(Fluid fluid) {
        return registry.get(fluid);
    }

    public static void init() {
        register(new FluidNuclearComponent(Fluids.WATER,
                NuclearConstant.BASE_HEAT_CONDUCTION * 5,
                1,
                NuclearConstant.ScatteringType.ULTRA_LIGHT,
                NuclearConstant.HYDROGEN,
                MIFluids.DEUTERIUM.variant(),
                1,
                1));
        register(new FluidNuclearComponent(MIFluids.HEAVY_WATER.asFluid(),
                NuclearConstant.BASE_HEAT_CONDUCTION * 6,
                1,
                NuclearConstant.ScatteringType.LIGHT,
                NuclearConstant.DEUTERIUM,
                MIFluids.TRITIUM.variant(),
                1,
                1));
        register(new FluidNuclearComponent(MIFluids.HIGH_PRESSURE_WATER.asFluid(),
                NuclearConstant.BASE_HEAT_CONDUCTION * 5,
                4,
                NuclearConstant.ScatteringType.ULTRA_LIGHT,
                NuclearConstant.HYDROGEN,
                MIFluids.DEUTERIUM.variant(),
                8,
                0.125));
        register(new FluidNuclearComponent(MIFluids.HIGH_PRESSURE_HEAVY_WATER.asFluid(),
                NuclearConstant.BASE_HEAT_CONDUCTION * 6,
                4,
                NuclearConstant.ScatteringType.LIGHT,
                NuclearConstant.DEUTERIUM,
                MIFluids.TRITIUM.variant(),
                8,
                0.125));

        KubeJSProxy.instance.fireRegisterFluidNeutronInteractionsEvent();
    }
}
