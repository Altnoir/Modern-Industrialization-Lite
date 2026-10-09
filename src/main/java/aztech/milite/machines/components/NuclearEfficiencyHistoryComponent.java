package aztech.milite.machines.components;

public class NuclearEfficiencyHistoryComponent extends IntegerHistoryComponent<NuclearEfficiencyHistoryComponent.Type> {
    // lowerCamelCase names are used to have that as the serialized form.
    public enum Type {
        feProduction,
        feFuelConsumption
    }

    public NuclearEfficiencyHistoryComponent() {
        super(Type.class, 300);
    }

    public void registerFeFuelConsumption(double fe) {
        addValue(Type.feFuelConsumption, (int) fe);
    }

    public void registerFeProduction(double fe) {
        addValue(Type.feProduction, (int) fe);
    }
}
