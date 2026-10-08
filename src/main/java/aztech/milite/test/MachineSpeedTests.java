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
 * The speed ladder of the crafting machines: a higher level finishes the same recipe faster by eating more RF per tick,
 * so one craft costs the same on every level, and an overdrive module doubles the speed without making the craft more
 * expensive.
 *
 * <p>
 * Everything here is measured through the capabilities a cable and a pipe would use, so the tests see exactly what a
 * player sees.
 */
public class MachineSpeedTests {
    /**
     * The test recipe: one stone in, one cobblestone out, which is also the vanilla compatible recipe of the macerator
     * ({@code milite:macerator/stone_to_cobblestone}). Its duration is the number of ticks a bronze machine takes over
     * it, so it takes a hundred ticks and two RF per tick there.
     */
    private static final String RECIPE = "stone_to_cobblestone";

    /** The ticks the recipe takes on a bronze machine, the slowest level there is. */
    private static final long RECIPE_DURATION = 100;

    /** What one craft costs no matter how fast the machine is: the duration of the recipe times its energy. */
    private static final long RF_PER_CRAFT = 200;

    /** What a bronze machine eats per tick, and therefore what the recipe's own energy is. */
    private static final long RF_PER_TICK = RF_PER_CRAFT / RECIPE_DURATION;

    /** The level the single machine tests use, and how many times faster than bronze it works. */
    private static final int ELECTRIC_SPEED = 4;

    /** What an electric machine eats per tick, which is what a test about running out of power has to drain. */
    private static final long ELECTRIC_RF_PER_TICK = RF_PER_TICK * ELECTRIC_SPEED;

    /**
     * How long every machine of a test is left to run before its counters are read, which is short enough that even an
     * iridium machine cannot fill the four output slots of a macerator: it would need 512 ticks to do that.
     */
    private static final int MEASURE_TICKS = 400;

    /** How much stone one refill offers the input slot, which is more than the fastest machine of a test eats per tick. */
    private static final int STONE_PER_REFILL = 4;

    /**
     * The ladder: every level crafts as many times faster than bronze as its speed multiplier says, and an iridium
     * machine therefore does in two ticks what a bronze one does in a hundred.
     */
    @MIGameTest(timeoutTicks = 800)
    public void higherLevelsCraftFasterForTheSameRfPerCraft(MIGameTestHelper helper) {
        List<Run> runs = new ArrayList<>();
        runs.add(start(helper, new BlockPos(0, 2, 0), "electric_macerator", false));
        runs.add(start(helper, new BlockPos(1, 2, 0), "aluminum_macerator", false));
        runs.add(start(helper, new BlockPos(2, 2, 0), "stainless_steel_macerator", false));
        runs.add(start(helper, new BlockPos(3, 2, 0), "titanium_macerator", false));
        runs.add(start(helper, new BlockPos(4, 2, 0), "iridium_macerator", false));

        helper.startSequence()
                .thenIdle(MEASURE_TICKS)
                .thenExecute(() -> {
                    // A craft needs a whole number of ticks, so a level whose draw does not divide the 200 RF of a craft
                    // rounds up: aluminum eats 16 RF a tick and therefore needs 13 ticks for a craft, stainless steel
                    // eats 32 and needs 7, titanium eats 64 and needs 4, iridium eats 128 and needs 2. The electric
                    // draw of 8 divides 200 exactly, so it needs the 25 ticks of a quarter of the bronze duration.
                    assertCrafts(helper, runs.get(0), 16L, "electric", ELECTRIC_SPEED);
                    assertCrafts(helper, runs.get(1), 30L, "aluminum", 8);
                    assertCrafts(helper, runs.get(2), 57L, "stainless steel", 16);
                    assertCrafts(helper, runs.get(3), 100L, "titanium", 32);
                    assertCrafts(helper, runs.get(4), 200L, "iridium", 64);

                    for (Run run : runs) {
                        assertFirstCraftCost(helper, run);
                    }
                })
                .thenSucceed();
    }

