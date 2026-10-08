package aztech.milite.client.datagen.model;

import aztech.milite.client.machines.models.UseBlockModelUnbakedModel;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class UseBlockModelModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
    private final Block targetBlock;

    protected UseBlockModelModelBuilder(Block targetBlock, T parent, ExistingFileHelper existingFileHelper) {
        super(UseBlockModelUnbakedModel.LOADER_ID, parent, existingFileHelper, false);

        this.targetBlock = targetBlock;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        var ret = super.toJson(json);
        ret.addProperty("block", BuiltInRegistries.BLOCK.getKey(targetBlock).toString());
        return ret;
    }
}
