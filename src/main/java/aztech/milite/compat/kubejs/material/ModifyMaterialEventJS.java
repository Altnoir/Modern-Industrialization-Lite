package aztech.milite.compat.kubejs.material;

import aztech.milite.materials.MaterialBuilder;
import dev.latvian.mods.kubejs.event.KubeEvent;

public class ModifyMaterialEventJS implements KubeEvent {
    public final MaterialBuilderJSWrapper builder;
    public final String name;

    public ModifyMaterialEventJS(MaterialBuilder materialBuilder) {
        this.builder = new MaterialBuilderJSWrapper(materialBuilder);
        this.name = materialBuilder.getMaterialName();
    }
}
