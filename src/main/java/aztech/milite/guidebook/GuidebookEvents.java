package aztech.milite.guidebook;

import aztech.milite.config.MIServerConfig;
import aztech.milite.MIAdvancementTriggers;
import aztech.milite.MIItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class GuidebookEvents {
    public static void init() {
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedInEvent.class, event -> {
            var player = event.getEntity();
            if (MIServerConfig.INSTANCE.spawnWithGuideBook.getAsBoolean()) {
                GuidebookPersistentState state = GuidebookPersistentState.get(player.getServer());
                if (!state.hasPlayerReceivedGuidebook(player)) {
                    if (player.getInventory().add(new ItemStack(MIItem.GUIDE_BOOK))) {
                        state.addPlayerReceivedGuidebook(player);
                    }
                }
            }
            // In any case, fire the logged in trigger
            MIAdvancementTriggers.PLAYER_LOGGED_IN.get().trigger((ServerPlayer) player);
        });

        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerRespawnEvent.class, event -> {
            if (!event.isEndConquered() && MIServerConfig.INSTANCE.respawnWithGuideBook.getAsBoolean()) {
                event.getEntity().getInventory().add(new ItemStack(MIItem.GUIDE_BOOK));
            }
        });
    }
}
