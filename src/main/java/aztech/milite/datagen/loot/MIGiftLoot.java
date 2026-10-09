package aztech.milite.datagen.loot;

import aztech.milite.materials.MIMaterials;
import aztech.milite.materials.part.MIParts;
import aztech.milite.MIBlock;
import aztech.milite.MIItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

public record MIGiftLoot(HolderLookup.Provider registries) implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(
                MILootTables.INDUSTRIALIST_GIFT,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(MIItem.STEEL_UPGRADE))
                                        .add(LootItem.lootTableItem(MIItem.PISTON))
                                        .add(LootItem.lootTableItem(MIItem.ROBOT_ARM))
                                        .add(LootItem.lootTableItem(MIBlock.ELECTRIC_MACHINE_HULL))
                                        .add(LootItem.lootTableItem(MIMaterials.BRONZE.getPart(MIParts.DRILL)))
                                        .add(LootItem.lootTableItem(MIMaterials.STEEL.getPart(MIParts.DRILL)))));
    }
}
