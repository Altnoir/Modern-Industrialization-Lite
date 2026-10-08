package aztech.milite.datagen.recipe;

import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;

public final class PlankRecipesProvider extends MIRecipesProvider {
    public PlankRecipesProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        genPlanks(consumer, "oak", true);
        genPlanks(consumer, "spruce", true);
        genPlanks(consumer, "birch", true);
        genPlanks(consumer, "jungle", true);
        genPlanks(consumer, "acacia", true);
        genPlanks(consumer, "dark_oak", true);
        genPlanks(consumer, "mangrove", true);
        genPlanks(consumer, "cherry", true);
        genPlanks(consumer, "crimson", false);
        genPlanks(consumer, "warped", false);
    }

    private static void genPlanks(RecipeOutput consumer, String prefix, boolean log) {
        String suffixTag = log ? "logs" : "stems";
        String suffix = log ? "log" : "stem";

        String packedSuffix = log ? "wood" : "hyphae";

        var planks = new MachineRecipeBuilder(MIMachineRecipeTypes.CUTTING_MACHINE, 2, 100)
                .addItemInput("#minecraft:" + prefix + "_" + suffixTag, 1).addItemOutput("minecraft:" + prefix + "_planks", 6);

        planks.offerTo(consumer, "cutting_machine/planks/" + prefix);

        var stripped = new MachineRecipeBuilder(MIMachineRecipeTypes.CUTTING_MACHINE, 2, 100)
                .addItemInput("minecraft:" + prefix + "_" + suffix, 1).addItemOutput("minecraft:stripped_" + prefix + "_" + suffix, 1);

        stripped.offerTo(consumer, "cutting_machine/stripped/" + prefix);

        var strippedWood = new MachineRecipeBuilder(MIMachineRecipeTypes.CUTTING_MACHINE, 2, 100)
                .addItemInput("minecraft:" + prefix + "_" + packedSuffix, 1).addItemOutput("minecraft:stripped_" + prefix + "_" + packedSuffix, 1);

        strippedWood.offerTo(consumer, "cutting_machine/stripped_wood/" + prefix);

        var slab = new MachineRecipeBuilder(MIMachineRecipeTypes.CUTTING_MACHINE, 2, 100)
                .addItemInput("minecraft:" + prefix + "_planks", 1).addItemOutput("minecraft:" + prefix + "_slab", 2);

        slab.offerTo(consumer, "cutting_machine/slabs/" + prefix);

        // packer

        var packedWood = new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100).addItemInput("minecraft:" + prefix + "_" + suffix, 4)
                .addItemOutput("minecraft:" + prefix + "_" + packedSuffix, 3);
        packedWood.offerTo(consumer, "packer/wood/" + prefix);

        var packedStrippedWood = new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .addItemInput("minecraft:stripped_" + prefix + "_" + suffix, 4)
                .addItemOutput("minecraft:stripped_" + prefix + "_" + packedSuffix, 3);
        packedStrippedWood.offerTo(consumer, "packer/stripped_wood/" + prefix);
    }
}
