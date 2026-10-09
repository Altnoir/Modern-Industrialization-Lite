package aztech.milite.test;

import aztech.milite.MI;
import aztech.milite.blocks.processingtable.ProcessingTableScreenHandler;
import aztech.milite.materials.MIMaterials;
import aztech.milite.materials.part.MIParts;
import aztech.milite.test.framework.MIGameTest;
import aztech.milite.test.framework.MIGameTestHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

/**
 * The Processing Table adds its four slots after the 36 player slots, so quick move has to use those indices and not
 * the usual "container slots first" ones. Its two side slots also stay locked until the key slot holds a casing.
 */
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

    @MIGameTest
    public void casingUnlocksTheSideSlots(MIGameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var menu = new ProcessingTableScreenHandler(0, player.getInventory());

        Item casing = MIMaterials.BRONZE.getPart(MIParts.MACHINE_CASING).asItem();
        Item drillHead = MIMaterials.BRONZE.getPart(MIParts.DRILL_HEAD).asItem();
        Item barrel = MIMaterials.BRONZE.getPart(MIParts.BARREL).asItem();

        if (menu.leftInput.mayPlace(new ItemStack(drillHead)) || menu.rightInput.mayPlace(new ItemStack(barrel))) {
            helper.fail("the side slots accepted items before the key slot held a casing");
        }

        menu.keyInput.set(new ItemStack(casing));
        if (!menu.areSideSlotsUnlocked()) {
            helper.fail("the side slots stayed locked with a bronze machine casing in the key slot");
        }

        menu.leftInput.set(new ItemStack(drillHead));
        menu.rightInput.set(new ItemStack(barrel));
        ItemStack macerator = menu.output.getItem().copy();
        if (!macerator.is(BuiltInRegistries.ITEM.get(MI.id("bronze_macerator")))) {
            helper.fail("the bronze macerator recipe produced %s".formatted(macerator));
        }

        // The two characterising items are not ordered.
        menu.leftInput.set(new ItemStack(barrel));
        menu.rightInput.set(new ItemStack(drillHead));
        if (!ItemStack.isSameItemSameComponents(menu.output.getItem(), macerator)) {
            helper.fail("swapping the two side items produced %s".formatted(menu.output.getItem()));
        }

        // Taking the casing back out relocks the sides and hands their contents to the player.
        for (int index = 0; index < player.getInventory().getContainerSize(); index++) {
            player.getInventory().setItem(index, ItemStack.EMPTY);
        }
        menu.keyInput.set(ItemStack.EMPTY);
        if (menu.areSideSlotsUnlocked()) {
            helper.fail("the side slots stayed unlocked after the casing was taken out");
        }
        if (menu.leftInput.hasItem() || menu.rightInput.hasItem()) {
            helper.fail("the relocked side slots kept their contents");
        }
        if (!player.getInventory().getItem(0).is(barrel) && !player.getInventory().getItem(0).is(drillHead)) {
            helper.fail("the contents of the relocked side slots did not reach the player inventory, slot 0 holds %s"
                    .formatted(player.getInventory().getItem(0)));
        }

        helper.succeed();
    }
}
