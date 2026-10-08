package aztech.milite.materials.part;

import static aztech.milite.materials.property.MaterialProperty.MEAN_RGB;

import aztech.milite.blocks.storage.barrel.BarrelBlock;
import aztech.milite.blocks.storage.barrel.BarrelBlockEntity;
import aztech.milite.blocks.storage.barrel.BarrelItem;
import aztech.milite.blocks.storage.StorageBehaviour;
import aztech.milite.datagen.tag.TagsToGenerate;
import aztech.milite.definition.BlockDefinition;
import aztech.milite.items.ContainerItem;
import aztech.milite.items.SortOrder;
import aztech.milite.MIBlock;
import aztech.milite.MICapabilities;
import aztech.milite.MICommonProxy;
import aztech.milite.MIRegistries;
import aztech.milite.MITags;
import aztech.milite.thirdparty.fabrictransfer.api.bridge.SlotItemHandler;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.EntityBlock;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jspecify.annotations.Nullable;

public class BarrelPart implements PartKeyProvider {
    @Override
    public PartKey key() {
        return new PartKey("barrel");
    }

    public PartTemplate of(String nameOverride, String pathOverride, int stackCapacity) {
        return of(new PartEnglishNameFormatter.Overridden(nameOverride), (long) stackCapacity, pathOverride);
    }

    public PartTemplate of(long stackCapacity) {
        return of(new PartEnglishNameFormatter.Default("Barrel"), stackCapacity, null);
    }

    private PartTemplate of(PartEnglishNameFormatter englishNameFormatter, Long stackCapacity, @Nullable String maybeOverriddenPath) {
        MutableObject<BlockEntityType<BarrelBlockEntity>> bet = new MutableObject<>();

        PartTemplate template = new PartTemplate(englishNameFormatter, key()).asColumnBlock(SortOrder.BARRELS)
                .withRegister((partContext, part, itemPath, itemId, itemTag, englishName) -> {

                    StorageBehaviour<ItemVariant> barrelStorageBehaviour = BarrelBlock.withStackCapacity(stackCapacity);

                    EntityBlock factory = (pos, state) -> new BarrelBlockEntity(bet.getValue(), pos, state);

                    BlockDefinition<BarrelBlock> blockDefinition = MIBlock.block(
                            englishName,
                            itemPath,
                            MIBlock.BlockDefinitionParams.defaultStone()
                                    .withBlockConstructor(s -> new BarrelBlock(factory, barrelStorageBehaviour))
                                    .withBlockItemConstructor(BarrelItem::new)
                                    .withModel((block, gen) -> {
                                        String name = gen.name(block);
                                        gen.simpleBlock(block,
                                                gen.models().cubeColumn(name, gen.blockTexture(name + "_side"), gen.blockTexture(name + "_top")));
                                    })
                                    .withBlockEntityRendererItemModel()
                                    .noLootTable()
                                    .sortOrder(SortOrder.BARRELS.and(stackCapacity)));

                    TagsToGenerate.generateTag(MITags.BARRELS, blockDefinition, "Barrels");

                    MIRegistries.BLOCK_ENTITIES.register(itemPath, () -> {
                        var ret = BlockEntityType.Builder.of(factory::newBlockEntity, blockDefinition.asBlock()).build(null);
                        // noinspection unchecked,rawtypes
                        bet.setValue((BlockEntityType) ret);
                        return ret;
                    });

                    MICapabilities.onEvent(event -> {
                        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, bet.getValue(), (be, side) -> new SlotItemHandler(be));

                        var item = (BarrelItem) blockDefinition.asItem();
                        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ignored) -> stack.getCount() == 1 ? new ContainerItem.ItemHandler(stack, item) : null, item);
                    });

                    MICommonProxy.INSTANCE.registerPartBarrelClient(bet::getValue, partContext.get(MEAN_RGB));
                });
        if (maybeOverriddenPath != null) {
            template = template.withCustomPath(maybeOverriddenPath);
        }
        return template;
    }
}
