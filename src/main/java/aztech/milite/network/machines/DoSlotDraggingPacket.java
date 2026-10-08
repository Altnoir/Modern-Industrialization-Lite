package aztech.milite.network.machines;

import aztech.milite.compat.viewer.ReiDraggable;
import aztech.milite.network.BasePacket;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.milite.util.Simulation;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;

public record DoSlotDraggingPacket(int containerId, int slotId, boolean isItem, @Nullable ItemVariant itemVariant,
        @Nullable FluidVariant fluidVariant) implements BasePacket {
    public DoSlotDraggingPacket(int containerId, int slotId, ItemVariant itemVariant) {
        this(containerId, slotId, true, itemVariant, null);
    }

    public DoSlotDraggingPacket(int containerId, int slotId, FluidVariant fluidVariant) {
        this(containerId, slotId, false, null, fluidVariant);
    }

    private DoSlotDraggingPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readInt(), buf.readVarInt(), buf.readBoolean(), buf);
    }

    private DoSlotDraggingPacket(int containerId, int slotId, boolean isItem, RegistryFriendlyByteBuf buf) {
        this(containerId, slotId, isItem, isItem ? ItemVariant.fromPacket(buf) : null, isItem ? null : FluidVariant.fromPacket(buf));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, DoSlotDraggingPacket> STREAM_CODEC = StreamCodec.ofMember(
            DoSlotDraggingPacket::write, DoSlotDraggingPacket::new);

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(containerId);
        buf.writeVarInt(slotId);
        buf.writeBoolean(isItem);
        if (isItem) {
            itemVariant.toPacket(buf);
        } else {
            fluidVariant.toPacket(buf);
        }
    }

    @Override
    public void handle(Context ctx) {
        ctx.assertOnServer();

        AbstractContainerMenu sh = ctx.getPlayer().containerMenu;
        if (sh.containerId == containerId) {
            Slot slot = sh.getSlot(slotId);
            ReiDraggable dw = (ReiDraggable) slot;
            if (isItem) {
                dw.dragItem(itemVariant, Simulation.ACT);
            } else {
                dw.dragFluid(fluidVariant, Simulation.ACT);
            }
        }
    }
}
