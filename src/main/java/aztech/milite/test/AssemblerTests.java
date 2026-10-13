package aztech.milite.test;

import aztech.milite.datagen.recipe.ProcessingTableRecipesProvider;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.MI;
import aztech.milite.MIRegistries;
import aztech.milite.test.framework.MIGameTest;
import aztech.milite.test.framework.MIGameTestHelper;
import net.minecraft.core.registries.BuiltInRegistries;

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

        var tableRecipes = level.getRecipeManager().getAllRecipesFor(MIRegistries.PROCESSING_TABLE_RECIPE_TYPE.get());
        if (tableRecipes.isEmpty()) {
            helper.fail("no Processing Table recipe is loaded, so this test cannot prove anything");
        }

        for (var tableRecipe : tableRecipes) {
            // Mirrors the Processing Table recipe name
            String path = tableRecipe.id().getPath();
            if (!path.startsWith(ProcessingTableRecipesProvider.PROCESSING_TABLE_RECIPE_FOLDER)) {
                helper.fail("recipe %s is not in the %s folder".formatted(tableRecipe.id(),
                        ProcessingTableRecipesProvider.PROCESSING_TABLE_RECIPE_FOLDER));
                return;
            }
            var assemblerId = MI.id(ProcessingTableRecipesProvider.MACHINE_RECIPE_FOLDER
                    + path.substring(ProcessingTableRecipesProvider.PROCESSING_TABLE_RECIPE_FOLDER.length()));

            var holder = assembler.getRecipesWithoutCache(level).stream()
                    .filter(candidate -> candidate.id().equals(assemblerId))
                    .findFirst()
                    .orElse(null);
            if (holder == null) {
                helper.fail("Processing Table recipe %s has no assembler recipe %s".formatted(tableRecipe.id(), assemblerId));
                return;
            }

            var recipe = holder.value();
            if (recipe.itemInputs.size() != 3) {
                helper.fail("recipe %s should take exactly three inputs, it takes %d".formatted(holder.id(), recipe.itemInputs.size()));
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
