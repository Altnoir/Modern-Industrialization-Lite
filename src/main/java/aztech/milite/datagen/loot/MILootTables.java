package aztech.milite.datagen.loot;

import aztech.milite.MI;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public final class MILootTables {
    private MILootTables() {}

    public static final ResourceKey<LootTable> INDUSTRIALIST_GIFT = ResourceKey.create(Registries.LOOT_TABLE,
            MI.id("gameplay/hero_of_the_village/industrialist_gift"));
}
