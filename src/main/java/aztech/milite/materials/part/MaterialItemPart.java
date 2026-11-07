package aztech.milite.materials.part;

import aztech.milite.items.SortOrder;
import aztech.milite.materials.MaterialBuilder;
import aztech.milite.materials.MaterialProgression;
import aztech.milite.MIItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.ItemLike;

public sealed interface MaterialItemPart extends PartKeyProvider, ItemLike permits MaterialItemPartImpl {
    /**
     * External parts are already registered and already have a texture,
     * but they're in the material system for recipe generation.
     */
    static MaterialItemPart external(PartKey key, String taggedItemId, String itemId) {
        return new MaterialItemPartImpl(key, taggedItemId, itemId, ctx -> {}, new TextureGenParams.NoTexture(), false);
    }

    static MaterialItemPart external(PartKeyProvider part, String taggedItemId, String itemId) {
        return external(part.key(), taggedItemId, itemId);
    }

    /**
     * External parts are already registered and already have a texture,
     * but they're in the material system for recipe generation.
     */
    static MaterialItemPart external(PartKeyProvider part, String itemId) {
        return external(part, itemId, itemId);
    }

    /**
     * Simple item parts are just regular MIItems.
     */
    static MaterialItemPart simpleItem(PartKeyProvider part, String englishName, String itemPath) {
        String itemId = "modern_tech:" + itemPath;

        return new MaterialItemPartImpl(part.key(), itemId, itemId, ctx -> {
            MIItem.item(englishName, itemPath, SortOrder.MATERIALS
                    .and(MaterialProgression.rankOf(ctx.getMaterialName()))
                    .and(PartProgression.rankOf(part.key())));
        }, new TextureGenParams.NoTexture(), true);
    }

    /**
     * @return The common tag of this material prefixed by # if available, or the id
     *         otherwise.
     */
    String getTaggedItemId();

    default Ingredient getTaggedIngredient() {
        var taggedItem = getTaggedItemId();
        if (taggedItem.startsWith("#")) {
            return Ingredient.of(ItemTags.create(ResourceLocation.parse(taggedItem.substring(1))));
        } else {
            return Ingredient.of(asItem());
        }
    }

    /**
     * @return The full id of this part. Includes the namespace and the path, separated by :.
     */
    String getItemId();

    @Override
    default Item asItem() {
        return BuiltInRegistries.ITEM.getOrThrow(ResourceKey.create(Registries.ITEM, ResourceLocation.parse(getItemId())));
    }

    default Block asBlock() {
        return BuiltInRegistries.BLOCK.getOrThrow(ResourceKey.create(Registries.BLOCK, ResourceLocation.parse(getItemId())));
    }

    /**
     * Perform any required registration.
     */
    void register(MaterialBuilder.PartContext partContext);

    /**
     * Return texture generation parameters. Return {@link TextureGenParams.NoTexture} if no texture is required.
     */
    TextureGenParams getTextureGenParams();

    boolean isInternal();
}
