package aztech.milite.test;

import aztech.milite.MI;
import aztech.milite.MIItem;
import aztech.milite.inventory.MIInventory;
import aztech.milite.machines.blockentities.AbstractCraftingMachineBlockEntity;
import aztech.milite.machines.components.CrafterComponent;
import aztech.milite.machines.components.OverdriveComponent;
import aztech.milite.test.framework.MIGameTest;
import aztech.milite.test.framework.MIGameTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * The speed ladder of the crafting machines: a higher level eats more FE per tick and finishes the same recipe in less
 * time, so one craft costs the same on every level.
 */
public class MachineSpeedTests {
    /** One stone into one cobblestone, so the output slots count the crafts. A hundred ticks at two FE per tick. */
    private static final String RECIPE = "stone_to_cobblestone";

    private static final long RECIPE_DURATION = 100;
    private static final long FE_PER_CRAFT = 200;
    private static final long FE_PER_TICK = FE_PER_CRAFT / RECIPE_DURATION;
    private static final int ELECTRIC_SPEED = 4;
    private static final long ELECTRIC_FE_PER_TICK = FE_PER_TICK * ELECTRIC_SPEED;

    /** Long enough to tell the levels apart, short enough that an iridium machine cannot fill the output slots. */
    private static final int MEASURE_TICKS = 400;

    private static final int STONE_PER_REFILL = 4;

    /** Every level does in two ticks what bronze does in a hundred. */
    @MIGameTest(timeoutTicks = 800)
    public void higherLevelsCraftFasterForTheSameFePerCraft(MIGameTestHelper helper) {
        List<Run> runs = new ArrayList<>();
        runs.add(start(helper, new BlockPos(0, 2, 0), "electric_macerator", false));
        runs.add(start(helper, new BlockPos(1, 2, 0), "aluminum_alloy_macerator", false));
        runs.add(start(helper, new BlockPos(2, 2, 0), "stainless_steel_macerator", false));
        runs.add(start(helper, new BlockPos(3, 2, 0), "titanium_macerator", false));
        runs.add(start(helper, new BlockPos(4, 2, 0), "iridium_macerator", false));

        helper.startSequence()
                .thenIdle(MEASURE_TICKS)
                .thenExecute(() -> {
                    // A craft takes a whole number of ticks, so a level whose draw does not divide the 200 FE of a craft
                    // rounds up: 13 ticks for aluminum alloy, 7 for stainless steel, 4 for titanium and 2 for iridium.
                    assertCrafts(helper, runs.get(0), 16L, "electric", ELECTRIC_SPEED);
                    assertCrafts(helper, runs.get(1), 30L, "aluminum alloy", 8);
                    assertCrafts(helper, runs.get(2), 57L, "stainless steel", 16);
                    assertCrafts(helper, runs.get(3), 100L, "titanium", 32);
                    assertCrafts(helper, runs.get(4), 200L, "iridium", 64);

                    for (Run run : runs) {
                        assertFirstCraftCost(helper, run);
                    }
                })
                .thenSucceed();
    }

    /** The overdrive module makes a machine twice as fast out of the same FE. */
    @MIGameTest(timeoutTicks = 800)
    public void overdriveModuleDoublesTheSpeedAndNotTheCost(MIGameTestHelper helper) {
        Run plain = start(helper, new BlockPos(0, 2, 0), "electric_macerator", false);
        Run overdriven = start(helper, new BlockPos(2, 2, 0), "electric_macerator", true);

        helper.startSequence()
                .thenIdle(MEASURE_TICKS)
                .thenExecute(() -> {
                    assertCrafts(helper, plain, 16L, "electric", ELECTRIC_SPEED);
                    assertCrafts(helper, overdriven, 30L, "overdriven electric", ELECTRIC_SPEED * 2);
                    assertFirstCraftCost(helper, plain);
                    assertFirstCraftCost(helper, overdriven);
                })
                .thenSucceed();
    }

