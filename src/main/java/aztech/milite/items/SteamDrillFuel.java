package aztech.milite.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public record SteamDrillFuel(int burnTicks, int maxBurnTicks) {
    public static final SteamDrillFuel EMPTY = new SteamDrillFuel(0, 0);

    public static final Codec<SteamDrillFuel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("burn_ticks").forGetter(SteamDrillFuel::burnTicks),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_burn_ticks").forGetter(SteamDrillFuel::maxBurnTicks)).apply(instance, SteamDrillFuel::new));
}
