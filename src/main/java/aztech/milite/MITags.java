package aztech.milite;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

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
}
