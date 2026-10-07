/*
 * MIT License
 *
 * Copyright (c) 2026 Altnoir Null々
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package aztech.modern_industrialization.datagen.recipe;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.MIBlockKeys;
import aztech.modern_industrialization.MIItem;
import aztech.modern_industrialization.blocks.processingtable.ProcessingTableRecipe;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.init.MachineLevel;
import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import aztech.modern_industrialization.materials.MIMaterials;
import aztech.modern_industrialization.materials.Material;
import aztech.modern_industrialization.materials.part.MIParts;
import aztech.modern_industrialization.materials.part.PartKeyProvider;
import aztech.modern_industrialization.recipe.json.ShapelessRecipeBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates the Processing Table recipes: one machine casing plus one characterising item make a machine.
 *
 * <p>
 * This is the only way a machine is crafted. Every level a machine exists at gets its own recipe here, using the casing
 * of that level, so the ingredient a player needs depends on which level they are building.
 *
 * <p>
 * Everything in the table below is referenced through the game classes rather than through ids, so a rename is caught by
 * the compiler instead of producing a recipe that silently never matches. The ids only appear again in the generated
 * json, because that is what the game reads.
 */
public class ProcessingTableRecipesProvider extends MIRecipesProvider {
    /** A machine to craft, and the item that characterises it. */
    private record Family(String machine, String levels, ItemLike ingredient) {}

    private static final List<Family> FAMILIES = new ArrayList<>();

    /** Levels a machine is registered at, in the same shape {@code SingleBlockCraftingMachines} uses. */
    private static final String BRONZE_TO_IRIDIUM = "bronze_to_iridium";
    private static final String STEEL_TO_IRIDIUM = "steel_to_iridium";
    private static final String ELECTRIC_TO_IRIDIUM = "electric_to_iridium";

    private static void family(String machine, String levels, ItemLike ingredient) {
        FAMILIES.add(new Family(machine, levels, ingredient));
    }

    static {
        // Bronze machines, which also exist at every level above.
        family("compressor", BRONZE_TO_IRIDIUM, Items.PISTON);
        family("cutting_machine", BRONZE_TO_IRIDIUM, MIItem.INVAR_ROTARY_BLADE);
        family("furnace", BRONZE_TO_IRIDIUM, Items.BLAST_FURNACE);
        family("macerator", BRONZE_TO_IRIDIUM, MIMaterials.BRONZE.getPart(MIParts.DRILL_HEAD));
        family("mixer", BRONZE_TO_IRIDIUM, MIMaterials.TIN.getPart(MIParts.ROTOR));

        // Steel machines.
        family("packer", STEEL_TO_IRIDIUM, MIItem.CONVEYOR);
        family("unpacker", STEEL_TO_IRIDIUM, Items.CHEST);
        family("wiremill", STEEL_TO_IRIDIUM, MIItem.MOTOR);

        // Electric machines.
        family("assembler", ELECTRIC_TO_IRIDIUM, MIItem.ROBOT_ARM);
        family("centrifuge", ELECTRIC_TO_IRIDIUM, MIItem.LARGE_MOTOR);
        family("chemical_reactor", ELECTRIC_TO_IRIDIUM, MIMaterials.ALUMINUM.getPart(MIParts.ROTOR));
        family("distillery", ELECTRIC_TO_IRIDIUM, MIItem.LARGE_PUMP);
        family("electrolyzer", ELECTRIC_TO_IRIDIUM, MIItem.ELECTRONIC_CIRCUIT);
        family("polarizer", ELECTRIC_TO_IRIDIUM, MIItem.INDUCTOR);
    }

    public ProcessingTableRecipesProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        for (Family family : FAMILIES) {
            for (MachineLevel level : levelsOf(family.levels())) {
                offer(consumer, "processing_table/" + prefixOf(level) + family.machine(),
                        casingOf(level), family.ingredient(),
                        MI.id(prefixOf(level) + family.machine()));
            }
        }

        // These two are not built from a machine casing, so they are listed on their own. The boilers are upgraded by
        // rebuilding them around the previous boiler.
        offer(consumer, "processing_table/bronze_boiler", MIBlock.BLOCK_FIRE_CLAY_BRICKS,
                lookupItem(MI.id("bronze_furnace")), MI.id("bronze_boiler"));
        offer(consumer, "processing_table/steel_boiler", lookupItem(MI.id("bronze_boiler")),
                Items.FURNACE, MI.id("steel_boiler"));
        offer(consumer, "processing_table/steam_quarry", casingOf(MachineLevel.STEEL),
                MIItem.INVAR_ROTARY_BLADE, MI.id("steam_quarry"));

        // Water pumps are not in the family list because they are registered with the special machines.
        offer(consumer, "processing_table/bronze_water_pump", casingOf(MachineLevel.BRONZE),
                item(MIMaterials.BRONZE, MIParts.TANK), MI.id("bronze_water_pump"));

