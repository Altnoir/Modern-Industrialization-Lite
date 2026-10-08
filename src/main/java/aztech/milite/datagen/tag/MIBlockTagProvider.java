package aztech.milite.datagen.tag;

import aztech.milite.definition.BlockDefinition;
import aztech.milite.MI;
import aztech.milite.MIBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MIBlockTagProvider extends BlockTagsProvider {
    public MIBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MI.ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (BlockDefinition<?> definition : MIBlock.BLOCK_DEFINITIONS.values()) {
            for (var tag : definition.tags) {
                tag(tag).add(definition.asBlock());
            }
        }

        for (var entry : TagsToGenerate.getTags().entrySet()) {
            boolean optional = TagsToGenerate.optionalTags.contains(entry.getKey());
            for (var item : entry.getValue()) {
                var itemKey = BuiltInRegistries.ITEM.getKey(item);
                var block = BuiltInRegistries.BLOCK.getOptional(itemKey);
                if (block.isEmpty()) {
                    continue;
                }
                var key = BlockTags.create(entry.getKey().location());
                if (optional) {
                    tag(key).addOptional(itemKey);
                } else {
                    tag(key).add(block.get());
                }
            }
        }

        for (var entry : TagsToGenerate.tagToBeAddedToAnotherTag.entrySet()) {
            var tagId = ResourceLocation.parse(entry.getKey());
            for (var tag : entry.getValue()) {
                if (this.builders.containsKey(ResourceLocation.parse(tag))) {
                    tag(key(tagId)).addTag(key(tag));
                }
            }
        }
    }

    private static TagKey<Block> key(ResourceLocation id) {
        return TagKey.create(BuiltInRegistries.BLOCK.key(), id);
    }

    private static TagKey<Block> key(String id) {
        return key(ResourceLocation.parse(id));
    }
}
