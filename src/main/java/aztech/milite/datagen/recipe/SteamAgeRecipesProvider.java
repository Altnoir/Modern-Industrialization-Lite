package aztech.milite.datagen.recipe;

import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.recipe.json.ShapedRecipeJson;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;

public class SteamAgeRecipesProvider extends FixedRecipesProvider {
    public SteamAgeRecipesProvider(PackOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        shaped(consumer, "steam_age/bronze/machine_casing_asbl",
                new ShapedRecipeJson("moderntech:bronze_machine_casing", 1, "PPP", "PGP", "PPP")
                        .addInput('P', "#c:plates/bronze")
                        .addInput('G', "#c:gears/bronze"));

        shaped(consumer, "steam_age/fireclay/coke_oven",
                new ShapedRecipeJson("moderntech:coke_oven", 1, "BBB", "BFB", "BBB")
                        .addInput('B', "minecraft:bricks")
                        .addInput('F', "minecraft:furnace"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 200)
                .level("bronze")
                .addItemInput(Blocks.FURNACE, 1, 1.0f)
                .addItemInput(Blocks.BRICKS, 8, 1.0f)
                .addItemOutput("moderntech:coke_oven", 1, 1.0f)
                .offerTo(consumer, "steam_age/fireclay/packer/coke_oven");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 200)
                .level("bronze")
                .addItemInput(Blocks.BLAST_FURNACE, 1, 1.0f)
                .addItemInput("moderntech:fire_clay_bricks", 8, 1.0f)
                .addItemOutput("moderntech:steam_blast_furnace", 1, 1.0f)
                .offerTo(consumer, "steam_age/fireclay/packer/packer_steam_blast_furnace");

        shaped(consumer, "steam_age/fireclay/steam_blast_furnace",
                new ShapedRecipeJson("moderntech:steam_blast_furnace", 1, "BBB", "BFB", "BBB")
                        .addInput('B', "moderntech:fire_clay_bricks")
                        .addInput('F', "minecraft:blast_furnace"));

        shaped(consumer, "steam_age/steel/machine_casing_asbl",
                new ShapedRecipeJson("moderntech:steel_machine_casing", 1, "PPP", "PGP", "PPP")
                        .addInput('P', "#c:plates/steel")
                        .addInput('G', "#c:gears/steel"));
    }
}
