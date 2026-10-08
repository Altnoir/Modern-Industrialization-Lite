package aztech.milite.network.machines;

import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.gui.MachineMenuServer;
import aztech.milite.machines.guicomponents.AutoExtract;
import aztech.milite.network.BasePacket;
import aztech.milite.network.MIStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SetAutoExtractPacket(int syncId, boolean isItem, boolean isExtract) implements BasePacket {
    public static final StreamCodec<ByteBuf, SetAutoExtractPacket> STREAM_CODEC = StreamCodec.composite(
            MIStreamCodecs.BYTE,
            SetAutoExtractPacket::syncId,
            ByteBufCodecs.BOOL,
            SetAutoExtractPacket::isItem,
            ByteBufCodecs.BOOL,
            SetAutoExtractPacket::isExtract,
            SetAutoExtractPacket::new);

    @Override
    public void handle(Context ctx) {
        ctx.assertOnServer();

        if (ctx.getPlayer().containerMenu.containerId == syncId) {
            var screenHandler = (MachineMenuServer) ctx.getPlayer().containerMenu;
            var autoExtract = screenHandler.blockEntity.guiComponents.getOrThrow(AutoExtract.class);
            OrientationComponent orientation = autoExtract.getOrientation();
            if (isItem) {
                orientation.extractItems = isExtract;
            } else {
                orientation.extractFluids = isExtract;
            }
            screenHandler.blockEntity.setChanged();
            screenHandler.blockEntity.sync();
        }
    }
}
