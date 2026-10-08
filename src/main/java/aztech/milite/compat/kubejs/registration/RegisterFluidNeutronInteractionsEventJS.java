package aztech.milite.compat.kubejs.registration;

import aztech.milite.nuclear.FluidNuclearComponent;
import aztech.milite.nuclear.IsotopeParams;
import aztech.milite.nuclear.NuclearConstant;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;

public class RegisterFluidNeutronInteractionsEventJS implements KubeEvent {
    public void register(
            Fluid fluid,
            double heatConduction,
            double density,
            String scatteringString,
            // Isotope params
            double thermalAbsorption,
            double fastAbsorption,
            double thermalScattering,
            double fastScattering,
            // Products
            Fluid productFluid,
            long productPerNeutron,
            double productProbability) {
        var scatteringType = switch (scatteringString) {
            case "ultra_light" -> NuclearConstant.ScatteringType.ULTRA_LIGHT;
            case "light" -> NuclearConstant.ScatteringType.LIGHT;
            case "medium" -> NuclearConstant.ScatteringType.MEDIUM;
            case "heavy" -> NuclearConstant.ScatteringType.HEAVY;
            default -> throw new IllegalArgumentException("Invalid ScatteringType: " + scatteringString);
        };

        FluidNuclearComponent.register(
                new FluidNuclearComponent(
                        fluid,
                        heatConduction,
                        density,
                        scatteringType,
                        new IsotopeParams(thermalAbsorption, fastAbsorption, thermalScattering, fastScattering),
                        FluidVariant.of(productFluid),
                        productPerNeutron,
                        productProbability));
    }

    public void remove(Fluid fluid) {
        if (FluidNuclearComponent.get(fluid) == null) {
            throw new IllegalArgumentException("Fluid " + BuiltInRegistries.FLUID.getKey(fluid) + " is not a nuclear component!");
        }

        FluidNuclearComponent.remove(fluid);
    }

    public void modify(
            Fluid fluid,
            double heatConduction,
            double density,
            String scatteringString,
            // Isotope params
            double thermalAbsorption,
            double fastAbsorption,
            double thermalScattering,
            double fastScattering,
            // Products
            Fluid productFluid,
            long productPerNeutron,
            double productProbability) {
        remove(fluid);
        register(fluid, heatConduction, density, scatteringString, thermalAbsorption, fastAbsorption, thermalScattering, fastScattering, productFluid,
                productPerNeutron, productProbability);
    }
}
