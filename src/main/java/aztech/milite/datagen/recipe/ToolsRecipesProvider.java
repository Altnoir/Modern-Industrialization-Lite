package aztech.milite.datagen.recipe;

import aztech.milite.recipe.json.ShapedRecipeJson;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;

public class ToolsRecipesProvider extends FixedRecipesProvider {
    public ToolsRecipesProvider(PackOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        shaped(consumer, "armor/diesel_jetpack",
                new ShapedRecipeJson("modern_tech:diesel_jetpack", 1, "PtP", "McM", "R R")
                        .addInput('P', "modern_tech:pump")
                        .addInput('M', "modern_tech:motor")
                        .addInput('R', "modern_tech:aluminum_rotor")
                        .addInput('t', "modern_tech:aluminum_tank")
                        .addInput('c', "modern_tech:analog_circuit"));

        shaped(consumer, "armor/gravichestplate",
                new ShapedRecipeJson("modern_tech:gravichestplate", 1, "P P", "PCP", "cPc")
                        .addInput('P', "modern_tech:superconductor_plate")
                        .addInput('C', "modern_tech:superconductor_coil")
                        .addInput('c', "modern_tech:cooling_cell"));

        shaped(consumer, "tools/diesel_chainsaw",
                new ShapedRecipeJson("modern_tech:diesel_chainsaw", 1, "cRr", "MrR", "tMc")
                        .addInput('M', "modern_tech:motor")
                        .addInput('r', "modern_tech:invar_rotary_blade")
                        .addInput('t', "modern_tech:aluminum_tank")
                        .addInput('R', "modern_tech:rubber_sheet")
                        .addInput('c', "modern_tech:analog_circuit"));

        shaped(consumer, "tools/diesel_mining_drill",
                new ShapedRecipeJson("modern_tech:diesel_mining_drill", 1, "crH", "MPr", "tMc")
                        .addInput('P', "modern_tech:pump")
                        .addInput('M', "modern_tech:motor")
                        .addInput('r', "modern_tech:invar_rotary_blade")
                        .addInput('t', "modern_tech:aluminum_tank")
                        .addInput('c', "modern_tech:analog_circuit")
                        .addInput('H', "modern_tech:aluminum_drill_head"));

        shaped(consumer, "tools/steam_mining_drill",
                new ShapedRecipeJson("modern_tech:steam_mining_drill", 1, "fdd", "PDd", "bPf")
                        .addInput('P', "modern_tech:iron_large_plate")
                        .addInput('f', "minecraft:furnace")
                        .addInput('b', "minecraft:bucket")
                        .addInput('d', "minecraft:diamond")
                        .addInput('D', "modern_tech:copper_drill_head"));

        shaped(consumer, "tools/wrench",
                new ShapedRecipeJson("modern_tech:wrench", 1, "b b", "bbb", " b ")
                        .addInput('b', "#c:plates/bronze"));
    }
}
