package aztech.milite.api.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;

public record FluidFuel(int euPerMb) {
    public static final Codec<FluidFuel> CODEC = RecordCodecBuilder.create(
            instance -> instance
                    .group(
                            ExtraCodecs.POSITIVE_INT.fieldOf("eu_per_mb").forGetter(FluidFuel::euPerMb))
                    .apply(instance, FluidFuel::new));

    public static int getEu(Fluid fluid) {
        var data = fluid.builtInRegistryHolder().getData(MIDataMaps.FLUID_FUELS);
        return data == null ? 0 : data.euPerMb();
    }
}
