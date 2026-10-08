package aztech.milite.nuclear;

public enum NeutronType {
    FAST(0),
    THERMAL(1),
    BOTH(2);

    public final int index;
    public static final NeutronType[] TYPES = { FAST, THERMAL, BOTH };

    NeutronType(int index) {
        this.index = index;
    }
}
