package aztech.milite.network.machines;

import aztech.milite.machines.gui.MachineMenuCommon;
import aztech.milite.network.BasePacket;
import aztech.milite.network.MIStreamCodecs;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record MachineComponentSyncPacket(int syncId, int componentIndex, byte[] data) implements BasePacket {
    public static final StreamCodec<ByteBuf, MachineComponentSyncPacket> STREAM_CODEC = StreamCodec.composite(
            MIStreamCodecs.BYTE,
            MachineComponentSyncPacket::syncId,
            ByteBufCodecs.VAR_INT,
            MachineComponentSyncPacket::componentIndex,
            ByteBufCodecs.BYTE_ARRAY,
            MachineComponentSyncPacket::data,
            MachineComponentSyncPacket::new);

    @Override
    public void handle(Context ctx) {
        ctx.assertOnClient();

        if (ctx.getPlayer().containerMenu.containerId == syncId) {
            var screenHandler = (MachineMenuCommon) ctx.getPlayer().containerMenu;
            var buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(data), ctx.getPlayer().registryAccess());
            try {
                screenHandler.readClientComponentSyncData(componentIndex, buf);
            } finally {
                buf.release();
            }
        }
    }
}
