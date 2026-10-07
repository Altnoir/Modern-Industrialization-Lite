/*
 * MIT License
 *
 * Copyright (c) 2026 Altnoir Null々
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package aztech.modern_industrialization.test;

import aztech.modern_industrialization.MIRegistries;
import aztech.modern_industrialization.datagen.recipe.ProcessingTableRecipesProvider;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.test.framework.MIGameTest;
import aztech.modern_industrialization.test.framework.MIGameTestHelper;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeHolder;

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