    /** A machine that runs out of power keeps its progress and picks the craft up when the power comes back. */
    @MIGameTest(timeoutTicks = 800)
    public void progressWaitsForPowerAndResumesWhenItComesBack(MIGameTestHelper helper) {
        // A fifth of a craft, so the machine always runs dry part way through the craft it is on.
        int buffer = (int) (FE_PER_CRAFT / 5);
        Run run = start(helper, new BlockPos(0, 2, 0), "electric_macerator", false, buffer);

        // The forty ticks it is fed for hand it 40 * 8 FE on top of the buffer, which is one craft and 120 FE of the
        // next one. The forty FE left in the buffer then run out 40 FE short of finishing that second craft.
        long drained = buffer + 40 * ELECTRIC_FE_PER_TICK;
        float drainedProgress = (float) (drained % FE_PER_CRAFT) / FE_PER_CRAFT;

        helper.startSequence()
                .thenIdle(40)
                .thenExecute(() -> run.feeding = false)
                .thenIdle(80)
                .thenExecute(() -> {
                    helper.assertValueEqual(run.consumed, drained, "FE a half crafted recipe cost before the buffer ran out");
                    helper.assertValueEqual(run.crafts(), 1L, "crafts of a machine that ran out of power part way through");
                    assertProgress(helper, run, drainedProgress, "progress of a machine that ran out of power");
                    run.frozen = run.consumed;
                    run.frozenProgress = run.crafter.getProgress();
                })
                // Two hundred ticks with an empty buffer must not eat a single FE or move the progress bar.
                .thenIdle(200)
                .thenExecute(() -> {
                    helper.assertValueEqual(run.consumed, run.frozen, "FE eaten by a machine whose buffer is empty");
                    helper.assertValueEqual(run.crafts(), 1L, "crafts of a machine whose buffer is empty");
                    assertProgress(helper, run, run.frozenProgress, "progress of a machine that has sat without power");
                    run.feeding = true;
                })
                // The 40 FE that were missing finish the craft within five ticks, and the craft still costs its 200.
                .thenIdle(30)
                .thenExecute(() -> {
                    helper.assertValueEqual(run.crafts(), 2L, "crafts of a machine that got its power back");
                    assertFirstCraftCost(helper, run);
                })
                .thenSucceed();
    }

    /** One machine of a test, and the counters the test reads once the measurement window is over. */
    private static final class Run {
        final String machine;
        final AbstractCraftingMachineBlockEntity blockEntity;
        final IItemHandler items;
        final IEnergyStorage energy;
        final CrafterComponent crafter;
        /** The FE the buffer is topped up to every tick, which is its whole capacity unless a test says otherwise. */
        int target;
        boolean feeding = true;
        long consumed;
        long stone;
        /** The FE the first craft cost, or -1 while the machine has not finished one. */
        long firstCraftFe = -1;
        long frozen;
        float frozenProgress;
        int lastStored;
        int ticks;

        Run(String machine, AbstractCraftingMachineBlockEntity blockEntity, IItemHandler items, IEnergyStorage energy,
                CrafterComponent crafter, int target) {
            this.machine = machine;
            this.blockEntity = blockEntity;
            this.items = items;
            this.energy = energy;
            this.crafter = crafter;
            this.target = target;
        }

        /** The crafts the machine finished, which is the cobblestone it made. */
        long crafts() {
            long crafts = 0;
            for (int slot = 1; slot < items.getSlots(); slot++) {
                crafts += items.getStackInSlot(slot).getCount();
            }
            return crafts;
        }
    }

    private Run start(MIGameTestHelper helper, BlockPos pos, String machineId, boolean withOverdrive) {
        return start(helper, pos, machineId, withOverdrive, Integer.MAX_VALUE);
    }

