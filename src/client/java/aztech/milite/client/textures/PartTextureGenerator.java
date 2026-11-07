package aztech.milite.client.textures;

import static aztech.milite.materials.property.MaterialProperty.SET;

import aztech.milite.client.textures.coloramp.Coloramp;
import aztech.milite.client.textures.coloramp.TabulatedColoramp;
import aztech.milite.materials.Material;
import aztech.milite.materials.part.MaterialItemPart;
import aztech.milite.materials.part.TextureGenParams;
import aztech.milite.materials.property.MaterialProperty;
import aztech.milite.materials.set.MaterialRawSet;
import aztech.milite.MI;
import net.minecraft.resources.ResourceLocation;

/**
 * All the per-part texture processing logic.
 * <p>
 * Two things are generated: raw metal ore blocks, which are a hand drawn shape over a stone background that depends on the
 * stone the ore generates in, and the block of a column shaped part (the quantum barrel), which is a column model built from a
 * shared template. Every other material part is a hand drawn texture in {@code src/main/resources/assets/modern_tech/textures}; add
 * the art there instead of adding a branch here, because a generated texture is written into the generated resources, which
 * {@code processResources} puts in front of the main tree.
 */
class PartTextureGenerator {
    static void processPart(TextureManager mtm, Material material, MaterialItemPart part) {
        var params = part.getTextureGenParams();
        if (params instanceof TextureGenParams.RawMetal(boolean isBlock, MaterialRawSet rawSet)) {
            processRawMetal(mtm, material, part, rawSet, isBlock);
        } else if (params instanceof TextureGenParams.ColumnBlock) {
            processColumn(mtm, material, part);
        }
    }

    /**
     * Raw ore blocks are generated because they are a hand drawn shape over a stone background that depends on the stone the
     * ore generates in, so they cannot be baked into a single static texture. The raw ore item is a hand drawn texture.
     */
    private static void processRawMetal(TextureManager mtm, Material material, MaterialItemPart part, MaterialRawSet rawSet,
            boolean isBlock) {
        if (!isBlock) {
            return;
        }

        // The raw block template is material set independent: it only depends on the stone the raw shape is drawn over.
        // The parent folder is part of the template name, see the javadoc of generateMaterialTexture.
        String template = "materialsets/raw/" + rawSet.name + "_block";
        String texturePath = "raw_" + material.name + "_block";
        MITextures.generateMaterialTexture(mtm, "common", template, texturePath, true, coloramp(mtm, material));
    }

    /**
     * A column block is a cube with a separate top and side texture. Only the parts that are not hand drawn reach this.
     */
    private static void processColumn(TextureManager mtm, Material material, MaterialItemPart part) {
        for (String suffix : new String[] { "side", "top" }) {
            MITextures.generateMaterialTexture(mtm, material.get(SET).name, part.key().key + "_" + suffix,
                    itemPath(part) + "_" + suffix, true, coloramp(mtm, material));
        }
    }

    private static String itemPath(MaterialItemPart part) {
        return ResourceLocation.parse(part.getItemId()).getPath();
    }

    private static Coloramp coloramp(TextureManager mtm, Material material) {
        var meanRgb = material.get(MaterialProperty.MEAN_RGB);
        if (meanRgb == 0) {
            MI.LOGGER.error("Missing mean RGB for material {}", material.name);
        }
        return new TabulatedColoramp(mtm, meanRgb, material.name);
    }
}
