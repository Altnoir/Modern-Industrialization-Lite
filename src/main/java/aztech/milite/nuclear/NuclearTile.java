package aztech.milite.nuclear;

import aztech.milite.machines.components.NuclearEfficiencyHistoryComponent;

import java.util.Optional;

public interface NuclearTile extends NuclearTileData {
    void setTemperature(double temp);

    void putHeat(double fe);

    default Optional<NuclearFuel> getFuel() {
        if (getComponent() instanceof NuclearFuel fuel) {
            return Optional.of(fuel);
        }
        return Optional.empty();
    }

    void absorbNeutrons(int neutronNumber, NeutronType type);

    void addNeutronsToFlux(int neutronNumber, NeutronType type);

    int neutronGenerationTick(NuclearEfficiencyHistoryComponent efficiencyHistory);

    void nuclearTick(NuclearEfficiencyHistoryComponent efficiencyHistory);
}