    /**
     * Build one machine and keep both its buffer and its input slot topped up every tick, so that nothing but the speed
     * of the machine can slow the recipe down.
     *
     * @param bufferTarget what the buffer is topped up to every tick, so that only a test about running out of power
     *                     ever passes less than the whole capacity
     */
    private Run start(MIGameTestHelper helper, BlockPos pos, String machineId, boolean withOverdrive, int bufferTarget) {
        Block block = BuiltInRegistries.BLOCK.getOptional(MI.id(machineId))
                .orElseThrow(() -> new AssertionError("The block " + MI.id(machineId) + " is not registered, the id of a machine level must have changed"));
        helper.setBlock(pos, block);

        var machine = (AbstractCraftingMachineBlockEntity) helper.getBlockEntity(pos);
        MIInventory inventory = machine.getInventory();
        IItemHandler items = inventory.itemStorage.itemHandler;

        // Through the same capability a cable would use, so that the test measures what a machine really asks for.
        IEnergyStorage energy = helper.requireCapability(Capabilities.EnergyStorage.BLOCK, pos, Direction.NORTH);

        OverdriveComponent overdrive = machine.components.getNullable(OverdriveComponent.class);
        if (overdrive == null) {
            helper.fail(machineId + " should have an overdrive slot", pos);
        }
        if (withOverdrive) {
            overdrive.setStackServer(machine, new ItemStack(MIItem.OVERDRIVE_MODULE.asItem()));
        }

        CrafterComponent crafter = machine.components.getOrThrow(CrafterComponent.class);

        Run run = new Run(machineId + (withOverdrive ? " with an overdrive module" : ""), machine, items, energy, crafter,
                bufferTarget);
        feed(helper, pos, run);

        helper.onEachTick(() -> feed(helper, pos, run));

        return run;
    }

    /** Give the machine a full buffer to craft from and a full input slot to craft. */
    private void feed(MIGameTestHelper helper, BlockPos pos, Run run) {
        // Whatever left the buffer since the last tick went into the recipe: a machine is the only thing that can take
        // FE out of it.
        int stored = run.energy.getEnergyStored();
        if (run.ticks > 0) {
            run.consumed += run.lastStored - stored;
        }
        if (run.feeding && stored < run.target) {
            stored += run.energy.receiveEnergy(run.target - stored, false);
        }
        run.lastStored = stored;
        run.ticks++;

        if (run.crafts() > 0 && run.firstCraftFe < 0) {
            run.firstCraftFe = run.consumed;
        }

        int room = run.items.getSlotLimit(0) - run.items.getStackInSlot(0).getCount();
        if (room > 0) {
            ItemStack stone = new ItemStack(Items.STONE, Math.min(STONE_PER_REFILL, room));
            ItemStack leftover = run.items.insertItem(0, stone, false);
            run.stone += stone.getCount() - leftover.getCount();
            // An input slot holds a stack, so it refusing stone once it is full is normal, not a failure.
            if (leftover.getCount() == stone.getCount()) {
                helper.fail("The input slot of " + run.machine + " refused all " + leftover.getCount() + " stone, it holds "
                        + run.items.getStackInSlot(0) + " and made " + run.crafts() + " cobblestone so far", pos);
            }
        }
    }

    private void assertCrafts(MIGameTestHelper helper, Run run, long expected, String level, int speedMultiplier) {
        helper.assertValueEqual(run.crafts(), expected, "crafts of the " + level + " macerator in " + MEASURE_TICKS
                + " ticks at " + speedMultiplier + " times the bronze speed, out of " + run.stone + " stone and "
                + run.consumed + " FE");
    }

    private void assertFirstCraftCost(MIGameTestHelper helper, Run run) {
        helper.assertValueEqual(run.firstCraftFe, FE_PER_CRAFT, "FE that the first craft of the recipe " + RECIPE
                + " cost " + run.machine + ", which must not depend on how fast the machine is");
    }

    private void assertProgress(MIGameTestHelper helper, Run run, float expected, String message) {
        float progress = run.crafter.getProgress();
        helper.assertTrue(Math.abs(progress - expected) < 1.0E-4F,
                "Expected " + message + " to be " + expected + ", but was " + progress);
    }
}
