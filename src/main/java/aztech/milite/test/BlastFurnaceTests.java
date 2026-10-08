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

package aztech.milite.test;

import aztech.milite.MI;
import aztech.milite.compat.rei.machines.ReiMachineRecipes;
import aztech.milite.inventory.ConfigurableItemStack;
import aztech.milite.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import aztech.milite.machines.components.CrafterComponent;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.init.MultiblockMachines;
import aztech.milite.test.framework.MIGameTest;
import aztech.milite.test.framework.MIGameTestHelper;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import java.util.HashSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

public class BlastFurnaceTests {
    private static final BlockPos POS = new BlockPos(1, 1, 1);

    /**
     * The level of an electric blast furnace is a property of the block, not of anything inside the structure, so each
     * level has to be a machine of its own.
     */
    @MIGameTest
    public void blastFurnacesHaveTheirOwnLevel(MIGameTestHelper helper) {
        var levels = new HashSet<MachineLevel>();

        for (var tier : ElectricBlastFurnaceBlockEntity.tiers) {
            if (!levels.add(tier.level())) {
                helper.fail("two blast furnaces run the same level of recipe: " + tier.level());
            }

            var definition = MultiblockMachines.ELECTRIC_BLAST_FURNACES.get(tier.level());
            if (definition == null) {
                helper.fail("no blast furnace block is registered for level " + tier.level());
            }

            helper.setBlock(POS, definition.asBlock());
            var furnace = (ElectricBlastFurnaceBlockEntity) helper.getBlockEntity(POS);
            if (furnace.getMachineLevel() != tier.level()) {
                helper.fail("%s runs %s recipes, but it should run %s ones"
                        .formatted(tier.id(), furnace.getMachineLevel(), tier.level()));
            }
            helper.setBlock(POS, Blocks.AIR);
        }

        helper.succeed();
    }

    /**
     * All three furnaces share one recipe type and are told apart by the level of a recipe, which means the level is the
     * only thing standing between a furnace and a recipe it is too weak for. A lower furnace has to refuse the recipes
     * of a higher one, and a higher one has to take over the recipes of every furnace below it.
     */
    @MIGameTest
    public void lowerBlastFurnaceRefusesHigherRecipes(MIGameTestHelper helper) {
        var recipes = helper.getLevel().getRecipeManager().getAllRecipesFor(MIMachineRecipeTypes.BLAST_FURNACE);
        if (recipes.isEmpty()) {
            helper.fail("no blast furnace recipe is loaded, so this test cannot prove anything");
        }
        if (recipes.stream().noneMatch(recipe -> recipe.value().level == MachineLevel.IRIDIUM)) {
            helper.fail("no iridium level blast furnace recipe is loaded, so the lower furnaces have nothing to refuse");
        }

        for (var tier : ElectricBlastFurnaceBlockEntity.tiers) {
            helper.setBlock(POS, MultiblockMachines.ELECTRIC_BLAST_FURNACES.get(tier.level()).asBlock());
            var furnace = (ElectricBlastFurnaceBlockEntity) helper.getBlockEntity(POS);

            for (var holder : recipes) {
                boolean shouldRefuse = !tier.level().canCraft(holder.value().level);
                if (furnace.banRecipe(holder.value()) != shouldRefuse) {
                    helper.fail("%s should %s %s, which needs the %s level"
                            .formatted(tier.id(), shouldRefuse ? "refuse" : "accept", holder.id(), holder.value().level));
                }
            }
            helper.setBlock(POS, Blocks.AIR);
        }

        helper.succeed();
    }

    /**
     * The three furnaces must not each get a viewer category of their own: they run the same recipe type and only
     * differ by the level they reach, so three categories would be the same recipe list three times over. One category
     * covers all three, and the steam blast furnace stays out of it.
     */
    @MIGameTest
    public void blastFurnacesShareOneViewerCategory(MIGameTestHelper helper) {
        var electric = MI.id("electric_blast_furnace");
        if (!ReiMachineRecipes.categories.containsKey(electric)) {
            helper.fail("no viewer category is registered for the electric blast furnaces");
        }

        for (var tier : ElectricBlastFurnaceBlockEntity.tiers) {
            var id = MI.id(tier.id());
            // The shared category is named after the first furnace, so that one id is expected to be a category.
            if (!id.equals(electric) && ReiMachineRecipes.categories.containsKey(id)) {
                helper.fail("%s has a viewer category of its own, it should share the electric one".formatted(tier.id()));
            }
            var categories = ReiMachineRecipes.machineToClickAreaCategory.get(id);
            if (categories == null || categories.stream().noneMatch(entry -> entry.category.equals(electric))) {
                helper.fail("%s is not a workstation of the electric blast furnace category".formatted(tier.id()));
            }
        }

        // The steam blast furnace is a different machine and keeps its own category.
        if (!ReiMachineRecipes.categories.containsKey(MI.id("steam_blast_furnace"))) {
            helper.fail("the steam blast furnace lost its own viewer category");
        }

        helper.succeed();
    }

