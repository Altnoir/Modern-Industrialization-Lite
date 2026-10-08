package aztech.milite.thirdparty.fabrictransfer.api.storage;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;

/**
 * An immutable association of an immutable object instance (for example {@code Item} or {@code Fluid}) and an optional NBT tag.
 *
 * <p>
 * This is exposed for convenience for code that needs to be generic across multiple transfer variants,
 * but note that a {@link Storage} is not necessarily bound to {@code TransferVariant}. Its generic parameter can be any immutable object.
 *
 * <p>
 * <b>Transfer variants must always be compared with {@code equals}, never by reference!</b>
 * {@code hashCode} is guaranteed to be correct and constant time independently of the size of the NBT.
 *
 * @param <O> The type of the immutable object instance, for example {@code Item} or {@code Fluid}.
 */
public interface TransferVariant<O> {
    /**
     * Return true if this variant is blank, and false otherwise.
     */
    boolean isBlank();

    /**
     * Return the immutable object instance of this variant.
     */
    O getObject();

    /**
     * Return {@code true} if the object of this variant matches the passed fluid.
     */
    default boolean isOf(O object) {
        return getObject() == object;
    }

    DataComponentPatch getComponentsPatch();

    /**
     * Save this variant into an NBT compound tag. Subinterfaces should have a matching static {@code fromNbt}.
     *
     * <p>
     * Note: This is safe to use for persisting data as objects are saved using their full Identifier.
     */
    Tag toNbt(HolderLookup.Provider registries);

    /**
     * Write this variant into a packet byte buffer. Subinterfaces should have a matching static {@code fromPacket}.
     *
     * <p>
     * Implementation note: Objects are saved using their raw registry integer id.
     */
    void toPacket(RegistryFriendlyByteBuf buf);
}
