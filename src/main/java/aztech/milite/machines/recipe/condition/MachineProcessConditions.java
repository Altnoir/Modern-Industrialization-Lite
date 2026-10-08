package aztech.milite.machines.recipe.condition;

import aztech.milite.MI;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;

public final class MachineProcessConditions {
    private static final BiMap<ResourceLocation, MapCodec<? extends MachineProcessCondition>> MAP = HashBiMap.create();
    private static final BiMap<ResourceLocation, StreamCodec<? super RegistryFriendlyByteBuf, ? extends MachineProcessCondition>> STREAM_MAP = HashBiMap
            .create();

    public static <T extends MachineProcessCondition> void register(
            ResourceLocation id,
            MapCodec<T> codec,
            StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        if (MAP.get(id) != null || MAP.inverse().get(codec) != null || STREAM_MAP.inverse().get(streamCodec) != null) {
            throw new IllegalArgumentException("Duplicate registration for process condition " + id);
        }

        MAP.put(id, codec);
        STREAM_MAP.put(id, streamCodec);
    }

    @Nullable
    public static MapCodec<? extends MachineProcessCondition> getCodec(ResourceLocation id) {
        return MAP.get(id);
    }

    @Nullable
    public static StreamCodec<? super RegistryFriendlyByteBuf, ? extends MachineProcessCondition> getStreamCodec(ResourceLocation id) {
        return STREAM_MAP.get(id);
    }

    public static ResourceLocation getId(MapCodec<? extends MachineProcessCondition> codec) {
        return MAP.inverse().get(codec);
    }

    public static ResourceLocation getId(StreamCodec<? super RegistryFriendlyByteBuf, ? extends MachineProcessCondition> streamCodec) {
        return STREAM_MAP.inverse().get(streamCodec);
    }

    static {
        register(MI.id("dimension"), DimensionProcessCondition.CODEC, DimensionProcessCondition.STREAM_CODEC);
        register(MI.id("adjacent_block"), AdjacentBlockProcessCondition.CODEC, AdjacentBlockProcessCondition.STREAM_CODEC);
        register(MI.id("biome"), BiomeProcessCondition.CODEC, BiomeProcessCondition.STREAM_CODEC);
        register(MI.id("custom"), CustomProcessCondition.CODEC, CustomProcessCondition.STREAM_CODEC);
    }
}
