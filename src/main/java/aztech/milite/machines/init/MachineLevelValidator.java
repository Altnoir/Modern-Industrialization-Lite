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

package aztech.milite.machines.init;

import aztech.milite.MI;
import aztech.milite.config.MIStartupConfig;
import aztech.milite.machines.recipe.MachineRecipe;
import aztech.milite.machines.recipe.MachineRecipeType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.server.MinecraftServer;

/**
 * Reports machine recipes that no machine can ever craft.
 *
 * <p>
 * A recipe states the level it needs, and a machine states the level it is. When a recipe asks for a level above the
 * highest level its machine type reaches, nothing can run it and the recipe is dead weight. This cannot be caught by the
 * compiler because the two halves live in different places, so it is checked once the recipes are loaded.
 *
 * <p>
 * Controlled by the {@code validateRecipeLevels} startup config option. It only logs, it never removes or alters a
 * recipe.
 */
public final class MachineLevelValidator {
    private MachineLevelValidator() {}

    public static void validate(MinecraftServer server) {
        if (!MIStartupConfig.INSTANCE.validateRecipeLevels.getAsBoolean()) {
            return;
        }

        var recipeManager = server.getRecipeManager();
        // Group by level so the log stays readable when a whole tier is wrong.
        Map<MachineLevel, List<String>> unreachable = new TreeMap<>();

        for (MachineRecipeType type : MIMachineRecipeTypes.getRecipeTypes()) {
            MachineLevel highest = MachineRegistrationHelper.highestLevel(type);
            if (highest == null) {
                // Not a machine type with a level, for example a type only used by a multiblock.
                continue;
            }
            for (var holder : recipeManager.getAllRecipesFor(type)) {
                MachineRecipe recipe = holder.value();
                if (!highest.canCraft(recipe.level)) {
                    unreachable.computeIfAbsent(recipe.level, level -> new ArrayList<>())
                            .add(holder.id() + " needs " + recipe.level + " but " + type.getId() + " only reaches " + highest);
                }
            }
        }

        if (unreachable.isEmpty()) {
            MI.LOGGER.info("Machine level check: every machine recipe can be crafted by some machine.");
            return;
        }

        int total = unreachable.values().stream().mapToInt(List::size).sum();
        MI.LOGGER.warn("Machine level check: {} machine recipe(s) ask for a level no machine of their type reaches.", total);
        unreachable.forEach((level, recipes) -> {
            MI.LOGGER.warn("  {} recipe(s) need the {} level:", recipes.size(), level);
            recipes.forEach(recipe -> MI.LOGGER.warn("    {}", recipe));
        });
    }
}
