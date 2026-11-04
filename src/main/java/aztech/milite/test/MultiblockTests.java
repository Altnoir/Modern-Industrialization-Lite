package aztech.milite.test;

import aztech.milite.MI;
import aztech.milite.MIFluids;
import aztech.milite.machines.blockentities.multiblocks.ElectricCraftingMultiblockBlockEntity;
import aztech.milite.machines.blockentities.multiblocks.LargeTankMultiblockBlockEntity;
import aztech.milite.machines.blockentities.multiblocks.SteamCraftingMultiblockBlockEntity;
import aztech.milite.machines.components.CrafterComponent;
import aztech.milite.machines.components.OverclockComponent;
import aztech.milite.machines.helper.SteamHelper;
import aztech.milite.machines.init.MultiblockHatches;
import aztech.milite.machines.init.MultiblockMachines;
import aztech.milite.test.framework.MIGameTest;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.milite.test.framework.MIGameTestHelper;
import aztech.milite.util.Simulation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.FluidStack;

public class MultiblockTests {
    @MIGameTest
    public void steamMultiblockCountsSteelSpeedOnce(MIGameTestHelper helper) {
        var controllerPos = new BlockPos(3, 1, 2);
        helper.setBlock(controllerPos, MultiblockMachines.STEAM_BLAST_FURNACE.asBlock());
        SteamCraftingMultiblockBlockEntity furnace = helper.getBlockEntity(controllerPos);
        var matcher = furnace.createShapeMatcher();
        matcher.buildMultiblock(helper.getLevel());
        var hatchPos = matcher.getPositions().stream()
                .filter(pos -> matcher.getHatchFlags(pos) != null)
                .findFirst().orElseThrow();
        var crafter = furnace.components.getOrThrow(CrafterComponent.class);

        helper.startSequence()
                .thenIdle(1)
                .thenExecute(() -> {
                    helper.assertValueEqual(crafter.getSpeedMultiplier(), 1, "bronze steam multiblock speed");
                    helper.getLevel().setBlockAndUpdate(hatchPos, MultiblockHatches.STEEL_ITEM.input().asBlock().defaultBlockState());
                })
                .thenIdle(1)
                .thenExecute(() -> {
                    if (!furnace.isShapeValid()) {
                        helper.fail("steam blast furnace did not form with a steel hatch");
                    }
                    helper.assertValueEqual(crafter.getSpeedMultiplier(), 2, "steel steam multiblock speed");
                    var overclockTag = new CompoundTag();
                    overclockTag.putInt("overclock2.00", 2400);
                    furnace.components.getOrThrow(OverclockComponent.class)
                            .readNbt(overclockTag, helper.getLevel().registryAccess(), false);
                    helper.assertValueEqual(crafter.getSpeedMultiplier(), 4, "steel steam multiblock with gunpowder");
                })
                .thenSucceed();
    }

