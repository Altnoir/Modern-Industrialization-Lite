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
                .addReusableItemInput("moderntech:copper_drill", 1)
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
                new ShapedRecipeJson("moderntech:aluminum_drill", 4, "gpd", "mCp", "cmg")
                        .addInput('d', "moderntech:aluminum_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/aluminum")
                        .addInput('m', "moderntech:pump")
                        .addInput('c', "moderntech:electrum_wire")
                        .addInput('C', "moderntech:electronic_circuit"));

        shaped(consumer, "quarry/drill/bronze_drill_asbl",
                new ShapedRecipeJson("moderntech:bronze_drill", 4, "  d", "gp ", "pg ")
                        .addInput('d', "moderntech:bronze_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/bronze"));

        shaped(consumer, "quarry/drill/copper_drill_asbl",
                new ShapedRecipeJson("moderntech:copper_drill", 4, "  d", "gp ", "pg ")
                        .addInput('d', "moderntech:copper_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/copper"));

        shaped(consumer, "quarry/drill/gold_drill_asbl",
                new ShapedRecipeJson("moderntech:gold_drill", 4, "gpd", "mCp", "cmg")
                        .addInput('d', "moderntech:gold_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/gold")
                        .addInput('m', "moderntech:motor")
                        .addInput('c', "moderntech:tin_wire")
                        .addInput('C', "moderntech:analog_circuit"));

        shaped(consumer, "quarry/drill/stainless_steel_drill_asbl",
                new ShapedRecipeJson("moderntech:stainless_steel_drill", 4, "gpd", "mCp", "cPg")
                        .addInput('d', "moderntech:stainless_steel_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/stainless_steel")
                        .addInput('m', "moderntech:motor")
                        .addInput('P', "moderntech:pump")
                        .addInput('c', "moderntech:aluminum_wire")
                        .addInput('C', MIItem.DIGITAL_CIRCUIT.id()));

        shaped(consumer, "quarry/drill/steel_drill_asbl",
                new ShapedRecipeJson("moderntech:steel_drill", 4, "gpd", "mCp", "cmg")
                        .addInput('d', "moderntech:steel_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/steel")
                        .addInput('m', "moderntech:motor")
                        .addInput('c', "moderntech:tin_wire")
                        .addInput('C', "moderntech:analog_circuit"));

        shaped(consumer, "quarry/drill/titanium_drill_asbl",
                new ShapedRecipeJson("moderntech:titanium_drill", 4, "gpd", "mCp", "cmg")
                        .addInput('d', "moderntech:titanium_drill_head")
                        .addInput('g', "#c:gears/iron")
                        .addInput('p', "#c:plates/titanium")
                        .addInput('m', "moderntech:large_motor")
                        .addInput('c', "moderntech:annealed_copper_wire")
                        .addInput('C', MIItem.PROCESSOR.id()));

        new MachineRecipeBuilder(MIMachineRecipeTypes.QUARRY, 8, 600)
                .level("bronze")
                .addReusableItemInput("moderntech:gold_drill", 1)
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
                .addReusableItemInput("moderntech:stainless_steel_drill", 1)
                .addItemOutput("moderntech:titanium_ore", 1, 0.15f)
                .addItemOutput("moderntech:tungsten_ore", 1, 0.2f)
                .addItemOutput("moderntech:monazite_ore", 1, 0.25f)
                .addItemOutput("moderntech:platinum_ore", 1, 0.12f)
                .offerTo(consumer, "quarry/stainless_steel");
    }
}
