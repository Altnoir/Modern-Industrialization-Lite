package aztech.milite.machines.recipe.condition;

import aztech.milite.machines.recipe.MachineRecipe;
import aztech.milite.MIText;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Locale;

public record AdjacentBlockProcessCondition(Block block, RelativePosition relativePosition) implements MachineProcessCondition {
    static final MapCodec<AdjacentBlockProcessCondition> CODEC = RecordCodecBuilder.mapCodec(
            g -> g.group(
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(c -> c.block),
                    StringRepresentable.fromEnum(RelativePosition::values).fieldOf("position").forGetter(c -> c.relativePosition))
                    .apply(g, AdjacentBlockProcessCondition::new));
    static final StreamCodec<RegistryFriendlyByteBuf, AdjacentBlockProcessCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.BLOCK),
            AdjacentBlockProcessCondition::block,
            ByteBufCodecs.fromCodec(StringRepresentable.fromEnum(RelativePosition::values)),
            AdjacentBlockProcessCondition::relativePosition,
            AdjacentBlockProcessCondition::new);

    public AdjacentBlockProcessCondition(Block block, String relativePosition) {
        this(block, RelativePosition.valueOf(relativePosition.toUpperCase(Locale.ROOT)));
    }

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        return switch (relativePosition) {
            case BELOW -> {
                var checkPos = context.getBlockEntity().getBlockPos().below();
                yield context.getLevel().getBlockState(checkPos).is(block);
            }
            case BEHIND -> {
                var orientation = context.getBlockEntity().orientation;
                if (!orientation.params.hasFacing) {
                    yield false;
                }
                var direction = orientation.facingDirection;
                var checkPos = context.getBlockEntity().getBlockPos().relative(direction.getOpposite());
                yield context.getLevel().getBlockState(checkPos).is(block);
            }
        };
    }

    @Override
    public void appendDescription(List<Component> list) {
        var text = switch (relativePosition) {
            case BELOW -> MIText.RequiresBlockBelow;
            case BEHIND -> MIText.RequiresBlockBehind;
        };
        list.add(text.text(block.getName()));
    }

    @Override
    public ItemStack icon() {
        return block.asItem().getDefaultInstance();
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

enum RelativePosition implements StringRepresentable {
    BELOW,
    BEHIND;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    @Override
    public String getSerializedName() {
        return toString().toLowerCase(Locale.ROOT);
    }
}
