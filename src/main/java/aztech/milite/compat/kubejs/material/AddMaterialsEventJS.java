package aztech.milite.compat.kubejs.material;

import aztech.milite.materials.MaterialBuilder;
import aztech.milite.materials.MaterialRegistry;
import aztech.milite.materials.property.MaterialProperty;
import dev.latvian.mods.kubejs.event.KubeEvent;

import java.util.function.Consumer;

public class AddMaterialsEventJS implements KubeEvent {
    public void createMaterial(String englishName, String materialName, int color, Consumer<MaterialBuilderJSWrapper> builder) {
        var materialBuilder = new MaterialBuilder(englishName, materialName).set(MaterialProperty.MEAN_RGB, color);
        var materialBuilderJS = new MaterialBuilderJSWrapper(materialBuilder);
        builder.accept(materialBuilderJS);
        MaterialRegistry.addMaterial(materialBuilder);
    }
}
