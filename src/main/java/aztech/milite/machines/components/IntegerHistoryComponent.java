package aztech.milite.machines.components;

import aztech.milite.machines.MachineComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

/**
 * Tracks a history of values for different enum values as keys.
 * Values will be serialized using the toString() function of the enum.
 */
public class IntegerHistoryComponent<K extends Enum<K>> implements MachineComponent {
    protected final Map<K, int[]> histories;
    protected final int[] updatingValues; // indexed by enum ordinal
    protected final double[] averages; // indexed by enum ordinal

    private final K[] keys;
    private final int tickHistorySize;

    public IntegerHistoryComponent(Class<K> keyType, int tickHistorySize) {
        this.keys = keyType.getEnumConstants();
        this.tickHistorySize = tickHistorySize;

        this.histories = new EnumMap<>(keyType);
        this.updatingValues = new int[keys.length];
        this.averages = new double[keys.length];

        for (K key : keys) {
            histories.put(key, new int[tickHistorySize]);
        }
    }

    @Override
    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        for (K key : keys) {
            tag.putIntArray(key.toString(), histories.get(key));
        }
    }

    @Override
    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        for (K key : keys) {
            String keyString = key.toString();
            if (tag.contains(keyString)) {
                int[] array = tag.getIntArray(keyString);
                if (array.length == tickHistorySize) {
                    histories.put(key, array);
                    continue;
                }
            }
            histories.put(key, new int[tickHistorySize]);
        }

        for (K key : keys) {
            double avg = 0;
            int[] values = histories.get(key);
            for (int value : values) {
                avg += value;
            }
            averages[key.ordinal()] = avg / tickHistorySize;
        }
    }

    public double getAverage(K key) {
        double ret = averages[key.ordinal()];
        // Round to zero if very small - negative values might lead to problems.
        return Math.abs(ret) < 1e-9 ? 0 : ret;
    }

    public void clear() {
        for (var array : histories.values()) {
            Arrays.fill(array, 0);
        }
        Arrays.fill(updatingValues, 0);
        Arrays.fill(averages, 0);
    }

    public void tick() {
        for (K key : keys) {
            int i = key.ordinal();
            int[] valuesArray = histories.get(key);

            // Update average
            averages[i] += (double) (updatingValues[i] - valuesArray[tickHistorySize - 1]) / tickHistorySize;

            // Shift values by 1 and add updating value at the beginning.
            System.arraycopy(valuesArray, 0, valuesArray, 1, tickHistorySize - 1);
            valuesArray[0] = updatingValues[i];
            updatingValues[i] = 0;
        }
    }

    public void addValue(K key, int delta) {
        updatingValues[key.ordinal()] += delta;
    }
}
