package aztech.milite.client.textures;

import aztech.milite.client.textures.coloramp.Coloramp;
import aztech.milite.client.textures.coloramp.TabulatedColoramp;
import aztech.milite.definition.FluidDefinition;
import aztech.milite.machines.models.MachineCasing;
import aztech.milite.machines.models.MachineCasings;
import aztech.milite.materials.Material;
import aztech.milite.materials.MaterialRegistry;
import aztech.milite.materials.part.MaterialItemPart;
import aztech.milite.materials.property.MaterialProperty;
import aztech.milite.MI;
import aztech.milite.MIFluids;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.Util;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.List;

public final class MITextures {
    public static CompletableFuture<?> offerTextures(BiConsumer<NativeImage, String> textureWriter, BiConsumer<JsonElement, String> mcMetaWriter,
            ResourceProvider manager, ExistingFileHelper fileHelper) {
        TextureManager mtm = new TextureManager(manager, textureWriter, mcMetaWriter);

        // Texture generation runs in two phases:
        // 1. all textures that don't depend on other generated textures are submitted to {@code defer.accept} and generated in parallel.
        // 2. textures that depend on other generated textures are submitted to {@code mtm.runAtEnd} are generated in parallel once phase 1. is
        // complete.

        // Futures for the first work phase
        List<CompletableFuture<?>> futures = new ArrayList<>();
        Consumer<IORunnable> defer = r -> futures.add(CompletableFuture.runAsync(r::safeRun, Util.backgroundExecutor()));

        // Material parts are hand drawn textures, they live in src/main/resources/assets/milite/textures. Only the parts that
        // cannot be baked into a static texture are generated, which is why this visits every part but almost all of them are
        // ignored by PartTextureGenerator.
        for (Material material : MaterialRegistry.getMaterials().values()) {
            for (MaterialItemPart part : material.getParts().values()) {
                defer.accept(() -> PartTextureGenerator.processPart(mtm, material, part));
            }
        }

        for (FluidDefinition fluid : MIFluids.FLUID_DEFINITIONS.values()) {
            defer.accept(() -> registerFluidTextures(mtm, fluid));
        }

        mtm.runAtEnd(() -> casingFromTextureBricked(mtm, MachineCasings.BRICKED_BRONZE,
                MI.id("textures/block/bronze_machine_casing.png")));
        mtm.runAtEnd(() -> casingFromTextureBricked(mtm, MachineCasings.BRICKED_STEEL,
                MI.id("textures/block/steel_machine_casing.png")));

        mtm.runAtEnd(() -> {
            if (!mtm.hasAsset("milite:textures/item/mixed_ingot_blastproof.png")) {
                mtm.addTexture("milite:textures/item/mixed_ingot_blastproof.png",
                        TextureHelper.tripleTexture(mtm.getAssetAsTexture("milite:textures/item/stainless_steel_ingot.png"),
                                mtm.getAssetAsTexture("milite:textures/item/titanium_ingot.png"),
                                mtm.getAssetAsTexture("milite:textures/item/tungsten_ingot.png")));
            }
        });

        mtm.runAtEnd(() -> {
            if (!mtm.hasAsset("milite:textures/item/mixed_plate_nuclear.png")) {
                mtm.addTexture("milite:textures/item/mixed_plate_nuclear.png",
                        TextureHelper.tripleTexture(mtm.getAssetAsTexture("milite:textures/item/cadmium_plate.png"),
                                mtm.getAssetAsTexture("milite:textures/item/beryllium_plate.png"),
                                mtm.getAssetAsTexture("milite:textures/item/blastproof_alloy_plate.png"), 1, 2));
            }
        });

        mtm.runAtEnd(() -> {
            if (!mtm.hasAsset("milite:textures/item/mixed_ingot_iridium.png")) {
                mtm.addTexture("milite:textures/item/mixed_ingot_iridium.png",
                        TextureHelper.tripleTexture(mtm.getAssetAsTexture("milite:textures/item/blastproof_alloy_ingot.png"),
                                mtm.getAssetAsTexture("milite:textures/item/iridium_ingot.png"),
                                mtm.getAssetAsTexture("milite:textures/item/blastproof_alloy_ingot.png")));
            }
        });

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenComposeAsync(v -> {
                    // Do second phase work
                    return mtm.doEndWork();
                }, Util.backgroundExecutor())
                .thenRun(() -> {
                    mtm.markTexturesAsGenerated(fileHelper);
                })
                .thenRun(() -> MI.LOGGER.info("I used the png to destroy the png."));
    }

    /**
     * Recolors a material set template with the material's coloramp and writes it into the generated resources.
     * <p>
     * Nothing is generated for a texture that already exists in the main tree: those are hand drawn, and the generated
     * resources are put in front of them, so writing one would replace the art.
     *
     * @param materialSet the material set whose template folder is searched first, {@code common} is the fallback
     * @param part        the template name, a {@code folder/name} pair for the templates that live in a subfolder
     */
    public static void generateMaterialTexture(TextureManager mtm, String materialSet, String part, String path, boolean isBlock,
            Coloramp coloramp) {
        String outputPath = String.format("milite:textures/%s/%s.png", isBlock ? "block" : "item", path);
        if (mtm.hasAsset(outputPath)) {
            return;
        }

        try {
            NativeImage texture = generateTexture(mtm, materialSet, part, coloramp);
            if (texture == null) {
                throw new IOException("Could not find any texture for " + part);
            }
            mtm.addTexture(outputPath, texture);
            texture.close();
        } catch (Throwable throwable) {
            MI.LOGGER.warn(String.format("Failed to generate texture for path %s, material set %s, partTemplate %s", path, materialSet,
                    part), throwable);
        }
    }

