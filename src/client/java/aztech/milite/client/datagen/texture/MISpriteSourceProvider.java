package aztech.milite.client.datagen.texture;

import aztech.milite.MI;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.concurrent.CompletableFuture;

public class MISpriteSourceProvider extends SpriteSourceProvider {
    public MISpriteSourceProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
        super(packOutput, lookupProvider, MI.ID, fileHelper);
    }

    @Override
    protected void gather() {
        atlas(BLOCKS_ATLAS)
                .addSource(new DirectoryLister("fluid", "fluid/"));
    }
}
