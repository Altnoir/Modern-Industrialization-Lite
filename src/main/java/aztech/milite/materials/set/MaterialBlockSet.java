package aztech.milite.materials.set;

import org.jspecify.annotations.Nullable;

public enum MaterialBlockSet {
    IRON("iron"),
    GOLD("diamond"),
    DIAMOND("diamond"),
    COPPER("copper"),
    LAPIS("lapis"),
    REDSTONE("redstone"),
    EMERALD("emerald"),
    COAL("coal"),
    NETHERITE("netherite");

    MaterialBlockSet(String name) {
        this.name = name;
    }

    public final String name;

    @Nullable
    public static MaterialBlockSet getByName(String materialSet) {
        for (MaterialBlockSet set : values()) {
            if (set.name.equals(materialSet)) {
                return set;
            }
        }
        return null;
    }
}
