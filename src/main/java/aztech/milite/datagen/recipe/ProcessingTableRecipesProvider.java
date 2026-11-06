package aztech.milite.datagen.recipe;

import aztech.milite.MI;
import aztech.milite.MIBlock;
import aztech.milite.MIBlockKeys;
import aztech.milite.MIItem;
import aztech.milite.blocks.processingtable.ProcessingTableRecipe;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.materials.MIMaterials;
import aztech.milite.materials.Material;
import aztech.milite.materials.part.MIParts;
import aztech.milite.materials.part.PartKeyProvider;
import aztech.milite.recipe.json.ShapedRecipeJson;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ProcessingTableRecipesProvider extends MIRecipesProvider {
    private static final int[][] CELL_PATTERNS = {
            {},
            {5},
            {4, 6},
            {4, 5, 6},
            {2, 4, 6, 8},
            {2, 4, 5, 6, 8},
            {1, 3, 7, 9, 4, 6},
            {1, 3, 4, 5, 6, 7, 9},
            {1, 3, 7, 9, 4, 6, 2, 8},
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
    };

    private static final List<Family> FAMILIES = new ArrayList<>();

    private static final String BRONZE_TO_IRIDIUM = "bronze_to_iridium";
    private static final String STEEL_TO_IRIDIUM = "steel_to_iridium";
    private static final String ELECTRIC_TO_IRIDIUM = "electric_to_iridium";
    private static final String ALUMINUM_TO_IRIDIUM = "aluminum_to_iridium";

    static {
        // Bronze machines, which also exist at every level above.
        family("compressor", BRONZE_TO_IRIDIUM, Items.PISTON);
        family("cutting_machine", BRONZE_TO_IRIDIUM, Items.IRON_PICKAXE);
        family("furnace", BRONZE_TO_IRIDIUM, Items.BLAST_FURNACE);
        family("macerator", BRONZE_TO_IRIDIUM, MIMaterials.BRONZE.getPart(MIParts.DRILL_HEAD));
        family("mixer", BRONZE_TO_IRIDIUM, MIMaterials.TIN.getPart(MIParts.ROTOR));

        // Steel machines.
        family("packer", STEEL_TO_IRIDIUM, MIItem.CONVEYOR, MIMaterials.STEEL.getPart(MIParts.PLATE), MIMaterials.STEEL.getPart(MIParts.GEAR));
        family("unpacker", STEEL_TO_IRIDIUM, Items.CHEST, MIMaterials.STEEL.getPart(MIParts.GEAR), MIMaterials.STEEL.getPart(MIParts.PLATE));
        family("wiremill", STEEL_TO_IRIDIUM, MIMaterials.STEEL.getPart(MIParts.GEAR), MIMaterials.STEEL.getPart(MIParts.ROD), MIMaterials.STEEL.getPart(MIParts.PLATE));

        // Electric machines.
        family("assembler", ELECTRIC_TO_IRIDIUM, MIItem.ROBOT_ARM, MIItem.CONVEYOR, MIItem.ANALOG_CIRCUIT, MIMaterials.STEEL.getPart(MIParts.GEAR));
        family("centrifuge", ALUMINUM_TO_IRIDIUM, MIItem.LARGE_MOTOR, MIMaterials.ALUMINUM.getPart(MIParts.GEAR), Items.GLASS, MIMaterials.ALUMINUM.getPart(MIParts.PLATE));
        family("chemical_reactor", ALUMINUM_TO_IRIDIUM, MIItem.LARGE_MOTOR, Items.BUCKET, Items.GLASS, MIMaterials.INVAR.getPart(MIParts.PLATE));
        family("distillery", ALUMINUM_TO_IRIDIUM, MIItem.LARGE_PUMP, MIMaterials.CUPRONICKEL.getPart(MIParts.PLATE), Items.GLASS, Items.BUCKET);
        family("electrolyzer", ALUMINUM_TO_IRIDIUM, MIItem.ELECTRONIC_CIRCUIT, MIMaterials.ALUMINUM.getPart(MIParts.WIRE), Items.BUCKET, Items.GLASS);
        family("polarizer", ALUMINUM_TO_IRIDIUM, MIItem.INDUCTOR, MIMaterials.COPPER.getPart(MIParts.WIRE), Items.REDSTONE, Items.IRON_INGOT);
    }

    private static void family(String machine, String levels, ItemLike... parts) {
        FAMILIES.add(new Family(machine, levels, List.of(parts)));
    }

    public ProcessingTableRecipesProvider(PackOutput packOutput) {
        super(packOutput);
    }

    private static List<MachineLevel> levelsOf(String levels) {
        return switch (levels) {
            case BRONZE_TO_IRIDIUM ->
                    List.of(MachineLevel.BRONZE, MachineLevel.STEEL, MachineLevel.ELECTRIC, MachineLevel.ALUMINUM,
                            MachineLevel.STAINLESS_STEEL, MachineLevel.TITANIUM, MachineLevel.IRIDIUM);
            case STEEL_TO_IRIDIUM -> List.of(MachineLevel.STEEL, MachineLevel.ELECTRIC, MachineLevel.ALUMINUM,
                    MachineLevel.STAINLESS_STEEL, MachineLevel.TITANIUM, MachineLevel.IRIDIUM);
            case ELECTRIC_TO_IRIDIUM ->
                    List.of(MachineLevel.ELECTRIC, MachineLevel.ALUMINUM, MachineLevel.STAINLESS_STEEL,
                            MachineLevel.TITANIUM, MachineLevel.IRIDIUM);
            case ALUMINUM_TO_IRIDIUM -> List.of(MachineLevel.ALUMINUM, MachineLevel.STAINLESS_STEEL,
                    MachineLevel.TITANIUM, MachineLevel.IRIDIUM);
            default -> throw new IllegalArgumentException("Unknown level list " + levels);
        };
    }

    private static String prefixOf(MachineLevel level) {
        return level.getSerializedName() + "_";
    }

    private static ItemLike casingOf(MachineLevel level) {
        return switch (level) {
            case BRONZE -> MIMaterials.BRONZE.getPart(MIParts.MACHINE_CASING);
            case STEEL -> lookupItem(MIBlockKeys.STEEL_MACHINE_HULL.location());
            case ELECTRIC -> lookupItem(MIBlockKeys.ELECTRIC_MACHINE_HULL.location());
            case ALUMINUM -> lookupItem(MIBlockKeys.ALUMINUM_MACHINE_HULL.location());
            case STAINLESS_STEEL -> lookupItem(MIBlockKeys.STAINLESS_STEEL_MACHINE_HULL.location());
            case TITANIUM -> lookupItem(MIBlockKeys.TITANIUM_MACHINE_HULL.location());
            case IRIDIUM -> lookupItem(MIBlockKeys.IRIDIUM_MACHINE_HULL.location());
        };
    }

    private static SortedMap<Integer, Ingredient> layout(List<Ingredient> parts) {
        if (parts.isEmpty() || parts.size() > ProcessingTableRecipe.SLOT_COUNT) {
            throw new IllegalArgumentException("Machine recipes require between one and nine parts");
        }
        int[] cells = CELL_PATTERNS[parts.size()];
        SortedMap<Integer, Ingredient> slots = new TreeMap<>();
        for (int index = 0; index < cells.length; index++) {
            slots.put(cells[index], parts.get(index));
        }
        return slots;
    }

    private static void offer(RecipeOutput consumer, String path, ItemLike key, ItemLike first, ItemLike second, ResourceLocation result) {
        offer(consumer, path, Ingredient.of(key), List.of(Ingredient.of(first), Ingredient.of(second), Ingredient.of(thirdPart(path))), result);
    }

    public static final String PROCESSING_TABLE_RECIPE_FOLDER = "processing_table/";
    public static final String MACHINE_RECIPE_FOLDER = "assembler_generated/";

    private static void offer(RecipeOutput consumer, String path, ItemLike key, Ingredient first, ItemLike second, ResourceLocation result) {
        offer(consumer, path, Ingredient.of(key), List.of(first, Ingredient.of(second), Ingredient.of(thirdPart(path))), result);
    }

    private static ItemLike thirdPart(String path) {
        return switch (path.substring(PROCESSING_TABLE_RECIPE_FOLDER.length())) {
            case "bronze_boiler", "bronze_water_pump" -> MIMaterials.BRONZE.getPart(MIParts.PLATE);
            case "large_steam_boiler" -> MIMaterials.COPPER.getPart(MIParts.PLATE);
            case "steel_boiler" -> Items.BUCKET;
            case "steam_quarry", "large_tank" -> MIMaterials.STEEL.getPart(MIParts.PLATE);
            case "distillation_tower", "configurable_chest", "configurable_tank" -> Items.GLASS;
            case "fusion_reactor" -> MIMaterials.IRIDIUM.getPart(MIParts.PLATE);
            case "nuclear_reactor", "pressurizer", "high_pressure_advanced_large_steam_boiler" -> MIMaterials.TITANIUM.getPart(MIParts.PLATE);
            case "electric_blast_furnace" -> MIItem.ANALOG_CIRCUIT;
            case "implosion_compressor" -> MIItem.DIGITAL_CIRCUIT;
            case "heat_exchanger", "vacuum_freezer" -> MIItem.PUMP;
            case "advanced_large_steam_boiler" -> MIMaterials.INVAR.getPart(MIParts.PLATE);
            case "high_pressure_large_steam_boiler" -> MIMaterials.STAINLESS_STEEL.getPart(MIParts.LARGE_PLATE);
            case "electric_steam_turbine", "aluminum_alloy_steam_turbine", "stainless_steel_steam_turbine", "plasma_turbine" -> MIMaterials.COPPER.getPart(MIParts.WIRE);
            case "electric_diesel_generator", "aluminum_alloy_diesel_generator", "stainless_steel_diesel_generator", "large_diesel_generator" -> MIItem.MOTOR;
            case "oil_drilling_rig", "electric_quarry" -> MIMaterials.STEEL.getPart(MIParts.GEAR);
            default -> throw new IllegalArgumentException("No third part defined for " + path);
        };
    }

    private static void offer(RecipeOutput consumer, String path, Ingredient key, List<Ingredient> sides, ResourceLocation result) {
        offer(consumer, path, key, layout(sides), result);
    }

    private static void offer(RecipeOutput consumer, String path, Ingredient key, SortedMap<Integer, Ingredient> slots, ResourceLocation result) {
        var recipe = ProcessingTableRecipe.fromSlots(key, slots, new ItemStack(lookupItem(result)));
        consumer.accept(MI.id(path), recipe, null);

        // A machine crafts at the same pace whatever the recipe, and at the lowest energy a machine can draw.
        var builder = new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, MACHINE_ADAPTER_ENERGY, MACHINE_ADAPTER_DURATION)
                .level(MachineLevel.BRONZE)
                .addItemInput(key, 1, 1);
        for (Ingredient side : slots.values()) {
            builder = builder.addItemInput(side, 1, 1);
        }
        builder.addItemOutput(lookupItem(result), 1)
                .offerTo(consumer, MACHINE_RECIPE_FOLDER + path.substring(PROCESSING_TABLE_RECIPE_FOLDER.length()));
    }

    private static void lithographyMachines(RecipeOutput consumer) {
        List<MachineLevel> levels = List.of(MachineLevel.STAINLESS_STEEL, MachineLevel.TITANIUM, MachineLevel.IRIDIUM);
        List<Material> materials = List.of(MIMaterials.STAINLESS_STEEL, MIMaterials.TITANIUM, MIMaterials.IRIDIUM);
        List<ItemLike> controllers = List.of(MIItem.ELECTRONIC_CIRCUIT, MIItem.DIGITAL_CIRCUIT, MIItem.PROCESSOR);
        ItemLike core = lookupItem(MIBlockKeys.ALUMINUM_MACHINE_HULL.location());
        for (int index = 0; index < levels.size(); index++) {
            MachineLevel level = levels.get(index);
            List<Ingredient> sides = new ArrayList<>();
            for (int plate = 0; plate < 2; plate++) {
                sides.add(materials.get(index).getPart(MIParts.PLATE).getTaggedIngredient());
            }
            sides.add(Ingredient.of(Tags.Items.GLASS_BLOCKS));
            sides.add(Ingredient.of(MIItem.ROBOT_ARM));
            sides.add(Ingredient.of(controllers.get(index)));
            String id = prefixOf(level) + "lithography";
            // The previous controller and machine start this tier without needing its own processor first.
            offer(consumer, PROCESSING_TABLE_RECIPE_FOLDER + id, Ingredient.of(core), sides, MI.id(id));
            core = lookupItem(MI.id(id));
        }
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        lithographyMachines(consumer);
        for (Family family : FAMILIES) {
            for (MachineLevel level : levelsOf(family.levels())) {
                offer(consumer, "processing_table/" + prefixOf(level) + family.machine(),
                        Ingredient.of(casingOf(level)),
                        layout(family.parts().stream().map(Ingredient::of).toList()),
                        MI.id(prefixOf(level) + family.machine()));
            }
        }
        offer(consumer, "processing_table/bronze_boiler", MIBlock.BLOCK_FIRE_CLAY_BRICKS,
                lookupItem(MI.id("bronze_furnace")), Items.BUCKET, MI.id("bronze_boiler"));
        offer(consumer, "processing_table/steel_boiler", lookupItem(MI.id("bronze_boiler")),
                Items.FURNACE, item(MIMaterials.STEEL, MIParts.PLATE), MI.id("steel_boiler"));
        offer(consumer, "processing_table/steam_quarry", casingOf(MachineLevel.STEEL),
                MIItem.INVAR_ROTARY_BLADE, item(MIMaterials.STEEL, MIParts.DRILL_HEAD), MI.id("steam_quarry"));
        offer(consumer, "processing_table/bronze_water_pump", casingOf(MachineLevel.BRONZE),
                Items.BUCKET, item(MIMaterials.BRONZE, MIParts.ROTOR), MI.id("bronze_water_pump"));

        offer(consumer, "processing_table/distillation_tower", lookupItem(MI.id("clean_stainless_steel_machine_casing")),
                lookupItem(MI.id("stainless_steel_distillery")), lookupItem(MI.id("stainless_steel_distillery")), MI.id("distillation_tower"));

        offer(consumer, "processing_table/fusion_reactor", lookupItem(MI.id("iridium_machine_hull")),
                MIItem.QUANTUM_PROCESSOR, lookupItem(MI.id("tungsten_large_plate")), MI.id("fusion_reactor"));

        offer(consumer, "processing_table/nuclear_reactor", lookupItem(MI.id("titanium_machine_hull")),
                MIItem.PROCESSOR, lookupItem(MI.id("nuclear_alloy_large_plate")), MI.id("nuclear_reactor"));

        offer(consumer, "processing_table/electric_blast_furnace", lookupItem(MI.id("electric_machine_hull")),
                Items.BLAST_FURNACE, lookupItem(MI.id("cupronickel_wire_magnetic")), MI.id("electric_blast_furnace"));
        offer(consumer, "processing_table/implosion_compressor", lookupItem(MI.id("stainless_steel_machine_hull")),
                lookupItem(MI.id("titanium_large_plate")), lookupItem(MI.id("blastproof_alloy_large_plate")), MI.id("implosion_compressor"));
        offer(consumer, "processing_table/pressurizer", lookupItem(MI.id("stainless_steel_machine_hull")),
                MIItem.DIGITAL_CIRCUIT, lookupItem(MI.id("solid_titanium_machine_casing")), MI.id("pressurizer"));
        offer(consumer, "processing_table/heat_exchanger", lookupItem(MI.id("stainless_steel_machine_hull")),
                lookupItem(MI.id("large_heat_exchanger")), lookupItem(MI.id("clean_stainless_steel_machine_casing")), MI.id("heat_exchanger"));

        // 锅炉
        offer(consumer, "processing_table/large_steam_boiler", lookupItem(MI.id("bronze_boiler")),
                lookupItem(MI.id("analog_circuit")), lookupItem(MI.id("bronze_plate")), MI.id("large_steam_boiler"));
        offer(consumer, "processing_table/advanced_large_steam_boiler", lookupItem(MI.id("large_steam_boiler")),
                lookupItem(MI.id("electronic_circuit")), lookupItem(MI.id("large_pump")), MI.id("advanced_large_steam_boiler"));
        offer(consumer, "processing_table/high_pressure_large_steam_boiler",
                lookupItem(MI.id("clean_stainless_steel_machine_casing")),
                lookupItem(MI.id("advanced_large_steam_boiler")), MIItem.DIGITAL_CIRCUIT, MI.id("high_pressure_large_steam_boiler"));
        offer(consumer, "processing_table/high_pressure_advanced_large_steam_boiler",
                lookupItem(MI.id("high_pressure_large_steam_boiler")),
                MIItem.PROCESSOR, lookupItem(MI.id("large_pump")), MI.id("high_pressure_advanced_large_steam_boiler"));

        // 涡轮
        offer(consumer, "processing_table/large_steam_turbine",
                Ingredient.of(lookupItem(MI.id("clean_stainless_steel_machine_casing"))),
                List.of(Ingredient.of(MIItem.DIGITAL_CIRCUIT), Ingredient.of(lookupItem(MI.id("stainless_steel_rotor"))),
                        Ingredient.of(item(MIMaterials.STAINLESS_STEEL, MIParts.LARGE_PLATE)), Ingredient.of(MIItem.PUMP)), MI.id("large_steam_turbine"));
        offer(consumer, "processing_table/electric_steam_turbine", lookupItem(MI.id("electric_machine_hull")),
                lookupItem(MI.id("analog_circuit")), lookupItem(MI.id("tin_rotor")), MI.id("electric_steam_turbine"));
        offer(consumer, "processing_table/aluminum_alloy_steam_turbine",
                lookupItem(MI.id("aluminum_alloy_machine_hull")),
                lookupItem(MI.id("electronic_circuit")), lookupItem(MI.id("aluminum_rotor")), MI.id("aluminum_alloy_steam_turbine"));
        offer(consumer, "processing_table/stainless_steel_steam_turbine",
                lookupItem(MI.id("stainless_steel_machine_hull")),
                MIItem.DIGITAL_CIRCUIT, lookupItem(MI.id("stainless_steel_rotor")), MI.id("stainless_steel_steam_turbine"));
        offer(consumer, "processing_table/plasma_turbine", lookupItem(MI.id("iridium_machine_hull")),
                MIItem.QUANTUM_PROCESSOR, lookupItem(MI.id("titanium_rotor")), MI.id("plasma_turbine"));

        // 柴油发电机
        offer(consumer, "processing_table/electric_diesel_generator", lookupItem(MI.id("electric_machine_hull")),
                lookupItem(MI.id("tin_wire")), lookupItem(MI.id("tin_gear")), MI.id("electric_diesel_generator"));
        offer(consumer, "processing_table/aluminum_alloy_diesel_generator",
                lookupItem(MI.id("aluminum_alloy_machine_hull")),
                lookupItem(MI.id("electrum_wire")), lookupItem(MI.id("aluminum_gear")), MI.id("aluminum_alloy_diesel_generator"));
        offer(consumer, "processing_table/stainless_steel_diesel_generator",
                lookupItem(MI.id("stainless_steel_machine_hull")),
                lookupItem(MI.id("aluminum_wire")), lookupItem(MI.id("stainless_steel_gear")), MI.id("stainless_steel_diesel_generator"));
        offer(consumer, "processing_table/large_diesel_generator", lookupItem(MI.id("titanium_machine_hull")),
                lookupItem(MI.id("silver_wire")), lookupItem(MI.id("titanium_gear")), MI.id("large_diesel_generator"));

        // 采掘与冷冻
        offer(consumer, "processing_table/oil_drilling_rig", lookupItem(MI.id("aluminum_alloy_machine_hull")),
                lookupItem(MI.id("electronic_circuit")), lookupItem(MI.id("aluminum_alloy_large_plate")), MI.id("oil_drilling_rig"));
        offer(consumer, "processing_table/electric_quarry", lookupItem(MI.id("aluminum_alloy_machine_hull")),
                lookupItem(MI.id("steam_quarry")), lookupItem(MI.id("electronic_circuit")), MI.id("electric_quarry"));
        offer(consumer, "processing_table/vacuum_freezer", lookupItem(MI.id("aluminum_alloy_machine_hull")),
                lookupItem(MI.id("large_pump")), lookupItem(MI.id("aluminum_large_plate")), MI.id("vacuum_freezer"));

        // 储运
        offer(consumer, "processing_table/large_tank", lookupItem(MI.id("steel_machine_hull")),
                Items.BUCKET, lookupItem(MI.id("pump")), MI.id("large_tank"));
        offer(consumer, "processing_table/configurable_chest", lookupItem(MI.id("steel_machine_hull")),
                Ingredient.of(Tags.Items.CHESTS), lookupItem(MI.id("analog_circuit")), MI.id("configurable_chest"));
        offer(consumer, "processing_table/configurable_tank", lookupItem(MI.id("steel_machine_hull")),
                Items.BUCKET, lookupItem(MI.id("analog_circuit")), MI.id("configurable_tank"));

        new ShapedRecipeJson(idOf(MIBlock.PROCESSING_TABLE.get()), 1, "II", "WW", "WW")
                .addInput('I', idOf(Items.HEAVY_WEIGHTED_PRESSURE_PLATE))
                .addInput('W', "#" + ItemTags.PLANKS.location())
                .offerTo(consumer, "processing_table/processing_table");
    }

    private record Family(String machine, String levels, List<ItemLike> parts) {
    }

    private static final int MACHINE_ADAPTER_ENERGY = 2;
    private static final int MACHINE_ADAPTER_DURATION = 200;

    private static Item item(Material material, PartKeyProvider part) {
        return material.getPart(part).asItem();
    }

    private static Item lookupItem(ResourceLocation id) {
        Item item = BuiltInRegistries.ITEM.get(id);
        if (item == Items.AIR) {
            throw new IllegalStateException("The processing table recipe table refers to " + id + ", which is not a registered item");
        }
        return item;
    }

    private static String idOf(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).toString();
    }
}
