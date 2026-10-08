package aztech.milite.network.machines;

import aztech.milite.inventory.ConfigurableScreenHandler;
import aztech.milite.network.BasePacket;
import aztech.milite.network.MIStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record LockAllPacket(int containerId, boolean lock) implements BasePacket {
    public static final StreamCodec<ByteBuf, LockAllPacket> STREAM_CODEC = StreamCodec.composite(
            MIStreamCodecs.BYTE,
            LockAllPacket::containerId,
            ByteBufCodecs.BOOL,
            LockAllPacket::lock,
            LockAllPacket::new);

    public void handle(Context ctx) {
        ctx.assertOnServer();

        AbstractContainerMenu menu = ctx.getPlayer().containerMenu;
        if (menu.containerId == containerId) {
            ((ConfigurableScreenHandler) menu).lockAll(lock);
        }
    }
}