    private static NativeImage generateTexture(TextureManager mtm, String materialSet, String part, Coloramp coloramp)
            throws IOException {
        NativeImage image = null;
        boolean explicitPath = part.startsWith("materialsets/");
        for (String layer : LAYERS) {
            String template = getTemplate(materialSet, explicitPath, part, layer);
            if (!mtm.hasAsset(template)) {
                template = getTemplate("common", explicitPath, part, layer);
            }

            if (mtm.hasAsset(template)) {
                if (image == null) {
                    image = mtm.getAssetAsTexture(template);
                    colorizeLayer(image, layer, coloramp);
                } else {
                    NativeImage topLayer = mtm.getAssetAsTexture(template);
                    colorizeLayer(topLayer, layer, coloramp);
                    NativeImage oldImage = image;
                    image = TextureHelper.blend(oldImage, topLayer);
                    oldImage.close();
                    topLayer.close();
                }
            }
        }
        return image;
    }

    private static String getTemplate(String materialSet, boolean explicitPath, String part, String suffix) {
        String template = explicitPath ? part : "materialsets/" + materialSet + "/" + part;
        return String.format("milite:textures/%s%s.png", template, suffix);
    }

    private static final String[] LAYERS = new String[] { "_underlay", "", "_overlay" };

    /**
     * Colorize layer if necessary.
     */
    private static void colorizeLayer(NativeImage image, String layer, Coloramp coloramp) {
        if (layer.equals("")) {
            TextureHelper.colorize(image, coloramp);
        }
    }

    private static void casingFromTextureBricked(TextureManager tm, MachineCasing casing, ResourceLocation topTexturePath) {
        String outputPath = String.format("%s:textures/block/casings/%s.png", casing.key.getNamespace(), casing.key.getPath());
        if (tm.hasAsset(outputPath)) {
            return;
        }

        try (
                var topTexture = tm.getAssetAsTexture(topTexturePath.toString());
                var brickTexture = tm.getAssetAsTexture("milite:textures/block/fire_clay_bricks.png")) {
            if (topTexture.getWidth() != brickTexture.getWidth() || topTexture.getHeight() != brickTexture.getHeight()) {
                throw new IllegalArgumentException("Texture and Brick must have same dimension");
            }
            // Copy image
            try (NativeImage copy = TextureHelper.copy(topTexture)) {
                for (int i = 0; i < copy.getWidth(); ++i) {
                    for (int j = copy.getHeight() / 2; j < copy.getHeight(); j++) {
                        copy.setPixelRGBA(i, j, brickTexture.getPixelRGBA(i, j));
                    }
                }
                String s = String.format("%s:textures/block/casings/%s.png", casing.key.getNamespace(), casing.key.getPath());
                tm.addTexture(s, copy);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate bricked casing texture", e);
        }
    }

    private static void registerFluidTextures(TextureManager tm, FluidDefinition fluid) {
        String path = "milite:textures/fluid/";
        String bucket = path + "bucket.png";
        String bucket_content = path + "bucket_content.png";

        Coloramp fluidColoramp = new TabulatedColoramp(fluid.color);

        // Bucket
        if (!tm.hasAsset(String.format("milite:textures/item/%s_bucket.png", fluid.path()))) {
            try {
                NativeImage bucket_image = tm.getAssetAsTexture(bucket);
                NativeImage bucket_content_image = tm.getAssetAsTexture(bucket_content);
                TextureHelper.colorize(bucket_content_image, fluidColoramp);
                NativeImage oldBucketImage = bucket_image;
                bucket_image = TextureHelper.blend(oldBucketImage, bucket_content_image);
                oldBucketImage.close();
                if (fluid.isGas) {
                    TextureHelper.flip(bucket_image);
                }
                tm.addTexture(String.format("milite:textures/item/%s_bucket.png", fluid.path()), bucket_image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Flowing Textures

        String pathFluid = path + String.format("template/%s.png", fluid.fluidTexture.path);

        if (!tm.hasAsset(String.format("milite:textures/fluid/%s_still.png", fluid.path()))) {
            try {
                NativeImage fluidAnim = tm.getAssetAsTexture(pathFluid);
                TextureHelper.colorize(fluidAnim, fluidColoramp);
                TextureHelper.setAlpha(fluidAnim, fluid.opacity);
                tm.addTexture(String.format("milite:textures/fluid/%s_still.png", fluid.path()), fluidAnim, true);
                tm.addMcMeta(String.format("milite:textures/fluid/%s_still.png.mcmeta", fluid.path()), fluid.fluidTexture.mcMetaInfo);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
