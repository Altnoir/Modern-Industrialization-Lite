package aztech.milite.api.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;

public record FluidFuel(int rfPerMb) {
    public static final Codec<FluidFuel> CODEC = RecordCodecBuilder.create(
            instance -> instance
                    .group(
                            ExtraCodecs.POSITIVE_INT.fieldOf("eu_per_mb").forGetter(FluidFuel::rfPerMb))
                    .apply(instance, FluidFuel::new));

    public static int getRf(Fluid fluid) {
        var data = fluid.builtInRegistryHolder().getData(MIDataMaps.FLUID_FUELS);
        return data == null ? 0 : data.rfPerMb();
    }
}
