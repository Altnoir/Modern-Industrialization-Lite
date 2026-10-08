package aztech.milite.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import java.util.function.Function;
import java.util.List;
import java.util.Optional;

public class MIExtraCodecs {
    public static final Codec<Float> FLOAT_01 = Codec.floatRange(0, 1);
    public static final Codec<Long> NON_NEGATIVE_LONG = longRange(0, Long.MAX_VALUE);
    public static final Codec<Long> POSITIVE_LONG = longRange(1, Long.MAX_VALUE);

    private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkRange(final N minInclusive, final N maxInclusive) {
        return value -> {
            if (value.compareTo(minInclusive) >= 0 && value.compareTo(maxInclusive) <= 0) {
                return DataResult.success(value);
            }
            return DataResult.error(() -> "Value " + value + " outside of range [" + minInclusive + ":" + maxInclusive + "]", value);
        };
    }

    static Codec<Long> longRange(final long minInclusive, final long maxInclusive) {
        final Function<Long, DataResult<Long>> checker = checkRange(minInclusive, maxInclusive);
        return Codec.LONG.flatXmap(checker, checker);
    }

    public static <T> Codec<List<T>> maybeList(Codec<T> elementCodec) {
        var listCodec = NeoForgeExtraCodecs.listWithOptionalElements(ConditionalOps.createConditionalCodec(elementCodec));
        return Codec.either(listCodec, elementCodec)
                .xmap(either -> either.map(Function.identity(), List::of), Either::left);
    }

    /**
     * A codec that can accept a single element, or a list of potentially conditional elements.
     */
    public static <T> MapCodec<List<T>> maybeList(Codec<T> elementCodec, String field) {
        return maybeList(elementCodec).optionalFieldOf(field, List.of());
    }

    public static <T> MapCodec<T> optionalFieldAlwaysWrite(Codec<T> baseCodec, String field, T defaultValue) {
        return baseCodec.optionalFieldOf(field)
                .xmap(read -> read.orElse(defaultValue), Optional::of);
    }
}
