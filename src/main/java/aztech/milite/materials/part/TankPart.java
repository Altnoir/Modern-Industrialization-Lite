package aztech.milite.materials.part;

import static aztech.milite.materials.property.MaterialProperty.MEAN_RGB;

import aztech.milite.blocks.storage.StorageBehaviour;
import aztech.milite.blocks.storage.tank.*;
import aztech.milite.datagen.model.BaseModelProvider;
import aztech.milite.datagen.tag.TagsToGenerate;
import aztech.milite.definition.BlockDefinition;
import aztech.milite.items.ContainerItem;
import aztech.milite.items.SortOrder;
import aztech.milite.MIBlock;
import aztech.milite.MICapabilities;
import aztech.milite.MICommonProxy;
import aztech.milite.MIRegistries;
import aztech.milite.MITags;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.EntityBlock;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidType;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

public class TankPart implements PartKeyProvider {
    public static final BiConsumer<Block, BaseModelProvider> MODEL_GENERATOR = (block, gen) -> {
        gen.simpleBlock(block, gen.models()
                .getBuilder(gen.blockTexture(block).getPath())
                .parent(gen.models().getExistingFile(gen.modLoc("base/tank")))
                .texture("0", gen.blockTexture(block).toString()));
    };

    @Override
    public PartKey key() {
        return new PartKey("tank");
    }

    public PartTemplate of(String nameOverride, String path, int bucketCapacity) {
        return of(new PartEnglishNameFormatter.Overridden(nameOverride), bucketCapacity, path);
    }

    public PartTemplate of(long bucketCapacity) {
        return of(new PartEnglishNameFormatter.Default("Tank"), bucketCapacity, null);
    }

    public PartTemplate of(PartEnglishNameFormatter englishNameFormatter, long bucketCapacity, @Nullable String maybePathOverridden) {
        MutableObject<BlockEntityType<TankBlockEntity>> bet = new MutableObject<>();
        long capacity = FluidType.BUCKET_VOLUME * bucketCapacity;

        PartTemplate tank = new PartTemplate(englishNameFormatter, key())
                .asBlock(SortOrder.TANKS, new TextureGenParams.SimpleRecoloredBlock())
                .withRegister((partContext, part, itemPath, itemId, itemTag, englishName) -> {

                    StorageBehaviour<FluidVariant> tankStorageBehaviour = StorageBehaviour.uniformQuantity(capacity);

                    EntityBlock factory = (pos, state) -> new TankBlockEntity(bet.getValue(), pos, state);

                    BlockDefinition<TankBlock> blockDefinition = MIBlock.block(
                            englishName,
                            itemPath,
                            MIBlock.BlockDefinitionParams.defaultStone()
                                    .withBlockConstructor(s -> new TankBlock(factory, tankStorageBehaviour))
                                    .withBlockItemConstructor(TankItem::new)
                                    .withModel(MODEL_GENERATOR)
                                    .withBlockEntityRendererItemModel()
                                    .noLootTable()
                                    .sortOrder(SortOrder.TANKS.and(bucketCapacity)));

                    TagsToGenerate.generateTag(MITags.TANKS, blockDefinition, "Tanks");

                    MIRegistries.BLOCK_ENTITIES.register(itemPath, () -> {
                        var ret = BlockEntityType.Builder.of(factory::newBlockEntity, blockDefinition.asBlock()).build(null);
                        // noinspection unchecked,rawtypes
                        bet.setValue((BlockEntityType) ret);
                        return ret;
                    });

                    MICapabilities.onEvent(event -> {
                        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, bet.getValue(), (be, side) -> be.fluidHandler);

                        var item = (TankItem) blockDefinition.asItem();
                        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ignored) -> new ContainerItem.FluidHandler(stack, item), item);
                    });

                    MICommonProxy.INSTANCE.registerPartTankClient(bet::getValue, partContext.get(MEAN_RGB));
                });

        if (maybePathOverridden != null) {
            tank = tank.withCustomPath(maybePathOverridden);
        }

        return tank;
    }
}