        // The Processing Table itself is an ordinary shapeless crafting recipe, not a Processing Table recipe.
        ShapelessRecipeBuilder.shapeless(MIBlock.PROCESSING_TABLE)
                .requires(Items.CRAFTING_TABLE)
                .requires(MIBlock.FORGE_HAMMER)
                .offerTo(consumer, "processing_table/processing_table");
    }

    /**
     * @return the levels a machine with this level list id is registered at
     */
    private static List<MachineLevel> levelsOf(String levels) {
        return switch (levels) {
            case BRONZE_TO_IRIDIUM -> List.of(MachineLevel.BRONZE, MachineLevel.STEEL, MachineLevel.ELECTRIC, MachineLevel.ALUMINUM,
                    MachineLevel.STAINLESS_STEEL, MachineLevel.TITANIUM, MachineLevel.IRIDIUM);
            case STEEL_TO_IRIDIUM -> List.of(MachineLevel.STEEL, MachineLevel.ELECTRIC, MachineLevel.ALUMINUM,
                    MachineLevel.STAINLESS_STEEL, MachineLevel.TITANIUM, MachineLevel.IRIDIUM);
            case ELECTRIC_TO_IRIDIUM -> List.of(MachineLevel.ELECTRIC, MachineLevel.ALUMINUM, MachineLevel.STAINLESS_STEEL,
                    MachineLevel.TITANIUM, MachineLevel.IRIDIUM);
            default -> throw new IllegalArgumentException("Unknown level list " + levels);
        };
    }

    /**
     * The id prefix of a machine at {@code level}. Every level carries its own prefix, so "assembler" alone is never a
     * machine id and the id of a variant always says which level it is.
     */
    private static String prefixOf(MachineLevel level) {
        return level.getSerializedName() + "_";
    }

    /**
     * The block a machine of this level is built around: the material casing for the two steam levels, the machine hull
     * for the electric ones.
     */
    private static ItemLike casingOf(MachineLevel level) {
        return switch (level) {
            case BRONZE -> MIMaterials.BRONZE.getPart(MIParts.MACHINE_CASING);
            case STEEL -> MIMaterials.STEEL.getPart(MIParts.MACHINE_CASING);
            case ELECTRIC -> lookupItem(MIBlockKeys.BASIC_MACHINE_HULL.location());
            case ALUMINUM -> lookupItem(MIBlockKeys.ADVANCED_MACHINE_HULL.location());
            case STAINLESS_STEEL -> lookupItem(MIBlockKeys.TURBO_MACHINE_HULL.location());
            case TITANIUM -> lookupItem(MIBlockKeys.HIGHLY_ADVANCED_MACHINE_HULL.location());
            case IRIDIUM -> lookupItem(MIBlockKeys.QUANTUM_MACHINE_HULL.location());
        };
    }

    /** Where the machine copies of the Processing Table recipes are written, relative to the recipe folder. */
    public static final String MACHINE_RECIPE_FOLDER = "assembler_machine/";

    /**
     * Writes one recipe twice: once for the Processing Table, and once as an ordinary assembler recipe. The second copy
     * is what lets an assembler build machines, and it is written in exactly the same shape as the assembler recipes
     * that are converted from 3x3 crafting, so the assembler needs no special case to read it.
     */
    private static void offer(RecipeOutput consumer, String path, ItemLike first, ItemLike second, ResourceLocation result) {
        var recipe = new ProcessingTableRecipe(Ingredient.of(first), Ingredient.of(second), new ItemStack(lookupItem(result)));
        consumer.accept(MI.id(path), recipe, null);

        // A machine crafts at the same pace whatever the recipe, and at the lowest energy a machine can draw.
        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, MACHINE_ADAPTER_ENERGY, MACHINE_ADAPTER_DURATION)
                .level(MachineLevel.BRONZE)
                .addItemInput(Ingredient.of(first), 1, 1)
                .addItemInput(Ingredient.of(second), 1, 1)
                .addItemOutput(lookupItem(result), 1)
                .offerTo(consumer, MACHINE_RECIPE_FOLDER + path.substring("processing_table/".length()));
    }

    /** Enough energy that even a bronze machine, the weakest one, can run the adapter recipes. */
    private static final int MACHINE_ADAPTER_ENERGY = 2;
    /** Ten seconds by default, matching the pace of the other machine recipes. */
    private static final int MACHINE_ADAPTER_DURATION = 200;

    private static Item item(Material material, PartKeyProvider part) {
        return material.getPart(part).asItem();
    }

    /**
     * Machine blocks are registered while the game starts and have no static field, so they can only be looked up by id.
     */
    private static Item lookupItem(ResourceLocation id) {
        Item item = BuiltInRegistries.ITEM.get(id);
        if (item == Items.AIR) {
            throw new IllegalStateException("The processing table recipe table refers to " + id + ", which is not a registered item");
        }
        return item;
    }
}
