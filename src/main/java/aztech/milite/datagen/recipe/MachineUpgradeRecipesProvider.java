package aztech.milite.datagen.recipe;

import aztech.milite.MI;
import aztech.milite.MIBlock;
import aztech.milite.MIBlockKeys;
import aztech.milite.machines.MachineBlock;
import aztech.milite.machines.init.MachineLevel;
import aztech.milite.recipe.json.ShapedRecipeJson;
import aztech.milite.recipe.json.ShapelessRecipeBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public class MachineUpgradeRecipesProvider extends MIRecipesProvider {
    private static final List<MachineLevel> UPGRADE_LADDER = List.of(
            MachineLevel.BRONZE,
            MachineLevel.STEEL,
            MachineLevel.ELECTRIC,
            MachineLevel.ALUMINUM,
            MachineLevel.STAINLESS_STEEL,
            MachineLevel.TITANIUM,
            MachineLevel.IRIDIUM);

    public MachineUpgradeRecipesProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        ItemLike steelCasing = itemOf("steel_machine_casing");
        ItemLike steelHull = itemOf("steel_machine_hull");

        new ShapedRecipeJson(idOf(steelHull), 2, "G", "S", "F")
                .addInput('G', "#c:gears/bronze")
                .addInput('S', idOf(steelCasing))
                .addInput('F', idOf(MIBlock.BLOCK_FIRE_CLAY_BRICKS))
                .offerTo(consumer, "machine_upgrade/steel_machine_hull");

        for (var entry : MIBlock.BLOCK_DEFINITIONS.entrySet()) {
            if (!(entry.getValue().asBlock() instanceof MachineBlock)) {
                continue;
            }

            String path = entry.getKey().getPath();
            MachineLevel level = levelOf(path);
            if (level == null) {
                continue;
            }

            MachineLevel target = nextLevel(level);
            if (target == null) {
                continue;
            }

            addUpgrade(consumer, entry.getValue(), path.substring(level.getSerializedName().length() + 1), target, hullOf(target));
        }
    }

    private static void addUpgrade(RecipeOutput consumer, ItemLike machine, String machineName, MachineLevel targetLevel, ItemLike hull) {
        // Lithography has its own upgrade path using the previous processor, before the target hull is available.
        if (machineName.equals("lithography")) {
            return;
        }
        ItemLike target = MIBlock.BLOCK_DEFINITIONS.get(MI.id(targetLevel.getSerializedName() + "_" + machineName));
        if (target == null) {
            return;
        }

        ShapelessRecipeBuilder.shapeless(target, 1).requires(hull).requires(machine)
                .offerTo(consumer, "machine_upgrade/" + targetLevel.getSerializedName() + "/" + machineName);
    }

    private static MachineLevel levelOf(String path) {
        MachineLevel match = null;
        for (MachineLevel level : UPGRADE_LADDER) {
            String prefix = level.getSerializedName() + "_";
            if (path.startsWith(prefix) && (match == null || prefix.length() > match.getSerializedName().length() + 1)) {
                match = level;
            }
        }

        return match;
    }

    private static MachineLevel nextLevel(MachineLevel level) {
        int index = UPGRADE_LADDER.indexOf(level);
        if (index < 0 || index + 1 >= UPGRADE_LADDER.size()) {
            return null;
        }

        return UPGRADE_LADDER.get(index + 1);
    }

    private static ItemLike hullOf(MachineLevel level) {
        return switch (level) {
            case STEEL -> itemOf("steel_machine_hull");
            case ELECTRIC -> itemOf(MIBlockKeys.ELECTRIC_MACHINE_HULL.location().getPath());
            case ALUMINUM -> itemOf(MIBlockKeys.ALUMINUM_MACHINE_HULL.location().getPath());
            case STAINLESS_STEEL -> itemOf(MIBlockKeys.STAINLESS_STEEL_MACHINE_HULL.location().getPath());
            case TITANIUM -> itemOf(MIBlockKeys.TITANIUM_MACHINE_HULL.location().getPath());
            case IRIDIUM -> itemOf(MIBlockKeys.IRIDIUM_MACHINE_HULL.location().getPath());
            default -> throw new IllegalStateException("There is no machine hull for the " + level.getSerializedName() + " level");
        };
    }

    private static ItemLike itemOf(String path) {
        Item item = BuiltInRegistries.ITEM.get(MI.id(path));
        if (item == Items.AIR) {
            throw new IllegalStateException("The machine upgrade recipes refer to " + MI.id(path) + ", which is not a registered item");
        }

        return item;
    }

    private static String idOf(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).toString();
    }
}
