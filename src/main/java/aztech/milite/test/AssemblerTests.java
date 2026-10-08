package aztech.milite.test;

import aztech.milite.datagen.recipe.ProcessingTableRecipesProvider;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipe;
import aztech.milite.MIRegistries;
import aztech.milite.test.framework.MIGameTest;
import aztech.milite.test.framework.MIGameTestHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class AssemblerTests {
    /**
     * An assembler builds machines, which it can only do because every Processing Table recipe is written a second time
     * as an ordinary assembler recipe. This checks that no recipe was left behind, and that each of them is reachable
     * the way an assembler reaches a recipe: through the item indexed cache of its recipe type. A recipe that is in the
     * recipe manager but missing from that cache is invisible to the machine, which is the failure this guards against.
     */
    @MIGameTest
    public void assemblerFindsProcessingTableRecipes(MIGameTestHelper helper) {
        var level = helper.getLevel();
        var assembler = MIMachineRecipeTypes.ASSEMBLER;

        int tableRecipes = level.getRecipeManager().getAllRecipesFor(MIRegistries.PROCESSING_TABLE_RECIPE_TYPE.get()).size();
        List<RecipeHolder<MachineRecipe>> machineRecipes = assembler.getRecipesWithoutCache(level).stream()
                .filter(holder -> holder.id().getPath().startsWith(ProcessingTableRecipesProvider.MACHINE_RECIPE_FOLDER))
                .toList();

        if (tableRecipes == 0) {
            helper.fail("no Processing Table recipe is loaded, so this test cannot prove anything");
        }
        if (machineRecipes.size() != tableRecipes) {
            helper.fail("%d Processing Table recipes are loaded but only %d of them can be run by an assembler"
                    .formatted(tableRecipes, machineRecipes.size()));
        }

        for (var holder : machineRecipes) {
            var recipe = holder.value();
            if (recipe.itemInputs.size() != 2) {
                helper.fail("recipe %s should take exactly two inputs, it takes %d".formatted(holder.id(), recipe.itemInputs.size()));
            }
            // An assembler looks recipes up by the items it holds, so every input has to lead back to the recipe.
            for (var input : recipe.itemInputs) {
                for (var item : input.getInputItems()) {
                    if (!assembler.getMatchingRecipes(level, item).contains(holder)) {
                        helper.fail("recipe %s cannot be found from input item %s, so an assembler holding it would do nothing"
                                .formatted(holder.id(), BuiltInRegistries.ITEM.getKey(item)));
                    }
                }
            }
        }

        helper.succeed();
    }
}
