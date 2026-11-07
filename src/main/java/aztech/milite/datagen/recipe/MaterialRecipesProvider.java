package aztech.milite.datagen.recipe;

import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.materials.Material;
import aztech.milite.materials.MaterialRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;

public class MaterialRecipesProvider extends MIRecipesProvider {
    public MaterialRecipesProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        for (Material material : MaterialRegistry.getMaterials().values()) {
            material.registerRecipes.accept(recipeOutput);
        }

        generatePressurizerRecipes(recipeOutput);
    }

    private void generatePressurizerRecipes(RecipeOutput recipeOutput) {
        String[][] fluids = {
                {"minecraft:water", "moderntech:high_pressure_water"},
                {"moderntech:steam", "moderntech:high_pressure_steam"},
                {"moderntech:heavy_water", "moderntech:high_pressure_heavy_water"},
                {"moderntech:heavy_water_steam", "moderntech:high_pressure_heavy_water_steam"}
        };

        for (String[] fluid : fluids) {
            String input = fluid[0];
            String highPressure = fluid[1];
            String name = input.substring(input.indexOf(':') + 1);

            new MachineRecipeBuilder(MIMachineRecipeTypes.PRESSURIZER, 64, 1200)
                    .addFluidInput(input, 1000)
                    .addFluidOutput(highPressure, 125)
                    .offerTo(recipeOutput, "materials/pressurizer/up/" + name);

            new MachineRecipeBuilder(MIMachineRecipeTypes.PRESSURIZER, 2, 100)
                    .addFluidInput(highPressure, 125)
                    .addFluidOutput(input, 1000)
                    .offerTo(recipeOutput, "materials/pressurizer/down/" + name);
        }
    }
}
