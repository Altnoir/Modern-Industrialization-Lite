package aztech.milite.test;

import aztech.milite.MI;
import aztech.milite.blocks.processingtable.ProcessingTableRecipe;
import aztech.milite.blocks.processingtable.ProcessingTableScreenHandler;
import aztech.milite.materials.MIMaterials;
import aztech.milite.materials.part.MIParts;
import aztech.milite.test.framework.MIGameTest;
import aztech.milite.test.framework.MIGameTestHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ProcessingTableTests {
    @MIGameTest
    public void quickMoveFillsTheInputSlots(MIGameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var menu = new ProcessingTableScreenHandler(0, player.getInventory());

        menu.getSlot(0).set(new ItemStack(Items.STONE, 8));
        menu.quickMoveStack(player, 0);

        if (menu.getSlot(0).hasItem()) {
            helper.fail("shift clicking a player slot left %d items behind in the inventory"
                    .formatted(menu.getSlot(0).getItem().getCount()));
        }
        if (menu.keyInput.getItem().getCount() != 8) {
            helper.fail("shift clicking a player slot put %s in the key slot instead of 8 stone"
                    .formatted(menu.keyInput.getItem()));
        }

        menu.quickMoveStack(player, menu.keyInput.index);

        if (menu.keyInput.hasItem()) {
            helper.fail("shift clicking the key slot left %s behind".formatted(menu.keyInput.getItem()));
        }
        if (menu.getSlot(0).getItem().getCount() != 8) {
            helper.fail("shift clicking the key slot returned %s to the inventory instead of 8 stone"
                    .formatted(menu.getSlot(0).getItem()));
        }

        helper.succeed();
    }

    private static List<ItemStack> emptyGrid() {
        List<ItemStack> grid = new ArrayList<>(ProcessingTableRecipe.SLOT_COUNT);
        for (int slot = 0; slot < ProcessingTableRecipe.SLOT_COUNT; slot++) {
            grid.add(ItemStack.EMPTY);
        }
        return grid;
    }

    private static ProcessingTableScreenHandler menuWithTestRecipes(Player player) {
        var fourParts = ProcessingTableRecipe.fromSlots(Ingredient.of(Items.IRON_INGOT),
                new TreeMap<>(Map.of(
                        1, Ingredient.of(Items.STONE),
                        3, Ingredient.of(Items.COBBLESTONE),
                        7, Ingredient.of(Items.DIRT),
                        9, Ingredient.of(Items.GRAVEL))),
                new ItemStack(Items.DIAMOND));
        var eightParts = ProcessingTableRecipe.fromSlots(Ingredient.of(Items.GOLD_INGOT),
                new TreeMap<>(Map.of(
                        1, Ingredient.of(Items.SAND),
                        2, Ingredient.of(Items.OAK_LOG),
                        3, Ingredient.of(Items.COAL),
                        4, Ingredient.of(Items.APPLE),
                        6, Ingredient.of(Items.BREAD),
                        7, Ingredient.of(Items.WHEAT),
                        8, Ingredient.of(Items.BONE),
                        9, Ingredient.of(Items.STRING))),
                new ItemStack(Items.EMERALD));
        var recipes = List.of(new RecipeHolder<>(MI.id("test/four_parts"), fourParts),
                new RecipeHolder<>(MI.id("test/eight_parts"), eightParts));

        return new ProcessingTableScreenHandler(0, player.getInventory()) {
            @Override
            protected List<RecipeHolder<ProcessingTableRecipe>> recipes() {
                return recipes;
            }
        };
    }

    @MIGameTest
    public void casingUnlocksItsOwnCells(MIGameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var menu = new ProcessingTableScreenHandler(0, player.getInventory());

        Item casing = MIMaterials.BRONZE.getPart(MIParts.MACHINE_CASING).asItem();
        Item drillHead = MIMaterials.BRONZE.getPart(MIParts.DRILL_HEAD).asItem();
        Item barrel = MIMaterials.BRONZE.getPart(MIParts.BARREL).asItem();

        for (int slot = ProcessingTableRecipe.MIN_SLOT; slot <= ProcessingTableRecipe.MAX_SLOT; slot++) {
            if (menu.isGridSlotActive(slot)) {
                helper.fail("cell %d accepted items before the key slot held a casing".formatted(slot));
            }
        }

        menu.keyInput.set(new ItemStack(casing));
        for (int slot = ProcessingTableRecipe.MIN_SLOT; slot <= ProcessingTableRecipe.MAX_SLOT; slot++) {
            boolean expected = slot == 4 || slot == 5 || slot == 6;
            if (menu.isGridSlotActive(slot) != expected) {
                helper.fail("the bronze machine recipes %s cell %d"
                        .formatted(expected ? "did not unlock" : "unlocked", slot));
            }
        }

        menu.gridInputs.get(4).set(new ItemStack(drillHead));
        ItemStack macerator = menu.output.getItem().copy();
        if (!macerator.is(BuiltInRegistries.ITEM.get(MI.id("bronze_macerator")))) {
            helper.fail("the bronze macerator recipe produced %s".formatted(macerator));
        }

        menu.gridInputs.get(4).set(new ItemStack(barrel));
        if (!menu.output.getItem().isEmpty()) {
            helper.fail("the bronze macerator recipe still produced %s with the wrong part in its cell"
                    .formatted(menu.output.getItem()));
        }

        helper.succeed();
    }

    @MIGameTest
    public void partsCanSwapWithinRecipeCells(MIGameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var menu = menuWithTestRecipes(player);

        menu.keyInput.set(new ItemStack(Items.IRON_INGOT));
        menu.gridInputs.get(0).set(new ItemStack(Items.COBBLESTONE));
        menu.gridInputs.get(2).set(new ItemStack(Items.STONE));
        menu.gridInputs.get(6).set(new ItemStack(Items.DIRT));
        menu.gridInputs.get(8).set(new ItemStack(Items.GRAVEL));

        if (!menu.output.getItem().is(Items.DIAMOND)) {
            helper.fail("the recipe did not produce a diamond with its first two parts swapped");
        }

        menu.gridInputs.get(0).set(new ItemStack(Items.STONE));
        menu.gridInputs.get(2).set(new ItemStack(Items.COBBLESTONE));
        if (!menu.output.getItem().is(Items.DIAMOND)) {
            helper.fail("the recipe produced %s once every part sat in its own cell".formatted(menu.output.getItem()));
        }

        helper.succeed();
    }

    @MIGameTest
    public void unusedCellsRejectPlacement(MIGameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var menu = menuWithTestRecipes(player);

        menu.keyInput.set(new ItemStack(Items.IRON_INGOT));

        if (!menu.gridInputs.get(0).mayPlace(new ItemStack(Items.STONE))) {
            helper.fail("cell 1 rejected an item the recipe asks for");
        }
        if (menu.gridInputs.get(1).mayPlace(new ItemStack(Items.STONE))) {
            helper.fail("cell 2 accepted an item the recipe does not ask for");
        }
        if (menu.gridInputs.get(4).mayPlace(new ItemStack(Items.STONE))) {
            helper.fail("cell 5 accepted an item the recipe does not ask for");
        }

        helper.succeed();
    }

    @MIGameTest
    public void removingTheKeyClearsTheCells(MIGameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var menu = menuWithTestRecipes(player);

        menu.keyInput.set(new ItemStack(Items.IRON_INGOT));
        menu.gridInputs.get(0).set(new ItemStack(Items.STONE));
        menu.gridInputs.get(2).set(new ItemStack(Items.COBBLESTONE));
        menu.gridInputs.get(6).set(new ItemStack(Items.DIRT));
        menu.gridInputs.get(8).set(new ItemStack(Items.GRAVEL));

        for (int index = 0; index < player.getInventory().getContainerSize(); index++) {
            player.getInventory().setItem(index, ItemStack.EMPTY);
        }
        menu.keyInput.set(ItemStack.EMPTY);

        for (Slot cell : menu.gridInputs) {
            if (cell.isActive()) {
                helper.fail("a cell stayed visible after the key was taken out");
            }
            if (cell.hasItem()) {
                helper.fail("a hidden cell kept its contents");
            }
        }

        int returned = 0;
        for (int index = 0; index < player.getInventory().getContainerSize(); index++) {
            returned += player.getInventory().getItem(index).getCount();
        }
        if (returned != 4) {
            helper.fail("the contents of the hidden cells handed %d items back instead of 4".formatted(returned));
        }

        helper.succeed();
    }

    @MIGameTest
    public void matchingIgnoresNeitherCellNorEmptySiblings(MIGameTestHelper helper) {
        SortedMap<Integer, Ingredient> slots = new TreeMap<>(Map.of(
                1, Ingredient.of(Items.STONE),
                9, Ingredient.of(Items.COBBLESTONE)));
        var recipe = ProcessingTableRecipe.fromSlots(Ingredient.of(Items.IRON_INGOT), slots, new ItemStack(Items.DIAMOND));

        List<ItemStack> grid = emptyGrid();
        grid.set(0, new ItemStack(Items.STONE));
        grid.set(8, new ItemStack(Items.COBBLESTONE));
        if (!recipe.matches(new ItemStack(Items.IRON_INGOT), grid)) {
            helper.fail("the recipe no longer matches the cells it asks for");
        }

        grid.set(0, new ItemStack(Items.COBBLESTONE));
        grid.set(8, new ItemStack(Items.STONE));
        if (!recipe.matches(new ItemStack(Items.IRON_INGOT), grid)) {
            helper.fail("the recipe did not match when its parts exchanged cells");
        }

        grid.set(0, new ItemStack(Items.STONE));
        grid.set(8, new ItemStack(Items.COBBLESTONE));
        grid.set(4, new ItemStack(Items.DIRT));
        if (recipe.matches(new ItemStack(Items.IRON_INGOT), grid)) {
            helper.fail("the recipe matched even though a cell it does not use held an item");
        }

        var overlapping = ProcessingTableRecipe.fromSlots(Ingredient.of(Items.IRON_INGOT),
                new TreeMap<>(Map.of(1, Ingredient.of(Items.STONE, Items.COBBLESTONE),
                        9, Ingredient.of(Items.STONE))), new ItemStack(Items.DIAMOND));
        grid.set(4, ItemStack.EMPTY);
        grid.set(0, new ItemStack(Items.STONE));
        grid.set(8, new ItemStack(Items.COBBLESTONE));
        if (!overlapping.matches(new ItemStack(Items.IRON_INGOT), grid)) {
            helper.fail("overlapping ingredients must be assigned without consuming a required item twice");
        }
        grid.set(0, new ItemStack(Items.COBBLESTONE));
        if (overlapping.matches(new ItemStack(Items.IRON_INGOT), grid)) {
            helper.fail("two cobblestone cannot replace the required stone");
        }

        helper.succeed();
    }
}
