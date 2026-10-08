package aztech.milite.materials.set;

public enum MaterialSet {
    METALLIC("metallic"),
    SHINY("shiny"),
    STONE("stone"),
    DULL("dull");

    MaterialSet(String name) {
        this.name = name;
    }

    public final String name;
}
