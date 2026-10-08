package aztech.milite.machines.components;

public class NuclearEfficiencyHistoryComponent extends IntegerHistoryComponent<NuclearEfficiencyHistoryComponent.Type> {
    // lowerCamelCase names are used to have that as the serialized form.
    public enum Type {
        euProduction,
        euFuelConsumption
    }

    public NuclearEfficiencyHistoryComponent() {
        super(Type.class, 300);
    }

    public void registerEuFuelConsumption(double eu) {
        addValue(Type.euFuelConsumption, (int) eu);
    }

    public void registerEuProduction(double eu) {
        addValue(Type.euProduction, (int) eu);
    }
}
