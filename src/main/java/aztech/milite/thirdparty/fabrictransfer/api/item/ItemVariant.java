package aztech.milite.thirdparty.fabrictransfer.api.item;

import aztech.milite.thirdparty.fabrictransfer.api.storage.TransferVariant;
import aztech.milite.thirdparty.fabrictransfer.impl.item.ItemVariantImpl;
import aztech.milite.thirdparty.fabrictransfer.impl.TransferApiImpl;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;
import java.util.Optional;

/**
 * An immutable count-less ItemStack, i.e. an immutable association of an item and an optional NBT compound tag.
 *
 * <p>
 * Do not implement, use the static {@code of(...)} functions instead.
 */
@ApiStatus.NonExtendable
public interface ItemVariant extends TransferVariant<Item> {
    Codec<ItemVariant> CODEC = ExtraCodecs.optionalEmptyMap(
            ItemStack.SINGLE_ITEM_CODEC.xmap(ItemVariant::of, ItemVariant::toStack))
            .xmap(o -> o.orElse(ItemVariant.blank()), fv -> fv.isBlank() ? Optional.empty() : Optional.of(fv));
    StreamCodec<RegistryFriendlyByteBuf, ItemVariant> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC.map(ItemVariant::of, ItemVariant::toStack);

    ItemVariant BLANK = of(Items.AIR);

    /**
     * Retrieve a blank ItemVariant.
     */
    static ItemVariant blank() {
        return BLANK;
    }

    /**
     * Retrieve an ItemVariant with the item and tag of a stack.
     */
    static ItemVariant of(ItemStack stack) {
        return ItemVariantImpl.of(stack);
    }

    /**
     * Retrieve an ItemVariant with an item and without a tag.
     */
    static ItemVariant of(ItemLike item) {
        return ItemVariantImpl.of(item.asItem());
    }

    /**
     * Return true if the item and tag of this variant match those of the passed stack, and false otherwise.
     */
    boolean matches(ItemStack stack);

    /**
     * Tests an {@link ItemStack} predicate with the inner stack.
     *
     * @param predicate Predicate to perform the test with
     * @return {@code true} if the test passed
     */
    boolean test(Predicate<ItemStack> predicate);

    /**
     * Return the item of this variant.
     */
    default Item getItem() {
        return getObject();
    }

    /**
     * Create a new item stack with count 1 from this variant.
     */
    default ItemStack toStack() {
        return toStack(1);
    }

    /**
     * Create a new item stack from this variant.
     *
     * @param count The count of the returned stack. It may lead to counts higher than maximum stack size.
     */
    ItemStack toStack(int count);

    int getMaxStackSize();

    @Override
    default Tag toNbt(HolderLookup.Provider registries) {
        return CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
    }

    /**
     * Deserialize a variant from an NBT compound tag, assuming it was serialized using
     * {@link #toNbt}. If an error occurs during deserialization, it will be logged
     * with the DEBUG level, and a blank variant will be returned.
     */
    static ItemVariant fromNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        return CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), nbt).resultOrPartial(TransferApiImpl.LOGGER::error)
                .orElse(blank());
    }

    @Override
    default void toPacket(RegistryFriendlyByteBuf buf) {
        STREAM_CODEC.encode(buf, this);
    }

    /**
     * Write a variant from a packet byte buffer, assuming it was serialized using
     * {@link #toPacket}.
     */
    static ItemVariant fromPacket(RegistryFriendlyByteBuf buf) {
        return STREAM_CODEC.decode(buf);
    }
}
