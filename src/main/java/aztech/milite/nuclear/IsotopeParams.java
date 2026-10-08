package aztech.milite.nuclear;

public class IsotopeParams {
    public final double thermalAbsorption;
    public final double fastAbsorption;
    public final double fastScattering;
    public final double thermalScattering;

    public IsotopeParams(double thermalAbsorbProba, double fastAbsorptionProba, double thermalScatteringProba, double fastScatteringProba) {
        this.thermalAbsorption = NeutronBehaviour.crossSectionFromProba(thermalAbsorbProba);
        this.fastAbsorption = NeutronBehaviour.crossSectionFromProba(fastAbsorptionProba);
        this.thermalScattering = NeutronBehaviour.crossSectionFromProba(thermalScatteringProba);
        this.fastScattering = NeutronBehaviour.crossSectionFromProba(fastScatteringProba);
    }
}
