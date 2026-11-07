package aztech.milite.test;

import aztech.milite.config.ConfigMigration;
import aztech.milite.inventory.FilledItemStorage;
import aztech.milite.test.framework.MIGameTest;
import aztech.milite.test.framework.MIGameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.io.IOException;
import java.nio.file.Files;

public class UpstreamCompatTests {
    @MIGameTest
    public void thirdPartyInventoryFullnessRespectsSlotLimits(MIGameTestHelper helper) {
        var inventory = new ItemStackHandler(2) {
            @Override
            public int getSlotLimit(int slot) {
                return 64;
            }
        };
        inventory.setStackInSlot(0, new ItemStack(Items.STONE, 64));
        inventory.setStackInSlot(1, new ItemStack(Items.DIRT, 64));
        helper.assertTrue(FilledItemStorage.isFull(inventory), "full mixed inventory");
        inventory.setStackInSlot(1, new ItemStack(Items.ENDER_PEARL, 15));
        helper.assertTrue(!FilledItemStorage.isFull(inventory), "partial pearl stack accepts another item");
        inventory.setStackInSlot(1, ItemStack.EMPTY);
        helper.assertTrue(!FilledItemStorage.isFull(inventory), "empty slot accepts items");

        var limited = new ItemStackHandler(1) {
            @Override
            public int getSlotLimit(int slot) {
                return 8;
            }
        };
        limited.setStackInSlot(0, new ItemStack(Items.STONE, 8));
        helper.assertTrue(FilledItemStorage.isFull(limited), "custom slot limit");

        var oversized = new ItemStackHandler(1) {
            @Override
            public int getSlotLimit(int slot) {
                return 1024;
            }
        };
        oversized.setStackInSlot(0, new ItemStack(Items.STONE, 64));
        helper.assertTrue(!FilledItemStorage.isFull(oversized), "oversized inventory can exceed item stack limits");

        var nativeInventory = new NativeFullInventory();
        helper.assertTrue(FilledItemStorage.isFull(nativeInventory), "native fullness fast path");
        helper.succeed();
    }

    @MIGameTest
    public void legacyFtbConfigPreservesBothSettings(MIGameTestHelper helper) throws IOException {
        var directory = Files.createTempDirectory("moderntech-ftb-config-");
        var config = directory.resolve("moderntech-startup.toml");
        try {
            helper.assertTrue(ConfigMigration.ftbQuestsDefault(config), "new installations default to enabled");
            Files.writeString(config, "[compat]\nftbQuestsIntegration = false\n");
            helper.assertTrue(!ConfigMigration.ftbQuestsDefault(config), "legacy disabled setting");
            Files.writeString(config, "[compat]\nftbQuestsIntegration = true\n");
            helper.assertTrue(ConfigMigration.ftbQuestsDefault(config), "legacy enabled setting");
            Files.writeString(config, "[compat]\nalmostUnifiedIntegration = true\n");
            helper.assertTrue(ConfigMigration.ftbQuestsDefault(config), "missing legacy key defaults to enabled");
        } finally {
            Files.deleteIfExists(config);
            Files.deleteIfExists(directory);
        }
        helper.succeed();
    }

    @MIGameTest
    public void ftbMultiblockTaskRoundTripsAndRequiresBuilding(MIGameTestHelper helper) {
        if (ModList.get().isLoaded("ftbquests")) {
            FTBQuestsTests.validate(helper);
        }
        helper.succeed();
    }

    private static class NativeFullInventory extends ItemStackHandler implements FilledItemStorage {
        @Override
        public boolean isFull() {
            return true;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            throw new AssertionError("Native fullness must not scan slots");
        }
    }
}