    /**
     * The overdrive module doubles the speed of a machine and leaves the cost of a craft alone, so an overdriven
     * machine makes exactly twice as much out of the same RF.
     */
    @MIGameTest(timeoutTicks = 800)
    public void overdriveModuleDoublesTheSpeedAndNotTheCost(MIGameTestHelper helper) {
        Run plain = start(helper, new BlockPos(0, 2, 0), "electric_macerator", false);
        Run overdriven = start(helper, new BlockPos(2, 2, 0), "electric_macerator", true);

        helper.startSequence()
                .thenIdle(MEASURE_TICKS)
                .thenExecute(() -> {
                    // An overdriven electric machine eats 16 RF a tick, so it needs 13 ticks a craft where the plain one
                    // needs 25, and makes 30 where the plain one makes 16.
                    assertCrafts(helper, plain, 16L, "electric", ELECTRIC_SPEED);
                    assertCrafts(helper, overdriven, 30L, "overdriven electric", ELECTRIC_SPEED * 2);
                    assertFirstCraftCost(helper, plain);
                    assertFirstCraftCost(helper, overdriven);
                })
                .thenSucceed();
    }

    /**
     * A machine that runs out of power keeps what it has already done and picks the craft up again once the power comes
     * back, instead of losing the progress or finishing the craft for free.
     */
    @MIGameTest(timeoutTicks = 800)
    public void progressWaitsForPowerAndResumesWhenItComesBack(MIGameTestHelper helper) {
        // A fifth of a craft is far less than the 200 RF a craft needs, so a machine that stops being fed always runs
        // dry part way through the craft it is on, however much of it is left by then.
        int buffer = (int) (RF_PER_CRAFT / 5);
        Run run = start(helper, new BlockPos(0, 2, 0), "electric_macerator", false, buffer);

        // Over the forty ticks it is fed for, the machine is handed the hundred RF it starts with plus eight RF a tick,
        // which is 320 RF and so one craft and 120 RF of the next one. Then it drinks the forty RF that are left in the
        // buffer and is 40 RF short of finishing that second craft.
        long fedRf = 40 * ELECTRIC_RF_PER_TICK;
        long drained = buffer + fedRf;
        float drainedProgress = (float) (drained % RF_PER_CRAFT) / RF_PER_CRAFT;

        helper.startSequence()
                // Forty ticks of crafting off a buffer too small to finish a craft from.
                .thenIdle(40)
                .thenExecute(() -> run.feeding = false)
                // Then it drains the buffer and runs dry 160 RF into the second craft, with the bar stuck at eight tenths.
                .thenIdle(80)
                .thenExecute(() -> {
                    helper.assertValueEqual(run.consumed, drained, "RF a half crafted recipe cost before the buffer ran out");
                    helper.assertValueEqual(run.crafts(), 1L, "crafts of a machine that ran out of power part way through");
                    assertProgress(helper, run, drainedProgress, "progress of a machine that ran out of power");
                    run.frozen = run.consumed;
                    run.frozenProgress = run.crafter.getProgress();
                })
                // With no power at all it must neither move nor forget: two hundred ticks without a single RF eaten.
                .thenIdle(200)
                .thenExecute(() -> {
                    helper.assertValueEqual(run.consumed, run.frozen, "RF eaten by a machine whose buffer is empty");
                    helper.assertValueEqual(run.crafts(), 1L, "crafts of a machine whose buffer is empty");
                    assertProgress(helper, run, run.frozenProgress, "progress of a machine that has sat without power");
                    run.feeding = true;
                })
                // The 40 RF that were missing finish the craft within five ticks, and the craft still costs its 200.
                .thenIdle(30)
                .thenExecute(() -> {
                    helper.assertValueEqual(run.crafts(), 2L, "crafts of a machine that got its power back");
                    assertFirstCraftCost(helper, run);
                })
                .thenSucceed();
    }

