package aztech.milite.datagen.recipe;

import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.recipe.json.ShapedRecipeJson;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;

public class QuarryRecipesProvider extends FixedRecipesProvider {
    public QuarryRecipesProvider(PackOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        new MachineRecipeBuilder(MIMachineRecipeTypes.QUARRY, 4, 600)
                .level("bronze")
                .addItemInput("milite:copper_drill", 1, 0.1f)
                .addItemOutput("minecraft:cobblestone", 32, 0.5f)
                .addItemOutput("minecraft:diorite", 16, 0.25f)
                .addItemOutput("minecraft:andesite", 16, 0.25f)
                .addItemOutput("minecraft:granite", 16, 0.25f)
                .addItemOutput("minecraft:dirt", 10, 0.5f)
                .addItemOutput("minecraft:gravel", 10, 0.5f)
                .addItemOutput("minecraft:cobbled_deepslate", 16, 0.25f)
                .addItemOutput("minecraft:tuff", 16, 0.25f)
                .offerTo(consumer, "quarry/copper");

        shaped(consumer, "quarry/drill/aluminum_drill_asbl",
                new ShapedRecipeJson("milite:aluminum_drill", 4, "gpd", "mCp", "cmg")
                        .addInput('d', "milite:aluminum_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/aluminum")
                        .addInput('m', "milite:pump")
                        .addInput('c', "milite:electrum_wire")
                        .addInput('C', "milite:electronic_circuit"));

        shaped(consumer, "quarry/drill/bronze_drill_asbl",
                new ShapedRecipeJson("milite:bronze_drill", 4, "  d", "gp ", "pg ")
                        .addInput('d', "milite:bronze_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/bronze"));

        shaped(consumer, "quarry/drill/copper_drill_asbl",
                new ShapedRecipeJson("milite:copper_drill", 4, "  d", "gp ", "pg ")
                        .addInput('d', "milite:copper_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/copper"));

        shaped(consumer, "quarry/drill/gold_drill_asbl",
                new ShapedRecipeJson("milite:gold_drill", 4, "gpd", "mCp", "cmg")
                        .addInput('d', "milite:gold_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/gold")
                        .addInput('m', "milite:motor")
                        .addInput('c', "milite:tin_wire")
                        .addInput('C', "milite:analog_circuit"));

        shaped(consumer, "quarry/drill/stainless_steel_drill_asbl",
                new ShapedRecipeJson("milite:stainless_steel_drill", 4, "gpd", "mCp", "cPg")
                        .addInput('d', "milite:stainless_steel_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/stainless_steel")
                        .addInput('m', "milite:advanced_motor")
                        .addInput('P', "milite:advanced_pump")
                        .addInput('c', "milite:aluminum_wire")
                        .addInput('C', "milite:digital_circuit"));

        shaped(consumer, "quarry/drill/steel_drill_asbl",
                new ShapedRecipeJson("milite:steel_drill", 4, "gpd", "mCp", "cmg")
                        .addInput('d', "milite:steel_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/steel")
                        .addInput('m', "milite:motor")
                        .addInput('c', "milite:tin_wire")
                        .addInput('C', "milite:analog_circuit"));

        shaped(consumer, "quarry/drill/titanium_drill_asbl",
                new ShapedRecipeJson("milite:titanium_drill", 4, "gpd", "mCp", "cmg")
                        .addInput('d', "milite:titanium_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/titanium")
                        .addInput('m', "milite:large_advanced_motor")
                        .addInput('c', "milite:annealed_copper_wire")
                        .addInput('C', "milite:processing_unit"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.QUARRY, 8, 600)
                .level("bronze")
                .addItemInput("milite:gold_drill", 1, 0.1f)
                .addItemOutput("minecraft:netherrack", 32, 0.5f)
                .addItemOutput("minecraft:blackstone", 16, 0.25f)
                .addItemOutput("minecraft:basalt", 16, 0.25f)
                .addItemOutput("minecraft:soul_soil", 20, 0.1f)
                .addItemOutput("minecraft:magma_block", 5, 0.1f)
                .addItemOutput("minecraft:soul_sand", 20, 0.25f)
                .addItemOutput("minecraft:ancient_debris", 1, 0.05f)
                .addItemOutput("minecraft:glowstone", 4, 0.15f)
                .addItemOutput("minecraft:nether_quartz_ore", 6, 0.15f)
                .addItemOutput("minecraft:nether_gold_ore", 1, 0.25f)
                .offerTo(consumer, "quarry/gold");

        new MachineRecipeBuilder(MIMachineRecipeTypes.QUARRY, 32, 600)
                .level("bronze")
                .addItemInput("milite:stainless_steel_drill", 1, 0.04f)
                .addItemOutput("milite:titanium_ore", 1, 0.15f)
                .addItemOutput("milite:tungsten_ore", 1, 0.2f)
                .addItemOutput("milite:monazite_ore", 1, 0.25f)
                .addItemOutput("milite:platinum_ore", 1, 0.12f)
                .offerTo(consumer, "quarry/stainless_steel");
    }
}
