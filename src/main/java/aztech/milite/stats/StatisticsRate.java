package aztech.milite.stats;

public enum StatisticsRate {
    LAST_SECOND(0, 20),
    LAST_MINUTE(1, 20 * 60),
    LAST_HOUR(2, 20 * 60 * 60),
    ;

    public static final int COUNT = 3;

    public final int id;
    public final int ticks;

    StatisticsRate(int id, int ticks) {
        this.id = id;
        this.ticks = ticks;
    }
}