    @MIGameTest
    public void largeTankEmitsRedstone(MIGameTestHelper helper) {
        var controllerPos = new BlockPos(3, 1, 2);
        helper.setBlock(controllerPos, MultiblockMachines.LARGE_TANK.asBlock());

        LargeTankMultiblockBlockEntity largeTank = helper.getBlockEntity(controllerPos);
        largeTank.createShapeMatcher().buildMultiblock(helper.getLevel());

        var comparatorPos0 = new BlockPos(3, 1, 1);
        helper.setBlock(comparatorPos0.below(), Blocks.STONE);
        helper.setBlock(comparatorPos0, Blocks.COMPARATOR.defaultBlockState().setValue(ComparatorBlock.FACING, Direction.SOUTH));

        var hatchPos1 = new BlockPos(3, 0, 4);
        helper.setBlock(hatchPos1, MultiblockHatches.LARGE_TANK.asBlock());
        var comparatorPos1 = new BlockPos(3, 0, 5);
        helper.setBlock(comparatorPos1, Blocks.COMPARATOR);

        var hatchPos2 = new BlockPos(4, 0, 2);
        helper.setBlock(hatchPos2, MultiblockHatches.LARGE_TANK.asBlock());
        var comparatorPos2 = new BlockPos(5, 0, 2);
        helper.setBlock(comparatorPos2, Blocks.COMPARATOR.defaultBlockState().setValue(ComparatorBlock.FACING, Direction.WEST));
        var redstonePos = new BlockPos(6, 0, 2);
        helper.setBlock(redstonePos, Blocks.REDSTONE_WIRE);

        helper.startSequence()
                .thenIdle(1) // formation time
                .thenExecute(() -> {
                    helper.assertBlockProperty(comparatorPos0, ComparatorBlock.POWERED, false);
                    helper.assertBlockProperty(comparatorPos1, ComparatorBlock.POWERED, false);
                    helper.assertBlockProperty(comparatorPos2, ComparatorBlock.POWERED, false);
                    helper.assertBlockProperty(redstonePos, RedStoneWireBlock.POWER, 0);
                })
                .thenExecute(() -> {
                    largeTank.getExposedFluidHandler().fill(new FluidStack(Fluids.WATER, 1), IFluidHandler.FluidAction.EXECUTE);
                })
                .thenIdle(3) // redstone update time (gametests run after the level tick so the delay is 1 more than usual)
                .thenExecute(() -> {
                    helper.assertBlockProperty(comparatorPos0, ComparatorBlock.POWERED, true);
                    helper.assertBlockProperty(comparatorPos1, ComparatorBlock.POWERED, true);
                    helper.assertBlockProperty(comparatorPos2, ComparatorBlock.POWERED, true);
                    helper.assertBlockProperty(redstonePos, RedStoneWireBlock.POWER, 1);
                })
                .thenExecute(() -> {
                    largeTank.getExposedFluidHandler().fill(new FluidStack(Fluids.WATER, Integer.MAX_VALUE), IFluidHandler.FluidAction.EXECUTE);
                })
                .thenIdle(3) // redstone update time
                .thenExecute(() -> {
                    helper.assertBlockProperty(comparatorPos0, ComparatorBlock.POWERED, true);
                    helper.assertBlockProperty(comparatorPos1, ComparatorBlock.POWERED, true);
                    helper.assertBlockProperty(comparatorPos2, ComparatorBlock.POWERED, true);
                    helper.assertBlockProperty(redstonePos, RedStoneWireBlock.POWER, 15);
                })
                .thenExecute(() -> {
                    helper.setBlock(hatchPos1.above(2), Blocks.AIR);
                })
                .thenIdle(3) // redstone update time
                .thenExecute(() -> {
                    helper.assertBlockProperty(comparatorPos0, ComparatorBlock.POWERED, false);
                    helper.assertBlockProperty(comparatorPos1, ComparatorBlock.POWERED, false);
                    helper.assertBlockProperty(comparatorPos2, ComparatorBlock.POWERED, false);
                    helper.assertBlockProperty(redstonePos, RedStoneWireBlock.POWER, 0);
                })
                .thenSucceed();
    }

