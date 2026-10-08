package aztech.milite.materials.property;

public enum MaterialHardness {
    SOFT(0.5),
    AVERAGE(1.0),
    HARD(2.0),
    VERY_HARD(4.0);

    public final double timeFactor;

    MaterialHardness(double timeFactor) {
        this.timeFactor = timeFactor;
    }
}
