package aztech.milite.materials.property;

import aztech.milite.materials.part.MIParts;
import aztech.milite.materials.part.PartKeyProvider;
import aztech.milite.materials.set.MaterialSet;
import aztech.milite.nuclear.IsotopeFuelParams;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Some extra property for a material
 */
public class MaterialProperty<T> {
    private static final Map<String, MaterialProperty<?>> PROPERTY_IDS = new HashMap<>();
    public static final Collection<MaterialProperty<?>> PROPERTIES = Collections.unmodifiableCollection(PROPERTY_IDS.values());

    public final T defaultValue;

    public MaterialProperty(String id, T defaultValue) {
        this.defaultValue = defaultValue;

        if (PROPERTY_IDS.put(id, this) != null) {
            throw new IllegalArgumentException("Duplicate material property id: " + id);
        }
    }

    /**
     * Defines the color to use for the material.
     */
    public static final MaterialProperty<Integer> MEAN_RGB = new MaterialProperty<>("mean_rgb", 0);
    /**
     * The hardness influences the speed of some recipes.
     */
    public static final MaterialProperty<MaterialHardness> HARDNESS = new MaterialProperty<>("hardness", MaterialHardness.AVERAGE);
    /**
     * Defines the "main" part, i.e. the default form of the material. (ingot for metals, gem for gems, etc.)
     */
    public static final MaterialProperty<PartKeyProvider> MAIN_PART = new MaterialProperty<>("main_part", MIParts.INGOT);
    /**
     * Which template set to use for the textures.
     */
    public static final MaterialProperty<MaterialSet> SET = new MaterialProperty<>("material_set", MaterialSet.DULL);
    /**
     * Isotope parameters for nuclear fission fuels.
     */
    public static final MaterialProperty<@Nullable IsotopeFuelParams> ISOTOPE = new MaterialProperty<>("isotope", null);
}
