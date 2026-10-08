package aztech.milite.client.datagen.texture;

// fetch:

// - datagen override (des resource packs, MI jar)
// - datagen
// - resource pack utilisateur
// - jar MI et MC client
// generate texture:
// - if override exists: copy from override
// - else: generate

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
            var nonGeneratedResources = packOutput.getOutputFolder().resolve("../../main/resources");
            packs.add(new FastPathPackResources(makePackInfo("nonGen"), nonGeneratedResources));
        }

        List<CompletableFuture<?>> jsonSaveFutures = new ArrayList<>();
        var fallbackProvider = new MultiPackResourceManager(PackType.CLIENT_RESOURCES, packs);

        return generateTextures(cache, fallbackProvider, jsonSaveFutures::add)
                .whenComplete((result, throwable) -> fallbackProvider.close())
                .thenRunAsync(() -> CompletableFuture.allOf(jsonSaveFutures.toArray(CompletableFuture[]::new)), Util.backgroundExecutor());
    }

    private CompletableFuture<?> generateTextures(CachedOutput cache, ResourceProvider fallbackResourceProvider,
            Consumer<CompletableFuture<?>> futureList) {
        // This is the order for texture fetching:
        // - texture overrides (in the datagen output folder)
        // - generated textures (also in the output folder)
        // - user-provided resource packs
        // - MI and MC jar textures
        var generatedResources = packOutput.getOutputFolder();
        List<PackResources> generatedPack = List.of(new FastPathPackResources(makePackInfo("gen"), generatedResources));

        var outputPack = new MultiPackResourceManager(PackType.CLIENT_RESOURCES, generatedPack);
        return MITextures.offerTextures(
                (image, textureId) -> writeTexture(cache, image, textureId),
                (json, path) -> futureList.accept(customJsonSave(cache, json, path)),
                resourceLocation -> {
                    // Generated first
                    var generated = outputPack.getResource(resourceLocation);
                    if (generated.isPresent()) {
                        return generated;
                    }
                    return fallbackResourceProvider.getResource(resourceLocation);
                },
                existingFileHelper)
                .whenComplete((result, throwable) -> outputPack.close());
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