    /**
     * An electric furnace takes over the recipes of the steam one, so the handful of recipes that only the steam furnace
     * knows are not a reason to keep a steam furnace standing next to an electric one. A steam recipe states no level,
     * so it counts as a bronze one, and no electric furnace is ever too weak for it.
     */
    @MIGameTest
    public void electricBlastFurnacesRunTheSteamRecipes(MIGameTestHelper helper) {
        var steamRecipes = helper.getLevel().getRecipeManager().getAllRecipesFor(MIMachineRecipeTypes.STEAM_BLAST_FURNACE);
        if (steamRecipes.isEmpty()) {
            helper.fail("no steam blast furnace recipe is loaded, so this test cannot prove anything");
        }

        for (var tier : ElectricBlastFurnaceBlockEntity.tiers) {
            helper.setBlock(POS, MultiblockMachines.ELECTRIC_BLAST_FURNACES.get(tier.level()).asBlock());
            var furnace = (ElectricBlastFurnaceBlockEntity) helper.getBlockEntity(POS);

            if (!furnace.recipeTypes().contains(MIMachineRecipeTypes.STEAM_BLAST_FURNACE)) {
                helper.fail("%s does not run the steam blast furnace recipes".formatted(tier.id()));
            }

            for (var holder : steamRecipes) {
                if (furnace.banRecipe(holder.value())) {
                    helper.fail("%s refuses %s, which is a steam recipe".formatted(tier.id(), holder.id()));
                }
            }

            helper.setBlock(POS, Blocks.AIR);
        }

        helper.succeed();
    }

    /**
     * Running the steam recipes takes more than declaring the type: the furnace has to find such a recipe when it looks
     * for something to start, which is the lookup it does whenever its contents change.
     */
    @MIGameTest
    public void electricBlastFurnaceFindsASteamRecipe(MIGameTestHelper helper) {
        var steamRecipe = helper.getLevel().getRecipeManager().getAllRecipesFor(MIMachineRecipeTypes.STEAM_BLAST_FURNACE).stream()
                .filter(holder -> holder.value().itemInputs.size() == 1)
                .filter(holder -> !holder.value().itemInputs.getFirst().getInputItems().isEmpty())
                .findFirst().orElse(null);
        if (steamRecipe == null) {
            helper.fail("no steam blast furnace recipe with a single item input is loaded, so this test cannot prove anything");
        }

        var input = steamRecipe.value().itemInputs.getFirst();
        var stack = new ConfigurableItemStack();
        stack.setKey(ItemVariant.of(input.getInputItems().getFirst()));
        stack.setAmount(Integer.MAX_VALUE);

        helper.setBlock(POS, MultiblockMachines.ELECTRIC_BLAST_FURNACES.get(MachineLevel.ELECTRIC).asBlock());
        var furnace = (ElectricBlastFurnaceBlockEntity) helper.getBlockEntity(POS);

        var found = CrafterComponent.getRecipes(helper.getLevel(), furnace.recipeTypes(), List.of(stack));
        if (found.stream().noneMatch(holder -> holder.id().equals(steamRecipe.id()))) {
            helper.fail("%s is a steam recipe, but the electric furnace did not find it".formatted(steamRecipe.id()));
        }

        helper.succeed();
    }

    /**
     * A recipe the electric furnaces can run has to be listed under them in the viewer as well, otherwise the recipe
     * only ever points at the steam furnace that a player no longer needs.
     */
    @MIGameTest
    public void steamCategoryListsTheElectricFurnaces(MIGameTestHelper helper) {
        var steam = MI.id("steam_blast_furnace");
        if (!ReiMachineRecipes.categories.containsKey(steam)) {
            helper.fail("no viewer category is registered for the steam blast furnace");
        }
        var workstations = ReiMachineRecipes.categories.get(steam).workstations;
        if (!workstations.contains(steam)) {
            helper.fail("the steam blast furnace is not a workstation of its own category");
        }

        for (var tier : ElectricBlastFurnaceBlockEntity.tiers) {
            var id = MI.id(tier.id());
            if (!workstations.contains(id)) {
                helper.fail("%s runs the steam recipes, but it is not a workstation of the steam category".formatted(tier.id()));
            }
            var categories = ReiMachineRecipes.machineToClickAreaCategory.get(id);
            if (categories == null || categories.stream().noneMatch(entry -> entry.category.equals(steam))) {
                helper.fail("%s does not point at the steam blast furnace category".formatted(tier.id()));
            }
            // Filling a machine from a recipe in the viewer looks that recipe up in the categories the machine is
            // registered under, so each of them has to name a category that is really registered.
            for (var entry : categories) {
                if (!ReiMachineRecipes.categories.containsKey(entry.category)) {
                    helper.fail("%s is registered under %s, which is not a registered category".formatted(tier.id(), entry.category));
                }
            }
        }

        helper.succeed();
    }
}
