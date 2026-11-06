package aztech.milite.datagen.recipe;

import aztech.milite.MIItem;

import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.recipe.json.ShapedRecipeJson;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;

public class QuarryRecipesProvider extends FixedRecipesProvider {
    public QuarryRecipesProvider(PackOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        new MachineRecipeBuilder(MIMachineRecipeTypes.QUARRY, 4, 600)
                .level("bronze")
                .addItemInput("milite:copper_drill", 1, 0f)
                .addItemOutput(Blocks.COBBLESTONE, 32, 0.5f)
                .addItemOutput(Blocks.DIORITE, 16, 0.25f)
                .addItemOutput(Blocks.ANDESITE, 16, 0.25f)
                .addItemOutput(Blocks.GRANITE, 16, 0.25f)
                .addItemOutput(Blocks.DIRT, 10, 0.5f)
                .addItemOutput(Blocks.GRAVEL, 10, 0.5f)
                .addItemOutput(Blocks.COBBLED_DEEPSLATE, 16, 0.25f)
                .addItemOutput(Blocks.TUFF, 16, 0.25f)
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
                        .addInput('m', "milite:motor")
                        .addInput('P', "milite:pump")
                        .addInput('c', "milite:aluminum_wire")
                        .addInput('C', MIItem.DIGITAL_CIRCUIT.id()));

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
                        .addInput('m', "milite:large_motor")
                        .addInput('c', "milite:annealed_copper_wire")
                        .addInput('C', MIItem.PROCESSOR.id()));

        new MachineRecipeBuilder(MIMachineRecipeTypes.QUARRY, 8, 600)
                .level("bronze")
                .addItemInput("milite:gold_drill", 1, 0f)
                .addItemOutput(Blocks.NETHERRACK, 32, 0.5f)
                .addItemOutput(Blocks.BLACKSTONE, 16, 0.25f)
                .addItemOutput(Blocks.BASALT, 16, 0.25f)
                .addItemOutput(Blocks.SOUL_SOIL, 20, 0.1f)
                .addItemOutput(Blocks.MAGMA_BLOCK, 5, 0.1f)
                .addItemOutput(Blocks.SOUL_SAND, 20, 0.25f)
                .addItemOutput(Blocks.ANCIENT_DEBRIS, 1, 0.05f)
                .addItemOutput(Blocks.GLOWSTONE, 4, 0.15f)
                .addItemOutput(Blocks.NETHER_QUARTZ_ORE, 6, 0.15f)
                .addItemOutput(Blocks.NETHER_GOLD_ORE, 1, 0.25f)
                .offerTo(consumer, "quarry/gold");

        new MachineRecipeBuilder(MIMachineRecipeTypes.QUARRY, 32, 600)
                .level("bronze")
                .addItemInput("milite:stainless_steel_drill", 1, 0f)
                .addItemOutput("milite:titanium_ore", 1, 0.15f)
                .addItemOutput("milite:tungsten_ore", 1, 0.2f)
                .addItemOutput("milite:monazite_ore", 1, 0.25f)
                .addItemOutput("milite:platinum_ore", 1, 0.12f)
                .offerTo(consumer, "quarry/stainless_steel");
    }
}
