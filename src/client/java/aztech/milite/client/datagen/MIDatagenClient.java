package aztech.milite.client.datagen;

import aztech.milite.client.datagen.model.MachineCasingsProvider;
import aztech.milite.client.datagen.model.MIModelProvider;
import aztech.milite.client.datagen.texture.MISpriteSourceProvider;
import aztech.milite.client.datagen.texture.TexturesProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class MIDatagenClient {
    public static void configure(
            DataGenerator gen,
            ExistingFileHelper fileHelper,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            boolean run,
            boolean runtimeDatagen) {
        gen.addProvider(run, new MISpriteSourceProvider(gen.getPackOutput(), lookupProvider, fileHelper));
        gen.addProvider(run, new TexturesProvider(gen.getPackOutput(), fileHelper, runtimeDatagen));
        gen.addProvider(run, new MachineCasingsProvider(gen.getPackOutput(), fileHelper));
        gen.addProvider(run, new MIModelProvider(gen.getPackOutput(), fileHelper));
    }
}
