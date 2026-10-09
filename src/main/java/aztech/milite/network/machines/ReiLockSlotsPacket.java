package aztech.milite.network.machines;

import aztech.milite.machines.gui.MachineMenuServer;
import aztech.milite.machines.guicomponents.ReiSlotLocking;
import aztech.milite.network.BasePacket;
import aztech.milite.network.MIStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record ReiLockSlotsPacket(int containedId, ResourceLocation recipeId) implements BasePacket {
    public static final StreamCodec<ByteBuf, ReiLockSlotsPacket> STREAM_CODEC = StreamCodec.composite(
            MIStreamCodecs.BYTE,
            ReiLockSlotsPacket::containedId,
            ResourceLocation.STREAM_CODEC,
            ReiLockSlotsPacket::recipeId,
            ReiLockSlotsPacket::new);

    @Override
    public void handle(Context ctx) {
        ctx.assertOnServer();

        AbstractContainerMenu sh = ctx.getPlayer().containerMenu;
        if (sh.containerId == containedId && sh instanceof MachineMenuServer screenHandler) {
            // Check that locking the slots is allowed in the first place
            var slotLocking = screenHandler.blockEntity.guiComponents.getOrThrow(ReiSlotLocking.class);
            if (!slotLocking.allowLocking().get())
                return;

            slotLocking.slotLockable().lockSlots(recipeId, ctx.getPlayer().getInventory());
        }
    }
}
