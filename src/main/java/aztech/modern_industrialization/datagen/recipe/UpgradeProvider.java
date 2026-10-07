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

package aztech.modern_industrialization.datagen.recipe;

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.init.MachineLevel;
import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;

import java.util.List;

/**
 * Recipes that turn one tier of gear into the next.
 *
 * <p>
 * This used to also generate the steel upgrade conversions, where a bronze machine plus an upgrade item became the steel
 * machine. Machines exist at every level on their own now, so those recipes are gone and only the quantum armour
 * conversions remain.
 */
public class UpgradeProvider extends MIRecipesProvider {
    public UpgradeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        buildQuantumArmour(consumer);
    }

    private static final List<String> QUANTUM_ITEMS = List.of("helmet", "chestplate", "leggings", "boots", "sword");

    /**
     * Quantum armour is the top of the armour ladder, so it is only craftable once an iridium packer exists.
     */
    private void buildQuantumArmour(RecipeOutput consumer) {
        for (var itemType : QUANTUM_ITEMS) {
            var packerRecipe = new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 1_000_000, 200)
                    .level(MachineLevel.IRIDIUM)
                    .addItemInput("minecraft:netherite_" + itemType, 1)
                    .addItemInput("milite:quantum_upgrade", 1)
                    .addItemOutput("milite:quantum_" + itemType, 1);
            packerRecipe.offerTo(consumer, "upgrade/packer/quantum/" + itemType);
        }
    }
}
