package aztech.milite.compat.rei.machines;

public enum SteamMode {
    STEAM_ONLY(true, false),
    ELECTRIC_ONLY(false, true),
    BOTH(true, true),
    NEITHER(false, false);

    public final boolean steam;
    public final boolean electric;

    SteamMode(boolean steam, boolean electric) {
        this.steam = steam;
        this.electric = electric;
    }
}
