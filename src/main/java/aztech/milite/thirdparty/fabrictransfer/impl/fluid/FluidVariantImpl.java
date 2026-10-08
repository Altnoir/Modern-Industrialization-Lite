package aztech.milite.thirdparty.fabrictransfer.impl.fluid;

import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Objects;

public class FluidVariantImpl implements FluidVariant {
    private static final Map<Fluid, FluidVariant> noTagCache = new ConcurrentHashMap<>();

    public static FluidVariant of(Fluid fluid) {
        Objects.requireNonNull(fluid, "Fluid may not be null.");

        return noTagCache.computeIfAbsent(fluid, f -> new FluidVariantImpl(new FluidStack(f, 1)));
    }

    public static FluidVariant of(FluidStack stack) {
        Objects.requireNonNull(stack);

        if (stack.isComponentsPatchEmpty() || stack.isEmpty()) {
            return of(stack.getFluid());
        } else {
            return new FluidVariantImpl(stack);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger("fabric-transfer-api-v1/fluid");

    private final FluidStack stack;
    private final int hashCode;

    public FluidVariantImpl(FluidStack stack) {
        this.stack = stack.copyWithAmount(1); // defensive copy
        this.hashCode = FluidStack.hashFluidAndComponents(stack);
    }

    @Override
    public Fluid getObject() {
        return this.stack.getFluid();
    }

    @Override
    public DataComponentPatch getComponentsPatch() {
        return this.stack.getComponentsPatch();
    }

    @Override
    public boolean matches(FluidStack stack) {
        return FluidStack.isSameFluidSameComponents(this.stack, stack);
    }

    @Override
    public FluidStack toStack(int count) {
        return this.stack.copyWithAmount(count);
    }

    @Override
    public boolean isBlank() {
        return this.stack.isEmpty();
    }

    @Override
    public String toString() {
        return "FluidVariant{stack=" + stack + '}';
    }

    @Override
    public boolean equals(Object o) {
        // succeed fast with == check
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FluidVariantImpl fluidVariant = (FluidVariantImpl) o;
        // fail fast with hash code
        return hashCode == fluidVariant.hashCode && matches(fluidVariant.stack);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
