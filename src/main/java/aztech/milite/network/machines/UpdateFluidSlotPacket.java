package aztech.milite.network.machines;

import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.ConfigurableScreenHandler;
import aztech.milite.network.BasePacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public record UpdateFluidSlotPacket(int syncId, int stackId, ConfigurableFluidStack newStack) implements BasePacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateFluidSlotPacket> STREAM_CODEC = StreamCodec.ofMember(
            UpdateFluidSlotPacket::write, UpdateFluidSlotPacket::new);

    public UpdateFluidSlotPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readUnsignedByte(), buf.readVarInt(), new ConfigurableFluidStack(buf.readNbt(), buf.registryAccess()));
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
            ConfigurableFluidStack oldStack = csh.inventory.getFluidStacks().get(stackId);
            csh.inventory.getFluidStacks().set(stackId, newStack);
            for (int i = 0; i < csh.slots.size(); ++i) {
                Slot slot = csh.slots.get(i);
                if (slot instanceof ConfigurableFluidStack.ConfigurableFluidSlot fs) {
                    if (fs.getConfStack() == oldStack) {
                        csh.updateSlot(i, newStack.new ConfigurableFluidSlot(fs));
                        return;
                    }
                }
            }
            throw new RuntimeException("Could not find slot to replace!");
        }
    }
}
