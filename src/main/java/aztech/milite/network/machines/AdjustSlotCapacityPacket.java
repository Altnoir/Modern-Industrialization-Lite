package aztech.milite.network.machines;

import aztech.milite.inventory.ConfigurableItemStack;
import aztech.milite.network.BasePacket;
import aztech.milite.network.MIStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public record AdjustSlotCapacityPacket(int syncId, int slotId, boolean isIncrease, boolean isShiftDown) implements BasePacket {
    public static final StreamCodec<ByteBuf, AdjustSlotCapacityPacket> STREAM_CODEC = StreamCodec.composite(
            MIStreamCodecs.BYTE,
            AdjustSlotCapacityPacket::syncId,
            ByteBufCodecs.VAR_INT,
            AdjustSlotCapacityPacket::slotId,
            ByteBufCodecs.BOOL,
            AdjustSlotCapacityPacket::isIncrease,
            ByteBufCodecs.BOOL,
            AdjustSlotCapacityPacket::isShiftDown,
            AdjustSlotCapacityPacket::new);

    @Override
    public void handle(Context ctx) {
        ctx.assertOnServer();

        AbstractContainerMenu sh = ctx.getPlayer().containerMenu;
        if (sh.containerId == syncId) {
            Slot slot = sh.getSlot(slotId);
            if (slot instanceof ConfigurableItemStack.ConfigurableItemSlot confSlot) {
                confSlot.getConfStack().adjustCapacity(isIncrease, isShiftDown);
            }
        }
    }
}
