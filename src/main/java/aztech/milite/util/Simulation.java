package aztech.milite.util;

public enum Simulation {
    SIMULATE,
    ACT;

    public boolean isActing() {
        return this == ACT;
    }
}
