package aztech.milite.test;

import aztech.milite.MI;
import aztech.milite.MIBlock;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.components.CrafterComponent;
import aztech.milite.test.framework.MIGameTest;
import aztech.milite.test.framework.MIGameTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * Overclocker units double the speed of an electric machine per unit, never change the FE a craft costs, and do nothing
 * at all for the steam machines, which are already overclocked with gunpowder.
 */
public class OverclockUnitTests {
    private static final int ELECTRIC_SPEED = 4;
    private static final int BRONZE_SPEED = 1;
    private static final int STEEL_SPEED = 2;

    private static final long FE_PER_CRAFT = 200;

    /** Long enough to tell one craft per tick from anything faster, short enough to still fit in the output slots. */
    private static final int MEASURE_TICKS = 16;

    /** Ticks the recipe stone_to_cobblestone takes on a machine that has no speed bonus at all. */
    private static final int CRAFT_TICKS = 100;

    /** The crafts a macerator finished, which is the cobblestone it made. */
    private static long crafts(IItemHandler items) {
        long crafts = 0;
        for (int slot = 1; slot < items.getSlots(); slot++) {
            crafts += items.getStackInSlot(slot).getCount();
        }
        return crafts;
    }

    /** One unit doubles the speed, three units multiply it by eight, and breaking one gives the speed back. */
    @MIGameTest(timeoutTicks = 100)
    public void unitsStackUpOnElectricMachines(MIGameTestHelper helper) {
        BlockPos pos = new BlockPos(0, 2, 0);
        CrafterComponent crafter = place(helper, pos, "electric_macerator");

        helper.setBlock(pos.east(), MIBlock.OVERCLOCKER_UNIT.get());
        helper.setBlock(pos.west(), MIBlock.OVERCLOCKER_UNIT.get());
        helper.setBlock(pos.above(), MIBlock.OVERCLOCKER_UNIT.get());

        helper.startSequence()
                .thenExecute(() -> helper.assertValueEqual(crafter.getSpeedMultiplier(), ELECTRIC_SPEED * 8,
                        "speed of a machine that had three units placed next to it"))
                // The machines also count the units around them when they load, so the count must survive that too.
                .thenIdle(2)
                .thenExecute(() -> helper.assertValueEqual(crafter.getSpeedMultiplier(), ELECTRIC_SPEED * 8,
                        "speed of a machine that loaded next to three units"))
                .thenExecute(() -> helper.setBlock(pos.west(), Blocks.AIR))
                .thenExecute(() -> helper.assertValueEqual(crafter.getSpeedMultiplier(), ELECTRIC_SPEED * 4,
                        "speed once a unit is broken again"))
                .thenSucceed();
    }

    /** Steam machines count the units next to them but must not get any faster. */
    @MIGameTest(timeoutTicks = 100)
    public void steamMachinesIgnoreTheUnits(MIGameTestHelper helper) {
        BlockPos bronze = new BlockPos(0, 2, 0);
        CrafterComponent bronzeCrafter = place(helper, bronze, "bronze_macerator");
        BlockPos steel = new BlockPos(2, 2, 0);
        CrafterComponent steelCrafter = place(helper, steel, "steel_macerator");

        helper.setBlock(bronze.east(), MIBlock.OVERCLOCKER_UNIT.get());
        helper.setBlock(steel.east(), MIBlock.OVERCLOCKER_UNIT.get());

        helper.startSequence()
                .thenExecute(() -> {
                    helper.assertValueEqual(bronzeCrafter.getSpeedMultiplier(), BRONZE_SPEED,
                            "speed of a bronze machine next to a unit");
                    helper.assertValueEqual(steelCrafter.getSpeedMultiplier(), STEEL_SPEED,
                            "speed of a steel machine next to a unit");
                })
                .thenIdle(2)
                .thenExecute(() -> {
                    helper.assertValueEqual(bronzeCrafter.getSpeedMultiplier(), BRONZE_SPEED,
                            "speed of a bronze machine that loaded next to a unit");
                    helper.assertValueEqual(steelCrafter.getSpeedMultiplier(), STEEL_SPEED,
                            "speed of a steel machine that loaded next to a unit");
                })
                .thenSucceed();
    }

    /** However many units a machine has, a craft never takes less than one tick. */
    @MIGameTest(timeoutTicks = 200)
    public void speedStopsAtOneTickPerCraft(MIGameTestHelper helper) {
        BlockPos pos = new BlockPos(0, 2, 0);
        CrafterComponent crafter = place(helper, pos, "electric_macerator");
        MachineBlockEntity machine = helper.getBlockEntity(pos);
        IItemHandler items = machine.getInventory().itemStorage.itemHandler;
        IEnergyStorage energy = helper.requireCapability(Capabilities.EnergyStorage.BLOCK, pos, Direction.NORTH);

        helper.startSequence()
                // The machine counts the units around itself once it loads, so the forced count has to come after that.
                .thenIdle(1)
                .thenExecute(() -> machine.setOverclockerUnits(30))
                .thenExecute(() -> {
                    helper.assertTrue(crafter.getSpeedMultiplier() > CRAFT_TICKS,
                            "Expected thirty units to be faster than one craft per tick, but the speed was "
                                    + crafter.getSpeedMultiplier());
                    energy.receiveEnergy(Integer.MAX_VALUE, false);
                    items.insertItem(0, new ItemStack(Items.STONE, MEASURE_TICKS + 8), false);
                })
                .thenIdle(MEASURE_TICKS)
                .thenExecute(() -> helper.assertValueEqual(crafts(items), (long) MEASURE_TICKS,
                        "crafts of a machine that is fast enough to finish a craft every tick, which it must not beat"))
                .thenSucceed();
    }

    private CrafterComponent place(MIGameTestHelper helper, BlockPos pos, String machineId) {
        Block block = BuiltInRegistries.BLOCK.getOptional(MI.id(machineId))
                .orElseThrow(() -> new AssertionError("The block " + machineId + " is not registered"));
        helper.setBlock(pos, block);
        MachineBlockEntity machine = helper.getBlockEntity(pos);
        return machine.components.getOrThrow(CrafterComponent.class);
    }
}