    /**
     * One machine of a test: its block entity, the two capabilities it is fed through, and the counters the test reads
     * once the measurement window is over.
     */
    private static final class Run {
        final String machine;
        final AbstractCraftingMachineBlockEntity blockEntity;
        final IItemHandler items;
        final IEnergyStorage energy;
        final CrafterComponent crafter;
        /** The RF the machine is topped up to every tick, which is its whole buffer unless a test says otherwise. */
        int target;
        boolean feeding = true;
        /** How much RF the machine ate over the whole test, which is what the recipe cost it. */
        long consumed;
        /** How much stone the input slot took, so that a machine starving for items cannot look merely slow. */
        long stone;
        /** The RF that the first craft cost, or -1 while the machine has not finished one. */
        long firstCraftRf = -1;
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

        /**
         * How many crafts the machine finished, which is how much cobblestone it made: the test recipe is one to one.
         * The output goes into the first output slot until it holds a stack and only then spills into the next one, so a
         * machine that outruns a single slot still counts in full.
         */
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
     * Build one machine, feed it the test recipe, and keep both its buffer and its input slot topped up every tick, so
     * that nothing but the speed of the machine can slow the recipe down.
     *
     * @param bufferTarget the RF the buffer is topped up to every tick, which is the whole buffer when it is larger than
     *                     the buffer, so that only a test about running out of power ever passes anything else
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

    /**
     * Give the machine everything it could possibly want this tick: a full buffer to craft from, and a full input slot
     * to craft, since a machine that runs out of either would only look slow.
     */
    private void feed(MIGameTestHelper helper, BlockPos pos, Run run) {
        // Whatever left the buffer since the last tick went into the recipe: a crafting machine is the only thing that
        // can take RF out of it.
        int stored = run.energy.getEnergyStored();
        if (run.ticks > 0) {
            run.consumed += run.lastStored - stored;
        }
        if (run.feeding && stored < run.target) {
            stored += run.energy.receiveEnergy(run.target - stored, false);
        }
        run.lastStored = stored;
        run.ticks++;

        if (run.crafts() > 0 && run.firstCraftRf < 0) {
            run.firstCraftRf = run.consumed;
        }

        // Top the input slot back up, but only up to what it can actually hold: an input slot caps at a stack, so it
        // refusing a stack's worth of stone once it is full is normal, not a failure.
        int room = run.items.getSlotLimit(0) - run.items.getStackInSlot(0).getCount();
        if (room > 0) {
            ItemStack stone = new ItemStack(Items.STONE, Math.min(STONE_PER_REFILL, room));
            ItemStack leftover = run.items.insertItem(0, stone, false);
            run.stone += stone.getCount() - leftover.getCount();
            if (leftover.getCount() == stone.getCount()) {
                helper.fail("The input slot of " + run.machine + " refused all " + leftover.getCount() + " stone, it holds "
                        + run.items.getStackInSlot(0) + " and made " + run.crafts() + " cobblestone so far", pos);
            }
        }
    }

    private void assertCrafts(MIGameTestHelper helper, Run run, long expected, String level, int speedMultiplier) {
        helper.assertValueEqual(run.crafts(), expected, "crafts of the " + level + " macerator in " + MEASURE_TICKS
                + " ticks at " + speedMultiplier + " times the reference speed, out of " + run.stone + " stone and "
                + run.consumed + " RF");
    }

    private void assertFirstCraftCost(MIGameTestHelper helper, Run run) {
        helper.assertValueEqual(run.firstCraftRf, RF_PER_CRAFT, "RF that the first craft of the recipe " + RECIPE
                + " cost " + run.machine + ", which must not depend on how fast the machine is");
    }

    private void assertProgress(MIGameTestHelper helper, Run run, float expected, String message) {
        float progress = run.crafter.getProgress();
        helper.assertTrue(Math.abs(progress - expected) < 1.0E-4F,
                "Expected " + message + " to be " + expected + ", but was " + progress);
    }
}