    @MIGameTest
    public void steamMultiblocksRunOnAnySteam(MIGameTestHelper helper) {
        var controllerPos = new BlockPos(3, 1, 2);
        helper.setBlock(controllerPos, MultiblockMachines.COKE_OVEN.asBlock());
        SteamCraftingMultiblockBlockEntity oven = helper.getBlockEntity(controllerPos);
        var matcher = oven.createShapeMatcher();
        matcher.buildMultiblock(helper.getLevel());
        var hatchPositions = matcher.getPositions().stream().filter(pos -> matcher.getHatchFlags(pos) != null).toList();
        var crafter = oven.getCrafterComponent();

        helper.startSequence()
                .thenExecute(() -> {
                    var level = helper.getLevel();
                    level.setBlockAndUpdate(hatchPositions.get(0), MultiblockHatches.BRONZE_ITEM.input().asBlock().defaultBlockState());
                    level.setBlockAndUpdate(hatchPositions.get(1), MultiblockHatches.BRONZE_ITEM.output().asBlock().defaultBlockState());
                    level.setBlockAndUpdate(hatchPositions.get(2), MultiblockHatches.BRONZE_FLUID.input().asBlock().defaultBlockState());
                    level.setBlockAndUpdate(hatchPositions.get(3), MultiblockHatches.BRONZE_FLUID.output().asBlock().defaultBlockState());
                })
                .thenIdle(5) // 成型
                .thenExecute(() -> {
                    var inventory = oven.getMultiblockInventoryComponent();
                    helper.assertTrue(MIFluids.STEAM.asFluid().defaultFluidState().is(SteamHelper.STEAM),
                            "milite steam belongs to the c:steam tag");
                    helper.assertTrue(MIFluids.HIGH_PRESSURE_STEAM.asFluid().defaultFluidState().is(SteamHelper.HIGH_PRESSURE_STEAM),
                            "milite high pressure steam belongs to the c:high_pressure_steam tag");
                    helper.assertValueEqual(SteamHelper.consumeSteamFe(inventory.getFluidInputs(), 1, Simulation.SIMULATE), 0L,
                            "steam made from nothing");
                    inventory.getFluidInputs().get(0).setKey(FluidVariant.of(MIFluids.HIGH_PRESSURE_STEAM.asFluid()));
                    inventory.getFluidInputs().get(0).setAmount(4000);
                    inventory.getItemInputs().get(0).setKey(ItemVariant.of(Items.COAL));
                    inventory.getItemInputs().get(0).setAmount(1);
                    helper.assertValueEqual(SteamHelper.consumeSteamFe(inventory.getFluidInputs(), 8, Simulation.SIMULATE), 8L,
                            "high pressure steam gives eight times the power");
                    helper.assertValueEqual(inventory.getFluidInputs().get(0).getAmount(), 4000L, "a simulation must not consume anything");
                })
                .thenIdle(20)
                .thenExecute(() -> {
                    if (!oven.isShapeValid()) {
                        helper.fail("the coke oven did not form");
                    }
                    if (!crafter.hasActiveRecipe()) {
                        helper.fail("the coke oven does not run on high pressure steam");
                    }
                    long steam = oven.getMultiblockInventoryComponent().getFluidInputs().get(0).getAmount();
                    if (steam >= 4000) {
                        helper.fail("the coke oven did not consume any high pressure steam");
                    }
                    var smokePos = oven.getOverclockParticlePos();
                    var controller = oven.getBlockPos();
                    double dx = smokePos.x - controller.getX();
                    double dy = smokePos.y - controller.getY();
                    double dz = smokePos.z - controller.getZ();
                    helper.assertTrue(Math.abs(dx - 0.5D) < 1e-6 && Math.abs(dy - 2.05D) < 1e-6 && Math.abs(dz - 1.5D) < 1e-6,
                            "overclock smoke rises out of the middle hole, not from above the controller");
                })
                .thenSucceed();
    }

    @MIGameTest
    public void implosionCompressorIsBuiltFromBlastproofCasing(MIGameTestHelper helper) {
        var controllerPos = new BlockPos(3, 1, 2);
        helper.setBlock(controllerPos, MultiblockMachines.IMPLOSION_COMPRESSOR.asBlock());

        ElectricCraftingMultiblockBlockEntity implosionCompressor = helper.getBlockEntity(controllerPos);
        var level = implosionCompressor.getLevel();
        var casingPositions = implosionCompressor.createShapeMatcher().getPositions();
        var blastproofCasing = BuiltInRegistries.BLOCK.get(MI.id("blastproof_casing"));
        var titaniumCasing = BuiltInRegistries.BLOCK.get(MI.id("solid_titanium_machine_casing"));

        helper.startSequence()
                .thenExecute(() -> casingPositions.forEach(pos -> level.setBlockAndUpdate(pos, blastproofCasing.defaultBlockState())))
                .thenIdle(1) // formation time
                .thenExecute(() -> {
                    if (!implosionCompressor.isShapeValid()) {
                        helper.fail("the implosion compressor does not form out of blastproof casings");
                    }
                })
                .thenExecute(() -> casingPositions.forEach(pos -> level.setBlockAndUpdate(pos, titaniumCasing.defaultBlockState())))
                .thenIdle(1)
                .thenExecute(() -> {
                    if (implosionCompressor.isShapeValid()) {
                        helper.fail("the implosion compressor forms out of titanium casings");
                    }
                })
                .thenSucceed();
    }
}
