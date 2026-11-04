package aztech.milite.network.machines;

import aztech.milite.machines.gui.MachineMenuServer;
import aztech.milite.machines.guicomponents.RedstoneControlGui;
import aztech.milite.network.BasePacket;
import aztech.milite.network.MIStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SetRedstoneControlPacket(int syncId, boolean enabled, boolean requiresLowSignal) implements BasePacket {
    public static final StreamCodec<ByteBuf, SetRedstoneControlPacket> STREAM_CODEC = StreamCodec.composite(
            MIStreamCodecs.BYTE,
            SetRedstoneControlPacket::syncId,
            ByteBufCodecs.BOOL,
            SetRedstoneControlPacket::enabled,
            ByteBufCodecs.BOOL,
            SetRedstoneControlPacket::requiresLowSignal,
            SetRedstoneControlPacket::new);

    @Override
    public void handle(Context ctx) {
        ctx.assertOnServer();

        if (ctx.getPlayer().containerMenu.containerId == syncId) {
            var screenHandler = (MachineMenuServer) ctx.getPlayer().containerMenu;
            var redstoneControl = screenHandler.blockEntity.guiComponents.getOrThrow(RedstoneControlGui.class).getComponent();
            redstoneControl.setEnabled(screenHandler.blockEntity, enabled);
            redstoneControl.setRequiresLowSignal(screenHandler.blockEntity, requiresLowSignal);
        }
    }
}
