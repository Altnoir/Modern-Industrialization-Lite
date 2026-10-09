package aztech.milite.client.datagen.texture;

import aztech.milite.client.textures.MITextures;
import aztech.milite.MI;
import aztech.milite.resource.FastPathPackResources;
import com.google.common.hash.Hashing;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.Util;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.List;
import java.util.Optional;

public record TexturesProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper, boolean runtimeDatagen) implements DataProvider {
    private static PackLocationInfo makePackInfo(String name) {
        return new PackLocationInfo(name, Component.literal(name), PackSource.BUILT_IN, Optional.empty());
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        var packs = new ArrayList<PackResources>();

        packs.add(new VanillaPackResourcesBuilder().exposeNamespace("minecraft").pushJarResources().build(makePackInfo("vanilla-textures")));

        if (runtimeDatagen) {
            // MI jar
            packs.add(new PathPackResources(makePackInfo("mi:runtimedatagen"),
                    ModList.get().getModFileById(MI.ID).getFile().getSecureJar().getRootPath()));

            // extra_datagen_resources folder
            var extra = FMLPaths.GAMEDIR.get().resolve("milite").resolve("extra_datagen_resources");
            packs.add(new FastPathPackResources(makePackInfo("extra"), extra));
        } else {
            // PathPackResources resolves resources against <root>/<packType>/<namespace>
            var nonGeneratedResources = packOutput.getOutputFolder().getParent().getParent().resolve("main/resources").normalize();
            packs.add(new FastPathPackResources(makePackInfo("nonGen"), nonGeneratedResources));
        }

        List<CompletableFuture<?>> jsonSaveFutures = new ArrayList<>();
        var fallbackProvider = new MultiPackResourceManager(PackType.CLIENT_RESOURCES, packs);

        return generateTextures(cache, fallbackProvider, jsonSaveFutures::add)
                .whenComplete((result, throwable) -> fallbackProvider.close())
                .thenRunAsync(() -> CompletableFuture.allOf(jsonSaveFutures.toArray(CompletableFuture[]::new)), Util.backgroundExecutor());
    }

    private CompletableFuture<?> generateTextures(CachedOutput cache, ResourceProvider resourceProvider,
            Consumer<CompletableFuture<?>> futureList) {
        // Textures are fetched from, in this order:
        // - the main resources tree, which holds the hand drawn textures
        // - MI and MC jar textures
        // A texture that already exists there is left alone, see MITextures.generateMaterialTexture.
        return MITextures.offerTextures(
                (image, textureId) -> writeTexture(cache, image, textureId),
                (json, path) -> futureList.accept(customJsonSave(cache, json, path)),
                resourceProvider,
                existingFileHelper);
    }

    private void writeTexture(CachedOutput cache, NativeImage image, String textureId) {
        try {
            var path = packOutput.getOutputFolder().resolve("assets").resolve(textureId.replace(':', '/'));
            cache.writeIfNeeded(path, image.asByteArray(), Hashing.sha1().hashBytes(image.asByteArray()));
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write texture " + textureId, ex);
        }
    }

    private CompletableFuture<?> customJsonSave(CachedOutput cache, JsonElement jsonElement, String path) {
        Path pathFormatted = packOutput.getOutputFolder().resolve("assets").resolve(path.replace(':', '/'));
        return DataProvider.saveStable(cache, jsonElement, pathFormatted);
    }

    @Override
    public String getName() {
        return "Textures";
    }
}
