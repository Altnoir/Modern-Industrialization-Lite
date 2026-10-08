package aztech.milite.materials.set;

import org.jspecify.annotations.Nullable;

public enum MaterialOreSet {
    IRON("iron"),
    GOLD("gold"),
    DIAMOND("diamond"),
    OLD("old"),
    LAPIS("lapis"),
    REDSTONE("redstone"),
    COPPER("copper"),
    EMERALD("emerald"),
    COAL("coal"),
    QUARTZ("quartz"),
    NETHER_GOLD("nether_gold");

    MaterialOreSet(String name) {
        this.name = name;
    }

    public final String name;

    @Nullable
    public static MaterialOreSet getByName(String ore_set) {
        for (MaterialOreSet set : values()) {
            if (set.name.equals(ore_set)) {
                return set;
            }
        }
        return null;
    }
}
