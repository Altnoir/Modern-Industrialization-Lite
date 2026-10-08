package aztech.milite.network.machines;

import aztech.milite.machines.gui.MachineMenuServer;
import aztech.milite.machines.guicomponents.ShapeSelection;
import aztech.milite.network.BasePacket;
import aztech.milite.network.MIStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record ChangeShapePacket(int syncId, int shapeLine, boolean clickedLeftButton) implements BasePacket {
    public static final StreamCodec<ByteBuf, ChangeShapePacket> STREAM_CODEC = StreamCodec.composite(
            MIStreamCodecs.BYTE,
            ChangeShapePacket::syncId,
            ByteBufCodecs.VAR_INT,
            ChangeShapePacket::shapeLine,
            ByteBufCodecs.BOOL,
            ChangeShapePacket::clickedLeftButton,
            ChangeShapePacket::new);

    @Override
    public void handle(Context ctx) {
        ctx.assertOnServer();

        AbstractContainerMenu menu = ctx.getPlayer().containerMenu;
        if (menu.containerId == syncId && menu instanceof MachineMenuServer machineMenu) {
            // Not every multiblock has a shape to select, so a missing component is ignored instead of throwing.
            var shapeSelection = machineMenu.blockEntity.guiComponents.getNullable(ShapeSelection.class);
            if (shapeSelection != null) {
                shapeSelection.behavior.handleClick(shapeLine, clickedLeftButton ? -1 : +1);
            }
        }
    }
}
