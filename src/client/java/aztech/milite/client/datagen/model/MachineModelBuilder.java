package aztech.milite.client.datagen.model;

import aztech.milite.client.machines.models.MachineUnbakedModel;
import aztech.milite.datagen.model.MachineModelProperties;
import com.google.gson.JsonObject;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

class MachineModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
    private final MachineModelProperties props;

    protected MachineModelBuilder(MachineModelProperties props, T parent, ExistingFileHelper existingFileHelper) {
        super(MachineUnbakedModel.LOADER_ID, parent, existingFileHelper, false);

        this.props = props;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        var ret = super.toJson(json);
        props.addToMachineJson(ret);
        return ret;
    }
}
