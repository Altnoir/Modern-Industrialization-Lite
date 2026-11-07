package aztech.milite.datagen.recipe;

import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;

public class FusionRecipesProvider extends FixedRecipesProvider {
    public FusionRecipesProvider(PackOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        new MachineRecipeBuilder(MIMachineRecipeTypes.FUSION_REACTOR, 16000, 1200)
                .level("iridium")
                .addFluidInput("moderntech:deuterium", 1000, 1.0f)
                .addFluidInput("moderntech:deuterium", 1000, 1.0f)
                .addFluidOutput("moderntech:helium_plasma", 500, 1.0f)
                .addFluidOutput("moderntech:tritium", 500, 1.0f)
                .offerTo(consumer, "fusion_reactor/deuterium_deuterium");

        new MachineRecipeBuilder(MIMachineRecipeTypes.FUSION_REACTOR, 16000, 900)
                .level("iridium")
                .addFluidInput("moderntech:deuterium", 1000, 1.0f)
                .addFluidInput("moderntech:tritium", 1000, 1.0f)
                .addFluidOutput("moderntech:helium_plasma", 1000, 1.0f)
                .addFluidOutput("moderntech:hydrogen", 1000, 1.0f)
                .offerTo(consumer, "fusion_reactor/deuterium_tritium");

        new MachineRecipeBuilder(MIMachineRecipeTypes.FUSION_REACTOR, 16000, 600)
                .level("iridium")
                .addFluidInput("moderntech:helium_3", 1000, 1.0f)
                .addFluidInput("moderntech:helium_3", 1000, 1.0f)
                .addFluidOutput("moderntech:helium_plasma", 1000, 1.0f)
                .addFluidOutput("moderntech:hydrogen", 2000, 1.0f)
                .offerTo(consumer, "fusion_reactor/helium_helium");

        new MachineRecipeBuilder(MIMachineRecipeTypes.FUSION_REACTOR, 16000, 300)
                .level("iridium")
                .addFluidInput("moderntech:helium_3", 1000, 1.0f)
                .addFluidInput("moderntech:tritium", 1000, 1.0f)
                .addFluidOutput("moderntech:helium_plasma", 1000, 1.0f)
                .addFluidOutput("moderntech:deuterium", 1000, 1.0f)
                .addFluidOutput("moderntech:hydrogen", 1000, 1.0f)
                .offerTo(consumer, "fusion_reactor/helium_tritium");
    }
}
