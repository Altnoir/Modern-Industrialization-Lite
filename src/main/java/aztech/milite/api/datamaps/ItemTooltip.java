package aztech.milite.api.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import java.util.List;

public record ItemTooltip(List<Component> components) {
    public static final Codec<ItemTooltip> CODEC = RecordCodecBuilder.create(
            instance -> instance
                    .group(
                            ComponentSerialization.CODEC.listOf(1, Integer.MAX_VALUE).fieldOf("components")
                                    .forGetter(ItemTooltip::components))
                    .apply(instance, ItemTooltip::new));
}
