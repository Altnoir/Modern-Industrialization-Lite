package aztech.milite.test;

import aztech.milite.compat.rei.machines.ReiMachineRecipes;
import aztech.milite.inventory.ConfigurableItemStack;
import aztech.milite.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import aztech.milite.machines.components.CrafterComponent;
import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.init.MultiblockMachines;
import aztech.milite.MI;
import aztech.milite.test.framework.MIGameTest;
import aztech.milite.test.framework.MIGameTestHelper;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;
import java.util.List;

public class BlastFurnaceTests {
    private static final BlockPos POS = new BlockPos(1, 1, 1);

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

    @MIGameTest
    public void blastFurnacesShareOneViewerCategory(MIGameTestHelper helper) {
        var electric = MI.id("electric_blast_furnace");
        if (!ReiMachineRecipes.categories.containsKey(electric)) {
            helper.fail("no viewer category is registered for the electric blast furnaces");
        }

        for (var tier : ElectricBlastFurnaceBlockEntity.tiers) {
            var id = MI.id(tier.id());
            if (!id.equals(electric) && ReiMachineRecipes.categories.containsKey(id)) {
                helper.fail("%s has a viewer category of its own, it should share the electric one".formatted(tier.id()));
            }
            var categories = ReiMachineRecipes.machineToClickAreaCategory.get(id);
            if (categories == null || categories.stream().noneMatch(entry -> entry.category.equals(electric))) {
                helper.fail("%s is not a workstation of the electric blast furnace category".formatted(tier.id()));
            }
        }

        if (!ReiMachineRecipes.categories.containsKey(MI.id("steam_blast_furnace"))) {
            helper.fail("the steam blast furnace lost its own viewer category");
        }

        helper.succeed();
    }

    @MIGameTest
    public void electricBlastFurnacesIgnoreTheSteamRecipes(MIGameTestHelper helper) {
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

        for (var tier : ElectricBlastFurnaceBlockEntity.tiers) {
            helper.setBlock(POS, MultiblockMachines.ELECTRIC_BLAST_FURNACES.get(tier.level()).asBlock());
            var furnace = (ElectricBlastFurnaceBlockEntity) helper.getBlockEntity(POS);

            if (furnace.recipeTypes().contains(MIMachineRecipeTypes.STEAM_BLAST_FURNACE)) {
                helper.fail("%s declares the steam blast furnace recipes".formatted(tier.id()));
            }

            var found = CrafterComponent.getRecipes(helper.getLevel(), furnace.recipeTypes(), List.of(stack));
            if (found.stream().anyMatch(holder -> holder.id().equals(steamRecipe.id()))) {
                helper.fail("%s found %s, which is a steam recipe".formatted(tier.id(), steamRecipe.id()));
            }

            helper.setBlock(POS, Blocks.AIR);
        }

        helper.succeed();
    }

    @MIGameTest
    public void steamCategoryKeepsTheElectricFurnacesOut(MIGameTestHelper helper) {
        var steam = MI.id("steam_blast_furnace");
        var electric = MI.id("electric_blast_furnace");
        if (!ReiMachineRecipes.categories.containsKey(steam)) {
            helper.fail("no viewer category is registered for the steam blast furnace");
        }
        var workstations = ReiMachineRecipes.categories.get(steam).workstations;
        if (workstations.size() != 1 || !workstations.contains(steam)) {
            helper.fail("the steam category lists %s as its workstations, it should list only the steam furnace"
                    .formatted(workstations));
        }

        for (var tier : ElectricBlastFurnaceBlockEntity.tiers) {
            var id = MI.id(tier.id());
            if (workstations.contains(id)) {
                helper.fail("%s does not run the steam recipes, but it is a workstation of the steam category".formatted(tier.id()));
            }

            var categories = ReiMachineRecipes.machineToClickAreaCategory.get(id);
            if (categories == null || categories.size() != 1 || !categories.getFirst().category.equals(electric)) {
                helper.fail("%s opens %s, it should open the electric blast furnace category alone".formatted(tier.id(), categories));
            }
        }

        helper.succeed();
    }
}
