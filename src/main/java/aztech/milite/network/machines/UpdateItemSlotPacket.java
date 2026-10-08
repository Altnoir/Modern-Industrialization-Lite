package aztech.milite.network.machines;

import aztech.milite.inventory.ConfigurableItemStack;
import aztech.milite.inventory.ConfigurableScreenHandler;
import aztech.milite.network.BasePacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public record UpdateItemSlotPacket(int syncId, int stackId, ConfigurableItemStack newStack) implements BasePacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateItemSlotPacket> STREAM_CODEC = StreamCodec.ofMember(
            UpdateItemSlotPacket::write, UpdateItemSlotPacket::new);

    public UpdateItemSlotPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readUnsignedByte(), buf.readVarInt(), new ConfigurableItemStack(buf.readNbt(), buf.registryAccess()));
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeByte(syncId);
        buf.writeVarInt(stackId);
        buf.writeNbt(newStack.toNbt(buf.registryAccess()));
    }

    @Override
    public void handle(Context ctx) {
        ctx.assertOnClient();

        AbstractContainerMenu sh = ctx.getPlayer().containerMenu;
        if (sh.containerId == syncId) {
            ConfigurableScreenHandler csh = (ConfigurableScreenHandler) sh;
            ConfigurableItemStack oldStack = csh.inventory.getItemStacks().get(stackId);
            // update stack
            csh.inventory.getItemStacks().set(stackId, newStack);
            // update slot
            for (int i = 0; i < csh.slots.size(); ++i) {
                Slot slot = csh.slots.get(i);
                if (slot instanceof ConfigurableItemStack.ConfigurableItemSlot is) {
                    if (is.getConfStack() == oldStack) {
                        csh.updateSlot(i, newStack.new ConfigurableItemSlot(is));
                        return;
                    }
                }
            }
            throw new RuntimeException("Could not find slot to replace!");
        }
    }
}
