package aztech.milite.test;

import aztech.milite.compat.rei.machines.ReiMachineRecipes;
import aztech.milite.inventory.ConfigurableItemStack;
import aztech.milite.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import aztech.milite.machines.components.ActiveShapeComponent;
import aztech.milite.machines.components.CrafterComponent;
import aztech.milite.machines.guicomponents.ShapeSelection;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.init.MultiblockMachines;
import aztech.milite.MI;
import aztech.milite.test.framework.MIGameTest;
import aztech.milite.test.framework.MIGameTestHelper;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.List;

public class BlastFurnaceTests {
    private static final BlockPos POS = new BlockPos(1, 1, 1);

    private static ElectricBlastFurnaceBlockEntity place(MIGameTestHelper helper) {
        helper.setBlock(POS, MultiblockMachines.ELECTRIC_BLAST_FURNACE.asBlock());
        return helper.getBlockEntity(POS);
    }

    private static ActiveShapeComponent shape(ElectricBlastFurnaceBlockEntity furnace) {
        return furnace.components.getNullable(ActiveShapeComponent.class);
    }

    @MIGameTest
    public void theSliderSelectsTheTierOfTheFurnace(MIGameTestHelper helper) {
        var levels = new HashSet<MachineLevel>();
        var coils = new HashSet<Object>();
        for (var tier : ElectricBlastFurnaceBlockEntity.tiers) {
            if (!levels.add(tier.level())) {
                helper.fail("two tiers run the same level of recipe: " + tier.level());
                return;
            }
            if (!coils.add(tier.coilBlockId())) {
                helper.fail("two tiers are built with the same coil: " + tier.coilBlockId());
                return;
            }
        }

        var furnace = place(helper);
        var slider = furnace.guiComponents.getNullable(ShapeSelection.class);
        if (slider == null) {
            helper.fail("the electric blast furnace has no tier slider");
            return;
        }

        for (int i = 0; i < ElectricBlastFurnaceBlockEntity.tiers.size(); ++i) {
            var tier = ElectricBlastFurnaceBlockEntity.tiers.get(i);
            shape(furnace).setShape(furnace, i);

            if (slider.behavior.getCurrentIndex(0) != i) {
                helper.fail("the slider shows tier %d, it should show %d".formatted(slider.behavior.getCurrentIndex(0), i));
                return;
            }
            if (furnace.getMachineLevel() != tier.level()) {
                helper.fail("%s runs %s recipes, but it should run %s ones"
                        .formatted(tier.id(), furnace.getMachineLevel(), tier.level()));
                return;
            }
        }

        helper.succeed();
    }

    @MIGameTest
    public void lowerTiersRefuseHigherRecipes(MIGameTestHelper helper) {
        var recipes = helper.getLevel().getRecipeManager().getAllRecipesFor(MIMachineRecipeTypes.BLAST_FURNACE);
        if (recipes.isEmpty()) {
            helper.fail("no blast furnace recipe is loaded, so this test cannot prove anything");
            return;
        }
        if (recipes.stream().noneMatch(recipe -> recipe.value().level == MachineLevel.IRIDIUM)) {
            helper.fail("no iridium level blast furnace recipe is loaded, so the lower tiers have nothing to refuse");
            return;
        }

        var furnace = place(helper);
        for (int i = 0; i < ElectricBlastFurnaceBlockEntity.tiers.size(); ++i) {
            var tier = ElectricBlastFurnaceBlockEntity.tiers.get(i);
            shape(furnace).setShape(furnace, i);

            for (var holder : recipes) {
                boolean shouldRefuse = !tier.level().canCraft(holder.value().level);
                if (furnace.banRecipe(holder.value()) != shouldRefuse) {
                    helper.fail("%s should %s %s, which needs the %s level"
                            .formatted(tier.id(), shouldRefuse ? "refuse" : "accept", holder.id(), holder.value().level));
                    return;
                }
            }
        }

        helper.succeed();
    }

    @MIGameTest
    public void allTiersShareOneViewerCategory(MIGameTestHelper helper) {
        var electric = MI.id("electric_blast_furnace");
        if (!ReiMachineRecipes.categories.containsKey(electric)) {
            helper.fail("no viewer category is registered for the electric blast furnace");
            return;
        }

        var categories = ReiMachineRecipes.machineToClickAreaCategory.get(electric);
        if (categories == null || categories.size() != 1 || !categories.getFirst().category.equals(electric)) {
            helper.fail("%s opens %s, it should open its own category alone".formatted(electric, categories));
            return;
        }

        long shapes = ReiMachineRecipes.multiblockShapes.stream()
                .filter(shape -> shape.machine().equals(electric))
                .count();
        if (shapes != ElectricBlastFurnaceBlockEntity.tiers.size()) {
            helper.fail("the electric blast furnace registers %d shapes, it should register one per tier (%d)"
                    .formatted(shapes, ElectricBlastFurnaceBlockEntity.tiers.size()));
            return;
        }

        var steam = MI.id("steam_blast_furnace");
        if (!ReiMachineRecipes.categories.containsKey(steam)) {
            helper.fail("no viewer category is registered for the steam blast furnace");
            return;
        }
        var workstations = ReiMachineRecipes.categories.get(steam).workstations;
        if (workstations.size() != 1 || !workstations.contains(steam) || workstations.contains(electric)) {
            helper.fail("the steam category lists %s as its workstations, it should list only the steam furnace"
                    .formatted(workstations));
            return;
        }

        helper.succeed();
    }

    @MIGameTest
    public void electricBlastFurnaceIgnoresTheSteamRecipes(MIGameTestHelper helper) {
        var steamRecipe = helper.getLevel().getRecipeManager().getAllRecipesFor(MIMachineRecipeTypes.STEAM_BLAST_FURNACE).stream()
                .filter(holder -> holder.value().itemInputs.size() == 1)
                .filter(holder -> !holder.value().itemInputs.getFirst().getInputItems().isEmpty())
                .findFirst().orElse(null);
        if (steamRecipe == null) {
            helper.fail("no steam blast furnace recipe with a single item input is loaded, so this test cannot prove anything");
            return;
        }

        var input = steamRecipe.value().itemInputs.getFirst();
        var stack = new ConfigurableItemStack();
        stack.setKey(ItemVariant.of(input.getInputItems().getFirst()));
        stack.setAmount(Integer.MAX_VALUE);

        var furnace = place(helper);
        if (furnace.recipeTypes().contains(MIMachineRecipeTypes.STEAM_BLAST_FURNACE)) {
            helper.fail("the electric blast furnace declares the steam blast furnace recipes");
            return;
        }

        var found = CrafterComponent.getRecipes(helper.getLevel(), furnace.recipeTypes(), List.of(stack));
        if (found.stream().anyMatch(holder -> holder.id().equals(steamRecipe.id()))) {
            helper.fail("the electric blast furnace found %s, which is a steam recipe".formatted(steamRecipe.id()));
            return;
        }

        helper.succeed();
    }
}
