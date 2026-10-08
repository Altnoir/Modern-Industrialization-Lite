package aztech.milite.machines.blockentities;

import aztech.milite.api.energy.CableTier;
import aztech.milite.api.energy.CableTierHolder;
import aztech.milite.machines.BEP;
import net.minecraft.util.Mth;

public class StorageMachineBlockEntity extends AbstractStorageMachineBlockEntity implements CableTierHolder {
    public StorageMachineBlockEntity(BEP bep, CableTier tier, String name, long eu_capacity) {
        super(bep, tier, tier, name, eu_capacity);
    }

    @Override
    protected boolean hasComparatorOutput() {
        return true;
    }

    @Override
    protected int getComparatorOutput() {
        double fillPercentage = (double) energy.getRf() / energy.getCapacity();
        return Mth.floor(fillPercentage * 14) + (energy.getRf() > 0 ? 1 : 0);
    }

    @Override
    public CableTier getCableTier() {
        return to;
    }
}
