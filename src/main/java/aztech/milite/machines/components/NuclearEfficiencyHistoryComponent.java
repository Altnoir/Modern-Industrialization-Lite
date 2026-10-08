package aztech.milite.machines.components;

public class NuclearEfficiencyHistoryComponent extends IntegerHistoryComponent<NuclearEfficiencyHistoryComponent.Type> {
    // lowerCamelCase names are used to have that as the serialized form.
    public enum Type {
        rfProduction,
        rfFuelConsumption
    }

    public NuclearEfficiencyHistoryComponent() {
        super(Type.class, 300);
    }

    public void registerRfFuelConsumption(double rf) {
        addValue(Type.rfFuelConsumption, (int) rf);
    }

    public void registerRfProduction(double rf) {
        addValue(Type.rfProduction, (int) rf);
    }
}
