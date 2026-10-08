package aztech.milite.api.machine.component;

import java.util.List;

public interface InventoryAccess {
    List<? extends FluidAccess> getFluidInputs();

    List<? extends FluidAccess> getFluidOutputs();

    List<? extends ItemAccess> getItemInputs();

    List<? extends ItemAccess> getItemOutputs();
}
