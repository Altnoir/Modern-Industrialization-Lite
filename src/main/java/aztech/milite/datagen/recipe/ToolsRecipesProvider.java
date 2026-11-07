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
                new ShapedRecipeJson("moderntech:diesel_jetpack", 1, "PtP", "McM", "R R")
                        .addInput('P', "moderntech:pump")
                        .addInput('M', "moderntech:motor")
                        .addInput('R', "moderntech:aluminum_rotor")
                        .addInput('t', "moderntech:aluminum_tank")
                        .addInput('c', "moderntech:analog_circuit"));

        shaped(consumer, "armor/gravichestplate",
                new ShapedRecipeJson("moderntech:gravichestplate", 1, "P P", "PCP", "cPc")
                        .addInput('P', "moderntech:superconductor_plate")
                        .addInput('C', "moderntech:superconductor_coil")
                        .addInput('c', "moderntech:cooling_cell"));

        shaped(consumer, "tools/diesel_chainsaw",
                new ShapedRecipeJson("moderntech:diesel_chainsaw", 1, "cRr", "MrR", "tMc")
                        .addInput('M', "moderntech:motor")
                        .addInput('r', "moderntech:invar_rotary_blade")
                        .addInput('t', "moderntech:aluminum_tank")
                        .addInput('R', "moderntech:rubber_sheet")
                        .addInput('c', "moderntech:analog_circuit"));

        shaped(consumer, "tools/diesel_mining_drill",
                new ShapedRecipeJson("moderntech:diesel_mining_drill", 1, "crH", "MPr", "tMc")
                        .addInput('P', "moderntech:pump")
                        .addInput('M', "moderntech:motor")
                        .addInput('r', "moderntech:invar_rotary_blade")
                        .addInput('t', "moderntech:aluminum_tank")
                        .addInput('c', "moderntech:analog_circuit")
                        .addInput('H', "moderntech:aluminum_drill_head"));

        shaped(consumer, "tools/steam_mining_drill",
                new ShapedRecipeJson("moderntech:steam_mining_drill", 1, "fdd", "PDd", "bPf")
                        .addInput('P', "moderntech:iron_large_plate")
                        .addInput('f', "minecraft:furnace")
                        .addInput('b', "minecraft:bucket")
                        .addInput('d', "minecraft:diamond")
                        .addInput('D', "moderntech:copper_drill_head"));

        shaped(consumer, "tools/wrench",
                new ShapedRecipeJson("moderntech:wrench", 1, "b b", "bbb", " b ")
                        .addInput('b', "#c:plates/bronze"));
    }
}
