package aztech.milite.items.armor;

import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.WeakHashMap;

public class MIKeyMap {
    private static final Map<Player, Boolean> HOLDING_UP = new WeakHashMap<>();

    static boolean isHoldingUp(Player player) {
        return HOLDING_UP.getOrDefault(player, false);
    }

    public static void update(Player player, boolean up) {
        HOLDING_UP.put(player, up);
    }

    public static void clear(Player player) {
        HOLDING_UP.remove(player);
    }
}
