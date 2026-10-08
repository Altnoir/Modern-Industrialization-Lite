package aztech.milite.materials.part;

import aztech.milite.items.SortOrder;
import aztech.milite.materials.set.MaterialBlockSet;

public class BlockPart implements PartKeyProvider {
    public PartTemplate of(MaterialBlockSet set) {
        return new PartTemplate("Block of %s", key())
                .withCustomPath("%s_block", "storage_blocks/%s")
                .asBlock(SortOrder.STORAGE_BLOCKS, new TextureGenParams.Block(set));
    }

    @Override
    public PartKey key() {
        return new PartKey("block");
    }
}
