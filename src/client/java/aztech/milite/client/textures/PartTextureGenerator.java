package aztech.milite.client.textures;

import static aztech.milite.materials.property.MaterialProperty.SET;

import aztech.milite.client.textures.coloramp.Coloramp;
import aztech.milite.client.textures.coloramp.DepletedColoramp;
import aztech.milite.client.textures.coloramp.HotIngotColoramp;
import aztech.milite.materials.Material;
import aztech.milite.materials.part.*;
import aztech.milite.materials.set.MaterialBlockSet;
import aztech.milite.materials.set.MaterialOreSet;
import aztech.milite.materials.set.MaterialRawSet;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.Objects;

/**
 * All the per-part texture processing logic.
 */
class PartTextureGenerator {
    static void processPart(Coloramp coloramp, TextureManager mtm, Material material, MaterialItemPart part) {
        var gen = new PartTextureGenerator(coloramp, mtm, material, part);

        try {
            gen.build(part);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final Coloramp coloramp;
    private final TextureManager mtm;
    private final Material material;
    private final String materialName;
    private final String itemPath;

    private PartTextureGenerator(Coloramp coloramp, TextureManager mtm, Material material, MaterialItemPart part) {
        this.coloramp = coloramp;
        this.mtm = mtm;
        this.material = material;
        this.materialName = material.name;
        this.itemPath = ResourceLocation.parse(part.getItemId()).getPath();
    }

    private void build(MaterialItemPart part) throws IOException {
        var params = part.getTextureGenParams();

        if (params instanceof TextureGenParams.Block(MaterialBlockSet blockSet)) {
            processBlock(blockSet);
        } else if (params instanceof TextureGenParams.CasingBlock) {
            processCasing(part);
        } else if (params instanceof TextureGenParams.ColumnBlock) {
            processColumn(part);
        } else if (params instanceof TextureGenParams.DepletedNuclear) {
            processDepletedNuclear();
        } else if (params instanceof TextureGenParams.DoubleIngot) {
            processDoubleIngot(part);
        } else if (params instanceof TextureGenParams.Gem) {
            processGem();
        } else if (params instanceof TextureGenParams.HotIngot) {
            processHotIngot();
        } else if (params instanceof TextureGenParams.Ore(ResourceLocation stoneType, MaterialOreSet oreSet)) {
            processOre(stoneType, oreSet);
        } else if (params instanceof TextureGenParams.RawMetal(boolean isBlock, MaterialRawSet rawSet)) {
            processRawMetal(isBlock, rawSet);
        } else if (params instanceof TextureGenParams.SimpleRecoloredBlock) {
            MITextures.generateItemPartTexture(mtm, part.key().key, material.get(SET).name, itemPath, true, coloramp);
        } else if (params instanceof TextureGenParams.SimpleRecoloredItem(PartKey basePart, String overlay)) {
            String partTemplate = Objects.requireNonNullElse(basePart, part).key().key;
            MITextures.generateItemPartTexture(mtm, partTemplate, overlay, material.get(SET).name, itemPath, false, coloramp);
        } else if (!(params instanceof TextureGenParams.NoTexture)) {
            throw new IllegalArgumentException("Unknown texture gen params class " + params.getClass().getSimpleName());
        }
    }

    private void processBlock(MaterialBlockSet set) throws IOException {
        if (mtm.hasTextureOverride(String.format("milite:textures/block/%s.png", itemPath))) {
            return;
        }

        String template = String.format("milite:textures/materialsets/blocks/%s.png", set.name);
        try (NativeImage image = mtm.getAssetAsTexture(template)) {
            TextureHelper.colorize(image, coloramp);
            String texturePath = String.format("milite:textures/block/%s.png", itemPath);
            mtm.addTexture(texturePath, image);
        }
    }

    private void processCasing(PartKeyProvider part) throws IOException {
        if (mtm.hasTextureOverride(String.format("milite:textures/block/%s.png", itemPath))) {
            return;
        }

        try (NativeImage image = MITextures.generateTexture(mtm, part.key().key, material.get(SET).name, coloramp)) {
            MITextures.appendTexture(mtm, image, itemPath, true);
        }
    }

    private void processColumn(PartKeyProvider part) {
        for (String suffix : new String[] { "side", "top" }) {
            MITextures.generateItemPartTexture(
                    mtm,
                    part.key().key + "_" + suffix,
                    material.get(SET).name,
                    itemPath + "_" + suffix,
                    true,
                    coloramp);
        }
    }

    private void processDepletedNuclear() {
        MITextures.generateItemPartTexture(mtm,
                NuclearFuelPart.Type.SIMPLE.key, "common", itemPath, false,
                new DepletedColoramp(coloramp));
    }

    private void processDoubleIngot(PartKeyProvider part) {
        mtm.runAtEnd(() -> {
            try {
                MITextures.generateDoubleIngot(mtm, materialName);
            } catch (Throwable throwable) {
                MITextures.logTextureGenerationError(throwable, materialName, material.get(SET).name, part.key().key);
            }
        });
    }

    private void processGem() throws IOException {
        if (mtm.hasTextureOverride(String.format("milite:textures/item/%s.png", itemPath))) {
            return;
        }

        String template = String.format("milite:textures/materialsets/gems/%s.png", materialName);
        try (NativeImage image = mtm.getAssetAsTexture(template)) {
            TextureHelper.colorize(image, coloramp);
            String texturePath = String.format("milite:textures/item/%s.png", itemPath);
            mtm.addTexture(texturePath, image);
        }
    }

    private void processHotIngot() {
        MITextures.generateItemPartTexture(mtm, MIParts.INGOT.key().key,
                material.get(SET).name, itemPath, false, new HotIngotColoramp(coloramp, 0.1, 0.5));
    }

    private void processOre(ResourceLocation stoneType, MaterialOreSet oreSet) throws IOException {
        if (mtm.hasTextureOverride(String.format("milite:textures/block/%s.png", itemPath))) {
            return;
        }

        String template = String.format("milite:textures/materialsets/ores/%s.png", oreSet.name);
        ResourceLocation from;
        boolean deepslate = stoneType.equals(OrePart.TYPE_DEEPSLATE);
        if (stoneType.equals(OrePart.TYPE_STONE) || deepslate) {
            from = ResourceLocation.withDefaultNamespace(switch (oreSet) {
                case IRON -> deepslate ? "deepslate_iron_ore" : "iron_ore";
                case COPPER -> deepslate ? "deepslate_copper_ore" : "copper_ore";
                case LAPIS -> deepslate ? "deepslate_lapis_ore" : "lapis_ore";
                case REDSTONE -> deepslate ? "deepslate" : "redstone_ore";
                case DIAMOND -> deepslate ? "deepslate" : "diamond_ore";
                case GOLD -> deepslate ? "deepslate_gold_ore" : "gold_ore";
                case EMERALD -> deepslate ? "deepslate_emerald_ore" : "emerald_ore";
                case COAL -> deepslate ? "deepslate_coal_ore" : "coal_ore";
                default -> deepslate ? "deepslate" : "stone";
            });
        } else {
            // Ore textures don't exist for any blocks other than stone or deepslate,
            // so if the source block is something else then just pull from the block's usual texture.
            from = stoneType;
        }

        try (NativeImage image = mtm.getAssetAsTexture(String.format("%s:textures/block/%s.png", from.getNamespace(), from.getPath()));
                NativeImage top = mtm.getAssetAsTexture(template)) {

            TextureHelper.colorize(top, coloramp);
            String texturePath = String.format("milite:textures/block/%s.png", itemPath);
            mtm.addTexture(texturePath, TextureHelper.blend(image, top), true);
        }
    }

    private void processRawMetal(boolean isBlock, MaterialRawSet rawSet) throws IOException {
        if (mtm.hasTextureOverride(String.format("milite:textures/%s/%s.png", isBlock ? "block" : "item", itemPath))) {
            return;
        }

        String template = String.format("milite:textures/materialsets/raw/%s.png", rawSet.name + (isBlock ? "_block" : ""));
        try (NativeImage image = mtm.getAssetAsTexture(template)) {
            TextureHelper.colorize(image, coloramp);
            String texturePath;
            if (isBlock) {
                texturePath = String.format("milite:textures/block/%s.png", itemPath);
            } else {
                texturePath = String.format("milite:textures/item/%s.png", itemPath);
            }
            mtm.addTexture(texturePath, image);
        }
    }
}
