package aztech.milite.materials.set;

public enum MaterialRawSet {
    IRON("iron"),
    GOLD("gold"),
    COPPER("copper"),
    URANIUM("uranium"),
    IRIDIUM("iridium");

    MaterialRawSet(String name) {
        this.name = name;
    }

    public final String name;

    public static MaterialRawSet getByName(String materialSet) {
        for (MaterialRawSet set : values()) {
            if (set.name.equals(materialSet)) {
                return set;
            }
        }
        return null;
    }
}
