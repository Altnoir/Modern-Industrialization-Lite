package aztech.milite.inventory;

import java.util.*;

/**
 * Immutable flags that represent the configuration of a configurable item or
 * fluid stack. This is used to recover stacks across inventory changes.
 */
public class SlotConfig {
    private final boolean playerLockable;
    private final boolean playerInsert;
    private final boolean playerExtract;
    private final boolean pipesInsert;
    private final boolean pipesExtract;

    SlotConfig(boolean playerLockable, boolean playerInsert, boolean playerExtract, boolean pipesInsert, boolean pipesExtract) {
        this.playerLockable = playerLockable;
        this.playerInsert = playerInsert;
        this.playerExtract = playerExtract;
        this.pipesInsert = pipesInsert;
        this.pipesExtract = pipesExtract;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SlotConfig that = (SlotConfig) o;
        return playerLockable == that.playerLockable && playerInsert == that.playerInsert && playerExtract == that.playerExtract
                && pipesInsert == that.pipesInsert && pipesExtract == that.pipesExtract;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerLockable, playerInsert, playerExtract, pipesInsert, pipesExtract);
    }

    /**
     * Read new list into the old list, replacing slots that have a matching config
     * in the new list.
     */
    public static <T extends ConfigurableSlot> void readSlotList(List<T> oldList, List<T> newList) {
        // Store positions of slots with the old config
        Map<SlotConfig, ArrayDeque<Integer>> configMatches = new HashMap<>();
        for (int i = 0; i < oldList.size(); ++i) {
            configMatches.computeIfAbsent(oldList.get(i).getConfig(), c -> new ArrayDeque<>()).addLast(i);
        }
        // Use slots from the new list when possible
        for (T newSlot : newList) {
            ArrayDeque<Integer> positions = configMatches.get(newSlot.getConfig());
            if (positions != null && positions.size() > 0) {
                oldList.set(positions.pollFirst(), newSlot);
            }
        }
    }
}
