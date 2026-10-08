package aztech.milite.api.machine.holder;

import aztech.milite.api.machine.component.EnergyAccess;

import java.util.List;

public interface EnergyListComponentHolder {
    List<? extends EnergyAccess> getEnergyComponents();
}
