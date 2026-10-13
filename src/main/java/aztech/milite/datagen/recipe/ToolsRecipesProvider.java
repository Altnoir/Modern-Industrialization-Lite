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
                new ShapedRecipeJson("milite:diesel_jetpack", 1, "PtP", "McM", "R R")
                        .addInput('P', "milite:pump")
                        .addInput('M', "milite:motor")
                        .addInput('R', "milite:aluminum_rotor")
                        .addInput('t', "milite:aluminum_tank")
                        .addInput('c', "milite:analog_circuit"));

        shaped(consumer, "armor/gravichestplate",
                new ShapedRecipeJson("milite:gravichestplate", 1, "P P", "PCP", "cPc")
                        .addInput('P', "milite:superconductor_plate")
                        .addInput('C', "milite:superconductor_coil")
                        .addInput('c', "milite:cooling_cell"));

        shaped(consumer, "tools/diesel_chainsaw",
                new ShapedRecipeJson("milite:diesel_chainsaw", 1, "cRr", "MrR", "tMc")
                        .addInput('M', "milite:motor")
                        .addInput('r', "milite:invar_rotary_blade")
                        .addInput('t', "milite:aluminum_tank")
                        .addInput('R', "milite:rubber_sheet")
                        .addInput('c', "milite:analog_circuit"));

        shaped(consumer, "tools/diesel_mining_drill",
                new ShapedRecipeJson("milite:diesel_mining_drill", 1, "crH", "MPr", "tMc")
                        .addInput('P', "milite:pump")
                        .addInput('M', "milite:motor")
                        .addInput('r', "milite:invar_rotary_blade")
                        .addInput('t', "milite:aluminum_tank")
                        .addInput('c', "milite:analog_circuit")
                        .addInput('H', "milite:aluminum_drill_head"));

        smithing(consumer, "tools/smithing/netherite_hammer",
                "minecraft:netherite_upgrade_smithing_template", "milite:diamond_hammer", "minecraft:netherite_ingot",
                "milite:netherite_hammer", 1);

        shaped(consumer, "tools/steam_mining_drill",
                new ShapedRecipeJson("milite:steam_mining_drill", 1, "fdd", "PDd", "bPf")
                        .addInput('P', "milite:iron_large_plate")
                        .addInput('f', "minecraft:furnace")
                        .addInput('b', "minecraft:bucket")
                        .addInput('d', "minecraft:diamond")
                        .addInput('D', "milite:copper_drill_head"));

        shaped(consumer, "tools/wrench",
                new ShapedRecipeJson("milite:wrench", 1, "b b", "bbb", " b ")
                        .addInput('b', "#c:plates/bronze"));
    }
}
