package aztech.milite.definition;

import net.minecraft.world.level.material.Fluid;

public interface FluidLike {
    Fluid asFluid();

    static FluidLike of(Fluid fluid) {
        return () -> fluid;
    }
}
