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

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.init.MachineLevel;
import aztech.modern_industrialization.machines.init.MultiblockMachines;
import aztech.modern_industrialization.test.framework.MIGameTest;
import aztech.modern_industrialization.test.framework.MIGameTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;

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
}
