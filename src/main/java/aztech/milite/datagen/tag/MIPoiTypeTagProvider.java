package aztech.milite.datagen.tag;

import aztech.milite.MI;
import aztech.milite.MIRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.tags.PoiTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MIPoiTypeTagProvider extends PoiTypeTagsProvider {
    public MIPoiTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MI.ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(PoiTypeTags.ACQUIRABLE_JOB_SITE).add(MIRegistries.INDUSTRIALIST_POI.getKey());
    }
}
