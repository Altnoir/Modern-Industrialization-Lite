package aztech.milite.api.machine.component;

import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import com.google.common.primitives.Ints;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;

public interface FluidAccess {
    @ApiStatus.Internal // Don't rely on these types existing forever!
    FluidVariant getVariant();

    long getAmount();

    long getCapacity();

    default FluidStack toStack() {
        return getVariant().toStack(Ints.saturatedCast(getAmount()));
    }
}
