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
                .addItemInput("modern_tech:copper_drill", 1, 0f)
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
                new ShapedRecipeJson("modern_tech:aluminum_drill", 4, "gpd", "mCp", "cmg")
                        .addInput('d', "modern_tech:aluminum_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/aluminum")
                        .addInput('m', "modern_tech:pump")
                        .addInput('c', "modern_tech:electrum_wire")
                        .addInput('C', "modern_tech:electronic_circuit"));

        shaped(consumer, "quarry/drill/bronze_drill_asbl",
                new ShapedRecipeJson("modern_tech:bronze_drill", 4, "  d", "gp ", "pg ")
                        .addInput('d', "modern_tech:bronze_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/bronze"));

        shaped(consumer, "quarry/drill/copper_drill_asbl",
                new ShapedRecipeJson("modern_tech:copper_drill", 4, "  d", "gp ", "pg ")
                        .addInput('d', "modern_tech:copper_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/copper"));

        shaped(consumer, "quarry/drill/gold_drill_asbl",
                new ShapedRecipeJson("modern_tech:gold_drill", 4, "gpd", "mCp", "cmg")
                        .addInput('d', "modern_tech:gold_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/gold")
                        .addInput('m', "modern_tech:motor")
                        .addInput('c', "modern_tech:tin_wire")
                        .addInput('C', "modern_tech:analog_circuit"));

        shaped(consumer, "quarry/drill/stainless_steel_drill_asbl",
                new ShapedRecipeJson("modern_tech:stainless_steel_drill", 4, "gpd", "mCp", "cPg")
                        .addInput('d', "modern_tech:stainless_steel_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/stainless_steel")
                        .addInput('m', "modern_tech:motor")
                        .addInput('P', "modern_tech:pump")
                        .addInput('c', "modern_tech:aluminum_wire")
                        .addInput('C', MIItem.DIGITAL_CIRCUIT.id()));

        shaped(consumer, "quarry/drill/steel_drill_asbl",
                new ShapedRecipeJson("modern_tech:steel_drill", 4, "gpd", "mCp", "cmg")
                        .addInput('d', "modern_tech:steel_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/steel")
                        .addInput('m', "modern_tech:motor")
                        .addInput('c', "modern_tech:tin_wire")
                        .addInput('C', "modern_tech:analog_circuit"));

        shaped(consumer, "quarry/drill/titanium_drill_asbl",
                new ShapedRecipeJson("modern_tech:titanium_drill", 4, "gpd", "mCp", "cmg")
                        .addInput('d', "modern_tech:titanium_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/titanium")
                        .addInput('m', "modern_tech:large_motor")
                        .addInput('c', "modern_tech:annealed_copper_wire")
                        .addInput('C', MIItem.PROCESSOR.id()));

        new MachineRecipeBuilder(MIMachineRecipeTypes.QUARRY, 8, 600)
                .level("bronze")
                .addItemInput("modern_tech:gold_drill", 1, 0f)
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
                .addItemInput("modern_tech:stainless_steel_drill", 1, 0f)
                .addItemOutput("modern_tech:titanium_ore", 1, 0.15f)
                .addItemOutput("modern_tech:tungsten_ore", 1, 0.2f)
                .addItemOutput("modern_tech:monazite_ore", 1, 0.25f)
                .addItemOutput("modern_tech:platinum_ore", 1, 0.12f)
                .offerTo(consumer, "quarry/stainless_steel");
    }
}
