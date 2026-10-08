package aztech.milite.util;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class TagHelper {
    public static List<TagKey<Block>> getMiningLevelTag(int level) {
        return List.of(switch (level) {
            case 1 -> BlockTags.NEEDS_STONE_TOOL;
            case 2 -> BlockTags.NEEDS_IRON_TOOL;
            case 3 -> BlockTags.NEEDS_DIAMOND_TOOL;
            default -> throw new IllegalArgumentException();
        }, BlockTags.MINEABLE_WITH_PICKAXE);
    }
}
