package aztech.milite.network.machines;

import aztech.milite.machines.gui.MachineMenuServer;
import aztech.milite.machines.guicomponents.MultiblockBuild;
import aztech.milite.network.BasePacket;
import aztech.milite.network.MIStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record BuildMultiblockPacket(int syncId) implements BasePacket {
    public static final StreamCodec<ByteBuf, BuildMultiblockPacket> STREAM_CODEC = MIStreamCodecs.BYTE
            .map(syncId -> new BuildMultiblockPacket(syncId), BuildMultiblockPacket::syncId);

    @Override
    public void handle(Context ctx) {
        ctx.assertOnServer();

        AbstractContainerMenu menu = ctx.getPlayer().containerMenu;
        if (menu.containerId == syncId && menu instanceof MachineMenuServer machineMenu) {
            var build = machineMenu.blockEntity.guiComponents.getNullable(MultiblockBuild.class);
            if (build != null) {
                build.build(ctx.getPlayer());
            }
        }
    }
}
