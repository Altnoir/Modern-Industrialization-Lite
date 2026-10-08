package aztech.milite.network.armor;

import aztech.milite.items.armor.MIKeyMap;
import aztech.milite.network.BasePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record UpdateKeysPacket(boolean up) implements BasePacket {
    public static final StreamCodec<ByteBuf, UpdateKeysPacket> STREAM_CODEC = ByteBufCodecs.BOOL
            .map(UpdateKeysPacket::new, UpdateKeysPacket::up);

    @Override
    public void handle(Context ctx) {
        MIKeyMap.update(ctx.getPlayer(), up);
    }
}
