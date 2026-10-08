package aztech.milite.nuclear;

import aztech.milite.materials.Material;
import aztech.milite.materials.property.MaterialProperty;

public class IsotopeFuelParams extends IsotopeParams {
    public final int maxTemp;
    public final double neutronsMultiplication;
    public final double directEnergyFactor;
    public final int tempLimitLow;
    public final int tempLimitHigh;

    public IsotopeFuelParams(double thermalAbsorbProba, double thermalScatterings, int maxTemp, int tempLimitLow, int tempLimitHigh,
            double neutronsMultiplication, double directEnergyFactor) {
        super(thermalAbsorbProba, NeutronBehaviour.reduceCrossProba(thermalAbsorbProba, 0.1), thermalScatterings,
                NeutronBehaviour.reduceCrossProba(thermalScatterings, 0.5));

        this.maxTemp = maxTemp;
        this.neutronsMultiplication = neutronsMultiplication;
        this.directEnergyFactor = directEnergyFactor;
        this.tempLimitLow = tempLimitLow;
        this.tempLimitHigh = tempLimitHigh;
    }

    public static IsotopeFuelParams of(Material material) {
        var params = material.get(MaterialProperty.ISOTOPE);
        if (params == null) {
            throw new IllegalArgumentException("Material %s must be a fuel isotope".formatted(material.name));
        }
        return params;
    }

    public static IsotopeFuelParams mix(Material a, Material b, double factor) {
        return mix(of(a), of(b), factor);
    }

    public static IsotopeFuelParams mix(IsotopeFuelParams a, IsotopeFuelParams b, double factor) {
        factor = 1 - factor;

        double newThermalAbsorptionProba = NeutronBehaviour.probaFromCrossSection(mix(a.thermalAbsorption, b.thermalAbsorption, factor));
        double newScatteringProba = NeutronBehaviour.probaFromCrossSection(mix(a.thermalScattering, b.thermalScattering, factor));
        double newNeutronMultiplicationFactor = mix(a.neutronsMultiplication, b.neutronsMultiplication, factor);

        double totalEnergy = mix(a.neutronsMultiplication * (1 + a.directEnergyFactor), b.neutronsMultiplication * (1 + b.directEnergyFactor),
                factor);

        int newMaxTemp = (int) mix(a.maxTemp, b.maxTemp, factor);
        int newTempLimitLow = (int) mix(a.tempLimitLow, b.tempLimitLow, factor);
        int newTempLimitHigh = (int) mix(a.tempLimitHigh, b.tempLimitHigh, factor);

        double newDirectEnergyFactor = totalEnergy / (newNeutronMultiplicationFactor) - 1;

        return new IsotopeFuelParams(newThermalAbsorptionProba, newScatteringProba, newMaxTemp, newTempLimitLow, newTempLimitHigh,
                newNeutronMultiplicationFactor, newDirectEnergyFactor);
    }

    private static double mix(double a, double b, double r) {
        return r * a + (1 - r) * b;
    }
}
