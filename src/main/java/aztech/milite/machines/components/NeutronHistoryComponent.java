package aztech.milite.machines.components;

import static aztech.milite.nuclear.NeutronType.FAST;
import static aztech.milite.nuclear.NeutronType.THERMAL;

import aztech.milite.nuclear.NeutronType;

public class NeutronHistoryComponent extends IntegerHistoryComponent<NeutronHistoryComponent.Type> {
    // lowerCamelCase names are used to have that as the serialized form.
    public enum Type {
        fastNeutronReceived,
        fastNeutronFlux,
        thermalNeutronReceived,
        thermalNeutronFlux,
        neutronGeneration,
        euGeneration
    }

    public NeutronHistoryComponent() {
        super(Type.class, 100);
    }

    public double getAverageReceived(NeutronType type) {
        if (type == FAST) {
            return getAverage(Type.fastNeutronReceived);
        } else if (type == THERMAL) {
            return getAverage(Type.thermalNeutronReceived);
        } else if (type == NeutronType.BOTH) {
            return getAverageReceived(FAST) + getAverageReceived(THERMAL);
        } else {
            return 0;
        }
    }

    public double getAverageFlux(NeutronType type) {
        if (type == FAST) {
            return getAverage(Type.fastNeutronFlux);
        } else if (type == THERMAL) {
            return getAverage(Type.thermalNeutronFlux);
        } else if (type == NeutronType.BOTH) {
            return getAverageFlux(FAST) + getAverageFlux(THERMAL);
        } else {
            return 0;
        }
    }

    public double getAverageGeneration() {
        return getAverage(Type.neutronGeneration);
    }

    public double getAverageEuGeneration() {
        return getAverage(Type.euGeneration);
    }
}
