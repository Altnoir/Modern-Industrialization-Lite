package aztech.milite.client.textures;

import aztech.milite.textures.MCMetaInfo;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.Util;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class TextureManager {
    private final ResourceProvider rm;
    private final BiConsumer<NativeImage, String> textureWriter;
    private final BiConsumer<JsonElement, String> mcMetaWriter;
    private final Queue<IORunnable> endRunnables = new ConcurrentLinkedQueue<>();
    private final Set<String> generatedTextures = ConcurrentHashMap.newKeySet();

    private final Gson GSON = new Gson();

    public TextureManager(ResourceProvider rm, BiConsumer<NativeImage, String> textureWriter, BiConsumer<JsonElement, String> mcMetaWriter) {
        this.rm = rm;
        this.textureWriter = textureWriter;
        this.mcMetaWriter = mcMetaWriter;
    }

    public boolean hasAsset(String asset) {
        return rm.getResource(ResourceLocation.parse(asset)).isPresent();
    }

    public NativeImage getAssetAsTexture(String textureId) throws IOException {
        var resource = rm.getResource(ResourceLocation.parse(textureId));
        if (resource.isPresent()) {
            try (var stream = resource.get().open()) {
                return NativeImage.read(stream);
            }
        } else {
            throw new IOException("Couldn't find texture " + textureId);
        }
    }

    /**
     * Add texture if it's not already loaded, but doesn't close the image.
     */
    public void addTexture(String textureId, NativeImage image) throws IOException {
        addTexture(textureId, image, false);
    }

    public void addTexture(String textureId, NativeImage image, boolean closeImage) throws IOException {
        // Every caller checks hasAsset first, so a texture that already exists in the main tree is never replaced.
        if (!textureId.contains(":textures/")) {
            throw new IllegalArgumentException("Invalid texture location: " + textureId);
        }

        textureWriter.accept(image, textureId);

        generatedTextures.add(textureId.replace(":textures/", ":"));

        if (closeImage) {
            image.close();
        }
    }

    public void addMcMeta(String path, MCMetaInfo info) {
        mcMetaWriter.accept(GSON.toJsonTree(info), path);
    }

    public void runAtEnd(IORunnable runnable) {
        endRunnables.add(runnable);
    }

    public CompletableFuture<?> doEndWork() {
        var ret = CompletableFuture.allOf(
                endRunnables.stream().map(r -> CompletableFuture.runAsync(r::safeRun, Util.backgroundExecutor())).toArray(CompletableFuture[]::new));
        endRunnables.clear();
        return ret;
    }

    public void markTexturesAsGenerated(ExistingFileHelper helper) {
        for (var texture : generatedTextures) {
            helper.trackGenerated(ResourceLocation.parse(texture), PackType.CLIENT_RESOURCES, "", "textures");
        }
    }
}
