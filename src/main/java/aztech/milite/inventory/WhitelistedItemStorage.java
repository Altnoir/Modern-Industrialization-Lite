package aztech.milite.inventory;

import net.minecraft.world.item.Item;

import java.util.Set;

public interface WhitelistedItemStorage {
    boolean currentlyWhitelisted();

    /**
     * Write whitelisted items to the set.
     */
    void getWhitelistedItems(Set<Item> item);
}
