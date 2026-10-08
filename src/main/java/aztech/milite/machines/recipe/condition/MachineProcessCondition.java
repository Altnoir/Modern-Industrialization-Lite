package aztech.milite.machines.recipe.condition;

import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.recipe.MachineRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public interface MachineProcessCondition {
    Codec<MachineProcessCondition> CODEC = ResourceLocation.CODEC
            .<MapCodec<? extends MachineProcessCondition>>flatXmap(
                    resLoc -> Optional.ofNullable(MachineProcessConditions.getCodec(resLoc))
                            .map(DataResult::success)
                            .orElseGet(() -> DataResult.error(() -> "Unknown machine process condition " + resLoc)),
                    codec -> Optional.ofNullable(MachineProcessConditions.getId(codec))
                            .map(DataResult::success)
                            .orElseGet(() -> DataResult.error(() -> "Unknown machine process condition codec " + codec)))
            .dispatch(MachineProcessCondition::codec, c -> c);
    @SuppressWarnings({ "unchecked", "rawtypes" })
    StreamCodec<RegistryFriendlyByteBuf, MachineProcessCondition> STREAM_CODEC = ResourceLocation.STREAM_CODEC
            .<RegistryFriendlyByteBuf>mapStream(b -> b)
            .map(
                    resLoc -> Optional.ofNullable(MachineProcessConditions.getStreamCodec(resLoc))
                            .orElseThrow(() -> new IllegalArgumentException("Unknown machine process condition " + resLoc)),
                    streamCodec -> Optional.ofNullable(MachineProcessConditions.getId(streamCodec))
                            .orElseThrow(() -> new IllegalArgumentException("Unknown machine process condition codec " + streamCodec)))
            .dispatch(r -> (StreamCodec) r.streamCodec(), c -> c);

    boolean canProcessRecipe(Context context, MachineRecipe recipe);

    void appendDescription(List<Component> list);

    default ItemStack icon() {
        return ItemStack.EMPTY;
    }

    MapCodec<? extends MachineProcessCondition> codec();

    StreamCodec<? super RegistryFriendlyByteBuf, ? extends MachineProcessCondition> streamCodec();

    interface Context {
        MachineBlockEntity getBlockEntity();

        default ServerLevel getLevel() {
            return (ServerLevel) getBlockEntity().getLevel();
        }
    }
}
