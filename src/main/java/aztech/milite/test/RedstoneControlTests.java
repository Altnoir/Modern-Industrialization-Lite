package aztech.milite.test;

import aztech.milite.MI;
import aztech.milite.machines.blockentities.AbstractCraftingMachineBlockEntity;
import aztech.milite.machines.components.CrafterComponent;
import aztech.milite.machines.components.RedstoneControlComponent;
import aztech.milite.test.framework.MIGameTest;
import aztech.milite.test.framework.MIGameTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;

public class RedstoneControlTests {
    private static final String RECIPE = "stone_to_cobblestone";
    private static final int MEASURE_TICKS = 40;
    private static final int STONE_PER_REFILL = 4;

    @MIGameTest(timeoutTicks = 400)
    public void redstoneControlStopsTheMachineUntilTheSignalArrives(MIGameTestHelper helper) {
        BlockPos pos = new BlockPos(0, 2, 0);
        Run run = start(helper, pos);
        run.component.setEnabled(run.machine, true);
        run.component.setRequiresLowSignal(run.machine, false);

        helper.startSequence()
                .thenIdle(MEASURE_TICKS)
                .thenExecute(() -> {
                    helper.assertValueEqual(run.crafts(), 0L, "crafts of a machine that requires a signal and has none");
                    helper.assertFalse(run.crafter.hasActiveRecipe(), "a machine that requires a signal and has none is inactive");
                    helper.setBlock(pos.east(), Blocks.REDSTONE_BLOCK);
                })
                .thenIdle(3)
                .thenExecute(() -> {
                    run.baseline = run.crafts();
                    helper.assertTrue(run.crafter.hasActiveRecipe() || run.baseline > 0, "the machine starts once the signal arrives");
                })
                .thenIdle(MEASURE_TICKS)
                .thenExecute(() -> helper.assertTrue(run.crafts() > run.baseline,
                        "crafts of a powered machine after the signal arrived, which was " + run.baseline))
                .thenSucceed();
    }

    @MIGameTest(timeoutTicks = 400)
    public void lowSignalInvertsTheControl(MIGameTestHelper helper) {
        BlockPos pos = new BlockPos(0, 2, 0);
        Run run = start(helper, pos);
        run.component.setEnabled(run.machine, true);
        run.component.setRequiresLowSignal(run.machine, true);
        helper.setBlock(pos.east(), Blocks.REDSTONE_BLOCK);

        helper.startSequence()
                .thenIdle(MEASURE_TICKS)
                .thenExecute(() -> {
                    helper.assertValueEqual(run.crafts(), 0L, "crafts of a machine that requires a low signal while the signal is high");
                    helper.setBlock(pos.east(), Blocks.AIR);
                })
                .thenIdle(3)
                .thenIdle(MEASURE_TICKS)
                .thenExecute(() -> helper.assertTrue(run.crafts() > 0,
                        "crafts of a machine that requires a low signal once the signal is gone"))
                .thenSucceed();
    }

    @MIGameTest(timeoutTicks = 200)
    public void redstoneStateRoundTripsAndMigratesTheOldModule(MIGameTestHelper helper) {
        Run run = start(helper, new BlockPos(0, 2, 0));
        var registries = helper.getLevel().registryAccess();

        run.component.setEnabled(run.machine, true);
        run.component.setRequiresLowSignal(run.machine, true);

        CompoundTag saved = new CompoundTag();
        run.component.writeNbt(saved, registries);

        RedstoneControlComponent loaded = new RedstoneControlComponent();
        loaded.readNbt(saved, registries, false);
        helper.assertTrue(loaded.isEnabled(), "a machine with redstone control enabled keeps it across a reload");
        helper.assertTrue(loaded.requiresLowSignal(), "a machine that requires a low signal keeps it across a reload");

        CompoundTag module = new CompoundTag();
        CompoundTag components = new CompoundTag();
        components.putBoolean("modern_tech:low_signal", true);
        module.put("components", components);
        CompoundTag old = new CompoundTag();
        old.put("redstoneModuleStack", module);

        RedstoneControlComponent migrated = new RedstoneControlComponent();
        migrated.readNbt(old, registries, false);
        helper.assertTrue(migrated.isEnabled(), "a machine with a redstone control module in an old save enables redstone control");
        helper.assertTrue(migrated.requiresLowSignal(), "a low signal module in an old save requires a low signal");

        RedstoneControlComponent empty = new RedstoneControlComponent();
        empty.readNbt(new CompoundTag(), registries, false);
        helper.assertFalse(empty.isEnabled(), "a machine without any saved redstone state runs on any signal");
        helper.assertFalse(empty.requiresLowSignal(), "a machine without any saved redstone state defaults to a high signal");

        helper.succeed();
    }

    private Run start(MIGameTestHelper helper, BlockPos pos) {
        Block block = BuiltInRegistries.BLOCK.getOptional(MI.id("electric_macerator"))
                .orElseThrow(() -> new AssertionError("The electric macerator is not registered"));
        helper.setBlock(pos, block);

        var machine = (AbstractCraftingMachineBlockEntity) helper.getBlockEntity(pos);
        IItemHandler items = machine.getInventory().itemStorage.itemHandler;
        IEnergyStorage energy = helper.requireCapability(Capabilities.EnergyStorage.BLOCK, pos, Direction.NORTH);
        RedstoneControlComponent component = machine.components.getOrThrow(RedstoneControlComponent.class);
        CrafterComponent crafter = machine.components.getOrThrow(CrafterComponent.class);

        Run run = new Run(machine, items, energy, component, crafter);
        feed(helper, run);

        helper.onEachTick(() -> feed(helper, run));

        return run;
    }

    private void feed(MIGameTestHelper helper, Run run) {
        int stored = run.energy.getEnergyStored();
        if (stored < run.energy.getMaxEnergyStored()) {
            stored += run.energy.receiveEnergy(run.energy.getMaxEnergyStored() - stored, false);
        }
        run.lastStored = stored;

        int room = run.items.getSlotLimit(0) - run.items.getStackInSlot(0).getCount();
        if (room > 0) {
            ItemStack stone = new ItemStack(Items.STONE, Math.min(STONE_PER_REFILL, room));
            ItemStack leftover = run.items.insertItem(0, stone, false);
            run.stone += stone.getCount() - leftover.getCount();
            if (leftover.getCount() == stone.getCount()) {
                helper.fail("The input slot refused all " + leftover.getCount() + " stone, it holds "
                        + run.items.getStackInSlot(0) + " and made " + run.crafts() + " cobblestone so far");
            }
        }
    }

    private static final class Run {
        final AbstractCraftingMachineBlockEntity machine;
        final IItemHandler items;
        final IEnergyStorage energy;
        final RedstoneControlComponent component;
        final CrafterComponent crafter;
        long baseline;
        int lastStored;
        int stone;

        Run(AbstractCraftingMachineBlockEntity machine, IItemHandler items, IEnergyStorage energy,
                RedstoneControlComponent component, CrafterComponent crafter) {
            this.machine = machine;
            this.items = items;
            this.energy = energy;
            this.component = component;
            this.crafter = crafter;
        }

        long crafts() {
            long crafts = 0;
            for (int slot = 1; slot < items.getSlots(); slot++) {
                crafts += items.getStackInSlot(slot).getCount();
            }
            return crafts;
        }
    }
}
