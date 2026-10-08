package aztech.milite.blocks.storage;

import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.milite.thirdparty.fabrictransfer.api.storage.TransferVariant;
import aztech.milite.util.MIExtraCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.function.UnaryOperator;

public record ResourceStorage<T extends TransferVariant<?>>(T resource, long amount, boolean locked) {
    public ResourceStorage {
        if (resource.isBlank() && amount != 0) {
            throw new IllegalArgumentException("Expected 0 amount for blank resoruce, got " + amount);
        }
    }

    public static final ResourceStorage<FluidVariant> FLUID_EMPTY = new ResourceStorage<>(FluidVariant.blank(), 0, false);
    public static final ResourceStorage<ItemVariant> ITEM_EMPTY = new ResourceStorage<>(ItemVariant.blank(), 0, false);

    public ResourceStorage<T> withResource(T resource) {
        return new ResourceStorage<>(resource, amount, locked);
    }

    public ResourceStorage<T> withAmount(long amount) {
        return new ResourceStorage<>(resource, amount, locked);
    }

    public ResourceStorage<T> withLocked(boolean locked) {
        return new ResourceStorage<>(resource, amount, locked);
    }

    public static <T extends TransferVariant<?>> Codec<ResourceStorage<T>> codec(Codec<T> variantCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                variantCodec.fieldOf("resource").forGetter(ResourceStorage::resource),
                MIExtraCodecs.POSITIVE_LONG.optionalFieldOf("amount", 0L).forGetter(ResourceStorage::amount),
                Codec.BOOL.optionalFieldOf("locked", false).forGetter(ResourceStorage::locked)).apply(instance, ResourceStorage::new));
    }

    public static <T extends TransferVariant<?>> StreamCodec<RegistryFriendlyByteBuf, ResourceStorage<T>> streamCodec(
            StreamCodec<RegistryFriendlyByteBuf, T> variantCodec) {
        return StreamCodec.composite(
                variantCodec,
                ResourceStorage::resource,
                ByteBufCodecs.VAR_LONG,
                ResourceStorage::amount,
                ByteBufCodecs.BOOL,
                ResourceStorage::locked,
                (resource, amount, locked) -> {
                    if (resource.isBlank()) {
                        amount = 0L;
                    }
                    return new ResourceStorage<>(resource, amount, locked);
                });
    }

    public static <T extends TransferVariant<?>> UnaryOperator<DataComponentType.Builder<ResourceStorage<T>>> component(Codec<T> variantCodec,
            StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return builder -> builder.persistent(codec(variantCodec)).networkSynchronized(streamCodec(streamCodec));
    }
}
