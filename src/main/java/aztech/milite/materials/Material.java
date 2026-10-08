package aztech.milite.materials;

import aztech.milite.materials.part.MaterialItemPart;
import aztech.milite.materials.part.PartKey;
import aztech.milite.materials.part.PartKeyProvider;
import aztech.milite.materials.property.MaterialProperty;
import net.minecraft.data.recipes.RecipeOutput;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.Map;

/**
 * A read-only material. Build with {@link MaterialBuilder}.
 */
public class Material {
    public final String name;
    final Map<PartKey, MaterialItemPart> parts;
    private final Map<MaterialProperty<?>, Object> properties;

    public final Consumer<RecipeOutput> registerRecipes;

    Material(String name, Map<MaterialProperty<?>, Object> properties, Map<PartKey, MaterialItemPart> parts,
            Consumer<RecipeOutput> registerRecipes) {
        this.name = name;
        this.properties = properties;
        this.parts = parts;
        this.registerRecipes = registerRecipes;
    }

    public Map<PartKey, MaterialItemPart> getParts() {
        return Collections.unmodifiableMap(parts);
    }

    @Nullable
    public MaterialItemPart getNullablePart(PartKeyProvider part) {
        return parts.get(part.key());
    }

    public MaterialItemPart getPart(PartKeyProvider part) {
        var ret = parts.get(part.key());
        if (ret == null) {
            throw new IllegalArgumentException("Can't find part " + part + " in material " + name);
        }
        return ret;
    }

    @Nullable
    public <T> T get(MaterialProperty<T> prop) {
        return (T) properties.get(prop);
    }
}
