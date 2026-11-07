package aztech.milite.datagen.recipe;

import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.recipe.json.ShapedRecipeJson;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.material.Fluids;

public class MiscRecipesProvider extends FixedRecipesProvider {
    public MiscRecipesProvider(PackOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        shaped(consumer, "forge_hammer",
                new ShapedRecipeJson("moderntech:forge_hammer", 1, "___", " i ", "iii")
                        .addInput('_', "minecraft:heavy_weighted_pressure_plate")
                        .addInput('i', "minecraft:iron_ingot"));

        shaped(consumer, "guide_book",
                new ShapedRecipeJson("moderntech:guidebook", 1, "ccc", "cbc", "ccc")
                        .addInput('c', "#c:nuggets/copper")
                        .addInput('b', "minecraft:book"));

        shaped(consumer, "other/packer_block_template",
                new ShapedRecipeJson("moderntech:packer_block_template", 1, "b", "p")
                        .addInput('b', "minecraft:bricks")
                        .addInput('p', "#c:plates/steel"));

        shaped(consumer, "other/packer_double_ingot_template",
                new ShapedRecipeJson("moderntech:packer_double_ingot_template", 1, "b", "p")
                        .addInput('b', "moderntech:iron_double_ingot")
                        .addInput('p', "#c:plates/steel"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput("#c:plates/steel", 8, 1.0f)
                .addFluidInput(Fluids.LAVA, 1000, 1.0f)
                .addItemOutput("moderntech:trash_can", 1, 1.0f)
                .offerTo(consumer, "trash_can_assembler");

        shaped(consumer, "trash_can",
                new ShapedRecipeJson("moderntech:trash_can", 1, "PPP", "PLP", "PPP")
                        .addInput('P', "#c:plates/steel")
                        .addInput('L', "minecraft:lava_bucket"));
    }
}
