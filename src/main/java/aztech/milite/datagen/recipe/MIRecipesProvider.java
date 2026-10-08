package aztech.milite.datagen.recipe;

import aztech.milite.MI;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public abstract class MIRecipesProvider extends RecipeProvider {
    public MIRecipesProvider(PackOutput packOutput) {
        super(packOutput, CompletableFuture.completedFuture(HolderLookup.Provider.create(Stream.of())));
    }

    protected static ResourceLocation id(String path) {
        return MI.id(path);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
