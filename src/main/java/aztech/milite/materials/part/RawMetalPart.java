package aztech.milite.materials.part;

import static aztech.milite.materials.part.MIParts.RAW_METAL_BLOCK;
import static aztech.milite.materials.part.MIParts.RAW_METAL;

import aztech.milite.items.SortOrder;
import aztech.milite.materials.set.MaterialRawSet;

import java.util.List;

public record RawMetalPart(boolean isBlock) implements PartKeyProvider {
    public PartTemplate of(MaterialRawSet set) {
        PartTemplate part = new PartTemplate(isBlock ? "Block of Raw %s" : "Raw %s", key());

        if (isBlock) {
            return part
                    .asBlock(SortOrder.RAW_ORE_BLOCKS, new TextureGenParams.RawMetal(true, set), 5, 6, 1)
                    .withCustomPath("raw_%s_block", "storage_blocks/raw_%s");
        } else {
            return part
                    .withTexture(new TextureGenParams.RawMetal(false, set))
                    .withCustomPath("raw_%s", "raw_materials/%s");
        }
    }

    public List<PartTemplate> ofAll(MaterialRawSet set) {
        return List.of(RAW_METAL.of(set), RAW_METAL_BLOCK.of(set));
    }

    @Override
    public PartKey key() {
        return new PartKey("raw_metal" + (isBlock ? "_block" : ""));
    }
}
