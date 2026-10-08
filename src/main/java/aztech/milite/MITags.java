package aztech.milite;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class MITags {
    public static final TagKey<Item> WRENCHES = item("tools/wrench");

    public static final TagKey<Item> BARRELS = miItem("barrels");
    public static final TagKey<Item> TANKS = miItem("tanks");

    public static TagKey<Item> item(String path) {
        return TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("c", path));
    }

    public static TagKey<Item> miItem(String path) {
        return TagKey.create(BuiltInRegistries.ITEM.key(), MI.id(path));
    }

    public static TagKey<Block> miBlock(String path) {
        return TagKey.create(Registries.BLOCK, MI.id(path));
    }
}
