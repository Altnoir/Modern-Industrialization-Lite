package aztech.milite.network.armor;

import aztech.milite.items.ActivatableItem;
import aztech.milite.network.BasePacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record ActivateItemPacket(EquipmentSlot slot, boolean activated) implements BasePacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, ActivateItemPacket> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(EquipmentSlot.class),
            ActivateItemPacket::slot,
            ByteBufCodecs.BOOL,
            ActivateItemPacket::activated,
            ActivateItemPacket::new);

    @Override
    public void handle(Context ctx) {
        Player player = ctx.getPlayer();
        ItemStack item = player.getItemBySlot(slot);
        if (item.getItem() instanceof ActivatableItem activatable) {
            activatable.setActivated(item, activated);
        }
    }
}
