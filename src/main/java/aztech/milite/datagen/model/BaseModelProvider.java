package aztech.milite.datagen.model;

import aztech.milite.MI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public abstract class BaseModelProvider extends BlockStateProvider {
    public BaseModelProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MI.ID, exFileHelper);
    }

    protected ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public String name(Block block) {
        return key(block).getPath();
    }

    public ResourceLocation blockTexture(String path) {
        return modLoc(ModelProvider.BLOCK_FOLDER + "/" + path);
    }

    public void existingModel(Block block) {
        existingModel(block, block);
    }

    public void existingModel(Block block, Block targetBlock) {
        simpleBlock(block, models().getExistingFile(blockTexture(targetBlock)));
    }

    public void existingModelWithItem(Block block) {
        simpleBlockWithItem(block, models().getExistingFile(blockTexture(block)));
    }
}
