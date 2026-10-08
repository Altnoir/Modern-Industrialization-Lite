package aztech.milite.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.HoneycombItem;

/**
 * Grants the vanilla "Wax On" advancement on successful usage.
 */
public class WaxItem extends HoneycombItem {
    public WaxItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var ret = super.useOn(context);
        if (ret.indicateItemUse()) {
            if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
                grantWaxOn(serverPlayer);
            }
        }
        return ret;
    }

    private static final ResourceLocation WAX_ON = ResourceLocation.withDefaultNamespace("husbandry/wax_on");

    private static void grantWaxOn(ServerPlayer serverPlayer) {
        var advancement = serverPlayer.server.getAdvancements().get(WAX_ON);
        if (advancement == null) {
            return;
        }

        var playerAdvancements = serverPlayer.server.getPlayerList().getPlayerAdvancements(serverPlayer);
        playerAdvancements.award(advancement, "wax_on");
    }
}
