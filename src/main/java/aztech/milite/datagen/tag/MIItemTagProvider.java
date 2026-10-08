package aztech.milite.datagen.tag;

import aztech.milite.machines.blockentities.ReplicatorMachineBlockEntity;
import aztech.milite.materials.MIMaterials;
import aztech.milite.MI;
import aztech.milite.MIItem;
import aztech.milite.MITags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.Tags;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.Objects;

public class MIItemTagProvider extends ItemTagsProvider {
    private final boolean runtimeDatagen;

    public MIItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper, boolean runtimeDatagen) {
        super(output, lookupProvider, CompletableFuture.completedFuture(TagLookup.empty()), MI.ID, existingFileHelper);
        this.runtimeDatagen = runtimeDatagen;
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (var entry : TagsToGenerate.getTags().entrySet()) {
            boolean optional = TagsToGenerate.optionalTags.contains(entry.getKey());
            for (var item : entry.getValue()) {
                if (optional) {
                    tag(entry.getKey()).addOptional(BuiltInRegistries.ITEM.getKey(item));
                } else {
                    tag(entry.getKey()).add(item);
                }
            }
        }

        for (var entry : TagsToGenerate.tagToBeAddedToAnotherTag.entrySet()) {
            var tagId = ResourceLocation.parse(entry.getKey());
            for (var tag : entry.getValue()) {
                tag(key(tagId)).addTag(key(tag));
            }
        }

        for (var goldenItem : MIMaterials.GOLD.getParts().values()) {
            String namespace = goldenItem.getItemId().split(":")[0];
            if (!Objects.equals(namespace, "minecraft")) {
                tag(ItemTags.PIGLIN_LOVED).add(goldenItem.asItem());
            }
        }

        tag(ReplicatorMachineBlockEntity.BLACKLISTED)
                .add(Items.BUNDLE, MIItem.PORTABLE_STORAGE_UNIT.asItem())
                .addTag(MITags.BARRELS);

        tag(Tags.Items.TOOLS_SHEAR).add(MIItem.DIESEL_CHAINSAW.asItem());
        tag(MITags.WRENCHES).add(MIItem.WRENCH.asItem());
        tag(ItemTags.AXES).add(MIItem.DIESEL_CHAINSAW.asItem());
        tag(ItemTags.HOES).add(MIItem.DIESEL_CHAINSAW.asItem());
        tag(ItemTags.PICKAXES).add(MIItem.STEAM_MINING_DRILL.asItem(), MIItem.DIESEL_MINING_DRILL.asItem());
        tag(ItemTags.SHOVELS).add(MIItem.STEAM_MINING_DRILL.asItem(), MIItem.DIESEL_MINING_DRILL.asItem());
        tag(ItemTags.SWORDS).add(MIItem.DIESEL_CHAINSAW.asItem(), MIItem.QUANTUM_SWORD.asItem());
        tag(Tags.Items.MELEE_WEAPON_TOOLS).add(MIItem.DIESEL_CHAINSAW.asItem(), MIItem.QUANTUM_SWORD.asItem());
        tag(ItemTags.DURABILITY_ENCHANTABLE).add(MIItem.IRON_HAMMER.asItem(), MIItem.STEEL_HAMMER.asItem(), MIItem.DIAMOND_HAMMER.asItem(),
                MIItem.NETHERITE_HAMMER.asItem());

        tag(ItemTags.HEAD_ARMOR).add(MIItem.RUBBER_HELMET.asItem(), MIItem.QUANTUM_HELMET.asItem());
        tag(ItemTags.CHEST_ARMOR).add(MIItem.QUANTUM_CHESTPLATE.asItem());
        tag(ItemTags.LEG_ARMOR).add(MIItem.QUANTUM_LEGGINGS.asItem());
        tag(ItemTags.FOOT_ARMOR).add(MIItem.RUBBER_BOOTS.asItem(), MIItem.QUANTUM_BOOTS.asItem());

        tag(ItemTags.COALS).add(ResourceKey.create(Registries.ITEM, MI.id("lignite_coal")));
    }

    private static TagKey<Item> key(ResourceLocation id) {
        return TagKey.create(BuiltInRegistries.ITEM.key(), id);
    }

    private static TagKey<Item> key(String id) {
        return key(ResourceLocation.parse(id));
    }
}
