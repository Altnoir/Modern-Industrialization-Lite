package aztech.milite.materials.part;

import static aztech.milite.materials.part.MIParts.FUEL_ROD;

import aztech.milite.items.SortOrder;
import aztech.milite.nuclear.IsotopeParams;
import aztech.milite.nuclear.NeutronBehaviour;
import aztech.milite.nuclear.NuclearAbsorbable;
import aztech.milite.nuclear.NuclearConstant;
import aztech.milite.nuclear.NuclearOrder;

public class ControlRodPart implements PartKeyProvider {
    @Override
    public PartKey key() {
        return FUEL_ROD.key;
    }

    public PartTemplate of(int maxTemperature, double heatConduction, double thermalAbsorbProba, double fastAbsorbProba,
            double thermalScatteringProba, double fastScatteringProba, NuclearConstant.ScatteringType scatteringType, double size) {
        return new PartTemplate("Control Rod", key())
                .withRegister((partContext, part, itemPath1, itemId, itemTag, itemEnglishName) -> NuclearAbsorbable
                        .of(partContext.getMaterialEnglishName() + " Control Rod", itemPath1, maxTemperature,
                                heatConduction * NuclearConstant.BASE_HEAT_CONDUCTION,
                                NeutronBehaviour.of(scatteringType,
                                        new IsotopeParams(thermalAbsorbProba, fastAbsorbProba, thermalScatteringProba,
                                                fastScatteringProba),
                                        size),
                                NuclearConstant.DESINTEGRATION_BY_ROD,
                                SortOrder.NUCLEAR.create(NuclearOrder.CONTROL_ROD)))
                .withCustomPath("%s_control_rod");
    }

    public PartTemplate of(int maxTemperature, double heatConduction, NuclearConstant.ScatteringType scatteringType,
            IsotopeParams params, double size) {
        return new PartTemplate("Control Rod", key())
                .withRegister((partContext, part, itemPath1, itemId, itemTag, itemEnglishName) -> NuclearAbsorbable
                        .of(partContext.getMaterialEnglishName() + " Control Rod", itemPath1, maxTemperature,
                                heatConduction * NuclearConstant.BASE_HEAT_CONDUCTION,
                                NeutronBehaviour.of(scatteringType, params, size),
                                NuclearConstant.DESINTEGRATION_BY_ROD,
                                SortOrder.NUCLEAR.create(NuclearOrder.CONTROL_ROD)))
                .withCustomPath("%s_control_rod");
    }
}
