package aztech.milite.network.machines;

import aztech.milite.inventory.ConfigurableScreenHandler;
import aztech.milite.network.BasePacket;
import aztech.milite.network.MIStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record SetLockingModePacket(int syncId, boolean lockingMode) implements BasePacket {
    public static final StreamCodec<ByteBuf, SetLockingModePacket> STREAM_CODEC = StreamCodec.composite(
            MIStreamCodecs.BYTE,
            SetLockingModePacket::syncId,
            ByteBufCodecs.BOOL,
            SetLockingModePacket::lockingMode,
            SetLockingModePacket::new);

    @Override
    public void handle(Context ctx) {
        ctx.assertOnServer();

        AbstractContainerMenu sh = ctx.getPlayer().containerMenu;
        if (sh.containerId == syncId) {
            ConfigurableScreenHandler csh = (ConfigurableScreenHandler) sh;
            csh.lockingMode = lockingMode;
        }
    }
}
