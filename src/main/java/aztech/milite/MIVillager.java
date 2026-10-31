package aztech.milite;

import aztech.milite.config.MIStartupConfig;
import aztech.milite.materials.MIMaterials;
import aztech.milite.materials.part.MIParts;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

public class MIVillager {
    public static void init(VillagerTradesEvent event) {
        if (event.getType() != MIRegistries.INDUSTRIALIST.value()) {
            return;
        }

        if (!MIStartupConfig.INSTANCE.defaultIndustrialistTrades.getAsBoolean())
            return;

        var level1 = event.getTrades().get(1);
        level1.add(sellItemsToVillager(MIMaterials.LIGNITE_COAL.getPart(MIParts.GEM).asItem(), 15, 16, 2));
        level1.add(buyItemsFromVillager(MIMaterials.FIRE_CLAY.getPart(MIParts.INGOT), 2, 6, 2));
        level1.add(buyItemsFromVillager(Items.COPPER_INGOT, 4, 8, 5));
        level1.add(buyItemsFromVillager(MIMaterials.TIN.getPart(MIParts.INGOT), 4, 3, 5));

        var level2 = event.getTrades().get(2);
        level2.add(buyItemsFromVillager(MIMaterials.COPPER.getPart(MIParts.GEAR), 4, 1, 5));
        level2.add(buyItemsFromVillager(MIMaterials.COPPER.getPart(MIParts.ROTOR), 4, 1, 5));
        level2.add(buyItemsFromVillager(MIMaterials.BRONZE.getPart(MIParts.INGOT), 4, 3, 2));
        level2.add(buyItemsFromVillager(MIItem.RUBBER_SHEET, 1, 6, 2));
        level2.add(sellItemsToVillager(MIMaterials.SULFUR.getPart(MIParts.DUST), 4, 16, 2));

        var level3 = event.getTrades().get(3);
        level3.add(buyItemsFromVillager(MIMaterials.BRONZE.getPart(MIParts.GEAR), 4, 1, 5));
        level3.add(buyItemsFromVillager(MIMaterials.BRONZE.getPart(MIParts.ROTOR), 4, 1, 5));
        level3.add(buyItemsFromVillager(MIMaterials.STEEL.getPart(MIParts.INGOT), 6, 3, 10));

        var level4 = event.getTrades().get(4);
        level4.add(buyItemsFromVillager(MIMaterials.STEEL.getPart(MIParts.GEAR), 5, 1, 5));
        level4.add(buyItemsFromVillager(MIMaterials.STEEL.getPart(MIParts.PLATE), 6, 3, 10));

        var level5 = event.getTrades().get(5);
        level5.add(buyItemsFromVillager(MIItem.ANALOG_CIRCUIT, 12, 1, 20));
        level5.add(buyItemsFromVillager(MIItem.MOTOR, 8, 2, 10));
        level5.add(buyItemsFromVillager(MIMaterials.BRONZE.getPart(MIParts.DRILL), 18, 4, 20));
    }

    private static VillagerTrades.ItemListing sellItemsToVillager(ItemLike soldItem, int numberOfItems, int maxUses, int xp) {
        return new VillagerTrades.EmeraldForItems(soldItem, numberOfItems, maxUses, xp);
    }

    private static VillagerTrades.ItemListing buyItemsFromVillager(ItemLike boughtItem, int emeraldCost, int numberOfItems, int xp) {
        return new VillagerTrades.ItemsForEmeralds(boughtItem.asItem(), emeraldCost, numberOfItems, xp);
    }
}
