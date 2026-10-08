package aztech.milite.materials;

import aztech.milite.compat.kubejs.KubeJSProxy;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class MaterialRegistry {
    static final Map<String, Material> MATERIALS = new TreeMap<>();

    public static Material addMaterial(MaterialBuilder materialBuilder) {
        KubeJSProxy.instance.fireModifyMaterialEvent(materialBuilder);

        Material material = materialBuilder.build();
        if (MATERIALS.put(material.name, material) != null) {
            throw new IllegalStateException("Duplicate registration of material " + material.name);
        }
        return material;
    }

    public static Material getMaterial(String name) {
        Material material = MATERIALS.get(name);
        if (material == null) {
            throw new IllegalArgumentException("No such material: " + name);
        }
        return material;
    }

    public static Map<String, Material> getMaterials() {
        return Collections.unmodifiableMap(MATERIALS);
    }
}
