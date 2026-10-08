package aztech.milite.stats;

import aztech.milite.util.TickHelper;
import net.minecraft.nbt.CompoundTag;

public class StatisticValue {
    private long allTime = 0;
    private final long[] pastRates = new long[StatisticsRate.COUNT];
    private final long[] currentRates = new long[StatisticsRate.COUNT];
    private final int[] remainingTicks = new int[StatisticsRate.COUNT];
    private long lastTick = 0;

    public StatisticValue() {}

    public StatisticValue(CompoundTag nbt) {
        allTime = nbt.getLong("at");
        for (var rate : StatisticsRate.values()) {
            pastRates[rate.id] = nbt.getLong("p" + rate.id);
            currentRates[rate.id] = nbt.getLong("c" + rate.id);
            remainingTicks[rate.id] = nbt.getInt("r" + rate.id);
        }
        lastTick = TickHelper.getCurrentTick();
    }

    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("at", allTime);
        for (var rate : StatisticsRate.values()) {
            nbt.putLong("p" + rate.id, pastRates[rate.id]);
            nbt.putLong("c" + rate.id, currentRates[rate.id]);
            nbt.putInt("r" + rate.id, remainingTicks[rate.id]);
        }
        return nbt;
    }

    public void add(long toAdd) {
        updateRates();

        allTime += toAdd;
        for (var rate : StatisticsRate.values()) {
            currentRates[rate.id] += toAdd;
        }
    }

    public void updateRates() {
        long tickDiff = TickHelper.getCurrentTick() - lastTick;
        if (tickDiff > 0) {
            for (var rate : StatisticsRate.values()) {
                int i = rate.id;
                if (tickDiff < remainingTicks[i]) {
                    remainingTicks[i] -= tickDiff;
                } else if (tickDiff < rate.ticks + remainingTicks[i]) {
                    pastRates[i] = currentRates[i];
                    currentRates[i] = 0;
                    remainingTicks[i] = rate.ticks - ((int) tickDiff - remainingTicks[i]);
                } else {
                    pastRates[i] = 0;
                    currentRates[i] = 0;
                    remainingTicks[i] = rate.ticks;
                }
            }
        }
    }
}
