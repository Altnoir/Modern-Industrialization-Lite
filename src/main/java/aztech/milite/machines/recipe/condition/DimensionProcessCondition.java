package aztech.milite.machines.recipe.condition;

import aztech.milite.machines.recipe.MachineRecipe;
import aztech.milite.MIItem;
import aztech.milite.MIText;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public record DimensionProcessCondition(ResourceKey<Level> dimension) implements MachineProcessCondition {
    static final MapCodec<DimensionProcessCondition> CODEC = ResourceKey.codec(Registries.DIMENSION)
            .fieldOf("dimension")
            .xmap(DimensionProcessCondition::new, DimensionProcessCondition::dimension);

    static final StreamCodec<RegistryFriendlyByteBuf, DimensionProcessCondition> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION),
            DimensionProcessCondition::dimension,
            DimensionProcessCondition::new);

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        return context.getLevel().dimension() == dimension;
    }

    @Override
    public void appendDescription(List<Component> list) {
        var loc = dimension.location();
        var dimComponent = Component.translatable("dimension.%s.%s".formatted(loc.getNamespace(), loc.getPath()));
        list.add(MIText.RequiresDimension.text(dimComponent));
    }

    @Override
    public ItemStack icon() {
        return MIItem.SINGULARITY.asItem().getDefaultInstance();
    }

    @Override
    public MapCodec<? extends MachineProcessCondition> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ? extends MachineProcessCondition> streamCodec() {
        return STREAM_CODEC;
    }
}
