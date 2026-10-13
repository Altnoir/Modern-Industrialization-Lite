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

import java.util.ArrayList;
import java.util.List;

public class ProcessingTableRecipesProvider extends MIRecipesProvider {
    private record Family(String machine, String levels, ItemLike first, ItemLike second) {
    }

    private static final List<Family> FAMILIES = new ArrayList<>();

    private static final String BRONZE_TO_IRIDIUM = "bronze_to_iridium";
    private static final String STEEL_TO_IRIDIUM = "steel_to_iridium";
    private static final String ELECTRIC_TO_IRIDIUM = "electric_to_iridium";

    private static void family(String machine, String levels, ItemLike first, ItemLike second) {
        FAMILIES.add(new Family(machine, levels, first, second));
    }

    static {
        // Bronze machines, which also exist at every level above.
        family("compressor", BRONZE_TO_IRIDIUM, Items.PISTON, MIMaterials.BRONZE.getPart(MIParts.GEAR));
        family("cutting_machine", BRONZE_TO_IRIDIUM, MIItem.INVAR_ROTARY_BLADE, MIMaterials.INVAR.getPart(MIParts.GEAR));
        family("furnace", BRONZE_TO_IRIDIUM, Items.BLAST_FURNACE, MIBlock.BLOCK_FIRE_CLAY_BRICKS);
        family("macerator", BRONZE_TO_IRIDIUM, MIMaterials.BRONZE.getPart(MIParts.DRILL_HEAD), MIMaterials.BRONZE.getPart(MIParts.BARREL));
        family("mixer", BRONZE_TO_IRIDIUM, MIMaterials.TIN.getPart(MIParts.ROTOR), Items.BUCKET);

        // Steel machines.
        family("packer", STEEL_TO_IRIDIUM, MIItem.CONVEYOR, MIMaterials.STEEL.getPart(MIParts.PLATE));
        family("unpacker", STEEL_TO_IRIDIUM, Items.CHEST, MIMaterials.STEEL.getPart(MIParts.GEAR));
        family("wiremill", STEEL_TO_IRIDIUM, MIItem.MOTOR, MIMaterials.INVAR.getPart(MIParts.ROD));

        // Electric machines.
        family("assembler", ELECTRIC_TO_IRIDIUM, MIItem.ROBOT_ARM, MIItem.CONVEYOR);
        family("centrifuge", ELECTRIC_TO_IRIDIUM, MIItem.LARGE_MOTOR, MIMaterials.ALUMINUM.getPart(MIParts.GEAR));
        family("chemical_reactor", ELECTRIC_TO_IRIDIUM, MIItem.LARGE_MOTOR, Items.BUCKET);
        family("distillery", ELECTRIC_TO_IRIDIUM, MIItem.LARGE_PUMP, MIMaterials.CUPRONICKEL.getPart(MIParts.PLATE));
        family("electrolyzer", ELECTRIC_TO_IRIDIUM, MIItem.ELECTRONIC_CIRCUIT, MIMaterials.ALUMINUM.getPart(MIParts.WIRE));
        family("polarizer", ELECTRIC_TO_IRIDIUM, MIItem.INDUCTOR, MIMaterials.COPPER.getPart(MIParts.WIRE));
    }

    public ProcessingTableRecipesProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        for (Family family : FAMILIES) {
            for (MachineLevel level : levelsOf(family.levels())) {
                offer(consumer, "processing_table/" + prefixOf(level) + family.machine(),
                        casingOf(level), family.first(), family.second(),
                        MI.id(prefixOf(level) + family.machine()));
            }
        }
        offer(consumer, "processing_table/bronze_boiler", MIBlock.BLOCK_FIRE_CLAY_BRICKS,
                lookupItem(MI.id("bronze_furnace")), Items.BUCKET,
                MI.id("bronze_boiler"));
        offer(consumer, "processing_table/steel_boiler", lookupItem(MI.id("bronze_boiler")),
                Items.FURNACE, item(MIMaterials.STEEL, MIParts.PLATE),
                MI.id("steel_boiler"));
        offer(consumer, "processing_table/steam_quarry", casingOf(MachineLevel.STEEL),
                MIItem.INVAR_ROTARY_BLADE, item(MIMaterials.STEEL, MIParts.DRILL_HEAD),
                MI.id("steam_quarry"));
        offer(consumer, "processing_table/bronze_water_pump", casingOf(MachineLevel.BRONZE),
                Items.BUCKET, item(MIMaterials.BRONZE, MIParts.ROTOR),
                MI.id("bronze_water_pump"));

        offer(consumer, "processing_table/distillation_tower", lookupItem(MI.id("clean_stainless_steel_machine_casing")),
                lookupItem(MI.id("stainless_steel_distillery")), lookupItem(MI.id("stainless_steel_distillery")),
                MI.id("distillation_tower"));

        new ShapedRecipeJson(idOf(MIBlock.PROCESSING_TABLE.get()), 1, "II", "WW", "WW")
                .addInput('I', idOf(Items.HEAVY_WEIGHTED_PRESSURE_PLATE))
                .addInput('W', "#" + ItemTags.PLANKS.location())
                .offerTo(consumer, "processing_table/processing_table");
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

    public static final String PROCESSING_TABLE_RECIPE_FOLDER = "processing_table/";
    public static final String MACHINE_RECIPE_FOLDER = "assembler_generated/";

    private static void offer(RecipeOutput consumer, String path, ItemLike key, ItemLike first, ItemLike second, ResourceLocation result) {
        var recipe = new ProcessingTableRecipe(Ingredient.of(key), Ingredient.of(first), Ingredient.of(second), new ItemStack(lookupItem(result)));
        consumer.accept(MI.id(path), recipe, null);

        // A machine crafts at the same pace whatever the recipe, and at the lowest energy a machine can draw.
        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, MACHINE_ADAPTER_ENERGY, MACHINE_ADAPTER_DURATION)
                .level(MachineLevel.BRONZE)
                .addItemInput(Ingredient.of(key), 1, 1)
                .addItemInput(Ingredient.of(first), 1, 1)
                .addItemInput(Ingredient.of(second), 1, 1)
                .addItemOutput(lookupItem(result), 1)
                .offerTo(consumer, MACHINE_RECIPE_FOLDER + path.substring(PROCESSING_TABLE_RECIPE_FOLDER.length()));
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
