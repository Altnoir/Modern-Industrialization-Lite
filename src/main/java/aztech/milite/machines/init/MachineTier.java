package aztech.milite.machines.init;

public enum MachineTier {
    BRONZE("bronze", true, 2, 2),
    STEEL("steel", true, 4, 4),
    ELECTRIC("electric", false, 32, 8),
    MULTIBLOCK("multiblock", false, 128, 8),
    UNLIMITED("unlimited", false, Integer.MAX_VALUE, 8);

    private final String name;
    private final boolean steam;
    private final int maxFe;
    private final int baseFe;

    MachineTier(String name, boolean steam, int maxFe, int baseFe) {
        this.name = name;
        this.steam = steam;
        this.maxFe = maxFe;
        this.baseFe = baseFe;
    }

    public boolean isSteam() {
        return steam;
    }

    public boolean isElectric() {
        return !steam;
    }

    public int getBaseFe() {
        return baseFe;
    }

    public int getMaxFe() {
        return maxFe;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getMaxStoredFe() {
        return isSteam() ? 0 : getMaxFe() * 100;
    }
}
