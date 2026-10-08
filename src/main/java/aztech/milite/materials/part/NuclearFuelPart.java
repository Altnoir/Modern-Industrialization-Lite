package aztech.milite.materials.part;

import aztech.milite.items.SortOrder;
import aztech.milite.materials.property.MaterialProperty;
import aztech.milite.MIItem;
import aztech.milite.nuclear.IsotopeFuelParams;
import aztech.milite.nuclear.NeutronBehaviour;
import aztech.milite.nuclear.NuclearConstant;
import aztech.milite.nuclear.NuclearFuel;
import aztech.milite.nuclear.NuclearFuel.NuclearFuelParams;
import aztech.milite.nuclear.NuclearOrder;

import java.util.List;

public class NuclearFuelPart implements PartKeyProvider {
    public enum Type {
        SIMPLE(1, "fuel_rod"),
        DOUBLE(2, "fuel_rod_double"),
        QUAD(4, "fuel_rod_quad"),
        DEPLETED(0, "fuel_rod_depleted");

        public final int size;
        public final String key;

        Type(int size, String key) {
            this.size = size;
            this.key = key;
        }
    }

    public final Type type;
    public final PartKey key;

    public NuclearFuelPart(Type type) {
        this.key = new PartKey(type.key);
        this.type = type;
    }

    @Override
    public PartKey key() {
        return key;
    }

    private PartTemplate of() {
        String englishNameFormatter = switch (type) {
            case SIMPLE -> "Fuel Rod";
            case DOUBLE -> "Double Fuel Rod";
            case QUAD -> "Quad Fuel Rod";
            case DEPLETED -> "Depleted %s Fuel Rod";
        };

        var out = new PartTemplate(englishNameFormatter, key)
                .withRegister((partContext, part, itemPath, itemId, itemTag, englishName) -> {
                    var sortOrder = SortOrder.NUCLEAR.create(NuclearOrder.FUEL_ROD).and(partContext.getMaterialName()).and(type);
                    if (Type.DEPLETED == type) {
                        MIItem.item(englishName, itemPath, sortOrder);
                    } else {
                        IsotopeFuelParams params = partContext.get(MaterialProperty.ISOTOPE);
                        if (params == null) {
                            throw new IllegalArgumentException("Material %s must be a fuel isotope".formatted(partContext.getMaterialName()));
                        }

                        NuclearFuelParams fuelParams = new NuclearFuelParams(NuclearConstant.DESINTEGRATION_BY_ROD * type.size, params.maxTemp,
                                params.tempLimitLow,
                                params.tempLimitHigh, params.neutronsMultiplication, params.directEnergyFactor, type.size);

                        NeutronBehaviour neutronBehaviour = NeutronBehaviour.of(NuclearConstant.ScatteringType.HEAVY, params, type.size);

                        NuclearFuel.of(englishName, itemPath, fuelParams,
                                neutronBehaviour, partContext.getMaterialName() + "_fuel_rod_depleted", sortOrder);
                    }
                });
        if (type == Type.DEPLETED) {
            out = out.withTexture(new TextureGenParams.DepletedNuclear());
        }
        return out;
    }

    public static List<PartTemplate> ofAll() {
        return List.of(
                MIParts.FUEL_ROD.of(),
                MIParts.FUEL_ROD_DOUBLE.of(),
                MIParts.FUEL_ROD_QUAD.of(),
                MIParts.FUEL_ROD_DEPLETED.of());
    }
}
