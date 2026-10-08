package aztech.milite.machines.init;

public enum MachineTier {
    BRONZE("bronze", true, 2, 2),
    STEEL("steel", true, 4, 4),
    LV("lv", false, 32, 8),
    MULTIBLOCK("multiblock", false, 128, 8),
    UNLIMITED("unlimited", false, Integer.MAX_VALUE, 8);

    private final String name;
    private final boolean steam;
    private final int maxRf;
    private final int baseRf;

    MachineTier(String name, boolean steam, int maxRf, int baseRf) {
        this.name = name;
        this.steam = steam;
        this.maxRf = maxRf;
        this.baseRf = baseRf;
    }

    public boolean isSteam() {
        return steam;
    }

    public boolean isElectric() {
        return !steam;
    }

    public int getBaseRf() {
        return baseRf;
    }

    public int getMaxRf() {
        return maxRf;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getMaxStoredRf() {
        return isSteam() ? 0 : getMaxRf() * 100;
    }
}
