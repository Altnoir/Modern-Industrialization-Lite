package aztech.milite.network;

import aztech.milite.MICommonProxy;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;

public interface BasePacket extends CustomPacketPayload {
    void handle(Context ctx);

    default void sendToServer() {
        PacketDistributor.sendToServer(this);
    }

    default void sendToClient(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, this);
    }

    @Override
    default Type<? extends CustomPacketPayload> type() {
        return MIPackets.packetTypes.get(getClass());
    }

    record Context(Class<? extends BasePacket> clazz, IPayloadContext inner) {
        public boolean isOnClient() {
            return inner.flow().isClientbound();
        }

        public void assertOnServer() {
            if (isOnClient()) {
                throw new IllegalArgumentException("Cannot handle packet on client: " + clazz);
            }
        }

        public void assertOnClient() {
            if (!isOnClient()) {
                throw new IllegalArgumentException("Cannot handle packet on server: " + clazz);
            }
        }

        public Player getPlayer() {
            return isOnClient() ? MICommonProxy.INSTANCE.getClientPlayer() : inner.player();
        }
    }
}
