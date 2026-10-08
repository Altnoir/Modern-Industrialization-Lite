package aztech.milite.datagen.tag;

import aztech.milite.MIItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.*;

public class TagsToGenerate {
    private static final Map<TagKey<Item>, List<ItemLike>> tagToItemMap = new HashMap<>();
    static final Set<TagKey<Item>> optionalTags = new HashSet<>();
    public static final Map<String, String> tagTranslations = new HashMap<>();
    static final Map<String, Set<String>> tagToBeAddedToAnotherTag = new HashMap<>();

    static Map<TagKey<Item>, List<Item>> getTags() {
        var ret = HashMap.<TagKey<Item>, List<Item>>newHashMap(tagToItemMap.size());
        for (var entry : tagToItemMap.entrySet()) {
            var items = new ArrayList<Item>(entry.getValue().size());
            for (var item : entry.getValue()) {
                items.add(item.asItem());
            }
            items.sort((item1, item2) -> {
                var key1 = BuiltInRegistries.ITEM.getKey(item1.asItem());
                var key2 = BuiltInRegistries.ITEM.getKey(item2.asItem());
                var def1 = MIItem.ITEM_DEFINITIONS.get(key1);
                var def2 = MIItem.ITEM_DEFINITIONS.get(key2);

                if (def1 != null && def2 != null) {
                    return def1.sortOrder.compareTo(def2.sortOrder);
                } else {
                    return key1.compareTo(key2);
                }
            });
            ret.put(entry.getKey(), items);
        }
        return ret;
    }

    private static void addTranslation(String tag, String tagEnglishName) {
        var tagId = ResourceLocation.parse(tag);
        tagTranslations.put("tag.%s.%s".formatted(tagId.getNamespace(), tagId.getPath()).replace('/', '.'), tagEnglishName);
    }

    public static void generateTag(String tag, ItemLike item, String tagEnglishName) {
        if (tag.startsWith("#")) {
            throw new IllegalArgumentException("Tag must not start with #: " + tag);
        }
        generateTagNoTranslation(ItemTags.create(ResourceLocation.parse(tag)), item);
        addTranslation(tag, tagEnglishName);
    }

    public static void generateTagNoTranslation(TagKey<Item> tag, ItemLike item) {
        tagToItemMap.computeIfAbsent(tag, t -> new ArrayList<>()).add(item);
    }

    public static void addTagToTag(String tagTobeAdded, String tagTarget, String targetEnglishName) {
        if (tagTobeAdded.startsWith("#")) {
            throw new IllegalArgumentException("Tag must not start with #: " + tagTobeAdded);
        }
        if (tagTarget.startsWith("#")) {
            throw new IllegalArgumentException("Tag must not start with #: " + tagTarget);
        }

        tagToBeAddedToAnotherTag.computeIfAbsent(tagTarget, t -> new TreeSet<>()).add(tagTobeAdded);
        addTranslation(tagTarget, targetEnglishName);
    }

    public static void generateTag(TagKey<Item> tag, ItemLike item, String tagEnglishName) {
        generateTag(tag.location().toString(), item, tagEnglishName);
    }

    public static void markTagOptional(TagKey<Item> tag) {
        optionalTags.add(tag);
    }
}
