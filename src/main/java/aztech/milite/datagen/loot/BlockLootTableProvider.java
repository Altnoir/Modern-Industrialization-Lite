package aztech.milite.datagen.loot;

import aztech.milite.definition.BlockDefinition;
import aztech.milite.MIBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class BlockLootTableProvider extends BlockLootSubProvider {
    public BlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.VANILLA_SET, registries);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return MIBlock.BLOCK_DEFINITIONS.values()
                .stream()
                .filter(x -> x.blockLoot != null)
                .<Block>map(BlockDefinition::asBlock)
                .toList();
    }

    @Override
    public void generate() {
        for (BlockDefinition<?> blockDefinition : MIBlock.BLOCK_DEFINITIONS.values()) {
            if (blockDefinition.blockLoot == null) {
                continue;
            }

            var block = blockDefinition.asBlock();
            if (blockDefinition.blockLoot instanceof MIBlockLoot.DropSelf) {
                dropSelf(block);
            } else if (blockDefinition.blockLoot instanceof MIBlockLoot.Ore(String loot)) {
                add(block, createOreDrop(block, BuiltInRegistries.ITEM.get(ResourceLocation.parse(loot))));
            }
        }
    }
}
