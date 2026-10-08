package aztech.milite.api.machine.holder;

import aztech.milite.api.machine.component.FluidAccess;
import org.jspecify.annotations.Nullable;

public interface FluidStorageComponentHolder {
    @Nullable
    FluidAccess getFluidStorageComponent();
}
