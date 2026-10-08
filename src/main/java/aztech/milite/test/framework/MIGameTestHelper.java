/*
 * MIT License
 *
 * Copyright (c) 2026 Altnoir Null々
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package aztech.milite.test.framework;

import aztech.milite.MIBlock;
import aztech.milite.blocks.storage.tank.creativetank.CreativeTankBlockEntity;
import aztech.milite.materials.MIMaterials;
import aztech.milite.materials.Material;
import aztech.milite.materials.part.MIParts;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jspecify.annotations.Nullable;

public class MIGameTestHelper extends GameTestHelper {
    public MIGameTestHelper(GameTestInfo testInfo) {
        super(testInfo);
    }

    public void creativeTank(BlockPos pos, Fluid fluid) {
        setBlock(pos, MIBlock.CREATIVE_TANK.get());
        var tank = (CreativeTankBlockEntity) getBlockEntity(pos);
        tank.setFluid(FluidVariant.of(fluid));
    }

    public void emptyTank(BlockPos pos) {
        emptyTank(pos, MIMaterials.IRIDIUM);
    }

    public void emptyTank(BlockPos pos, Material material) {
        setBlock(pos, material.getPart(MIParts.TANK).asBlock());
    }

    public void assertAir(BlockPos pos) {
        assertBlockPresent(Blocks.AIR, pos);
    }

    public <T, C extends @Nullable Object> T requireCapability(BlockCapability<T, C> cap, BlockPos pos, C context) {
        var ret = getLevel().getCapability(cap, absolutePos(pos), context);
        if (ret == null) {
            fail("Could not find capability " + cap.name(), pos);
        }
        return ret;
    }

    public void assertFluid(BlockPos pos, Fluid fluid, int amount) {
        var fluidHandler = requireCapability(Capabilities.FluidHandler.BLOCK, pos, null);
        boolean foundAny = false;
        var fluidName = BuiltInRegistries.FLUID.getKey(fluid);
        for (int i = 0; i < fluidHandler.getTanks(); ++i) {
            var stack = fluidHandler.getFluidInTank(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.is(fluid) && stack.getAmount() == amount) {
                foundAny = true;
                continue;
            }
            fail("Expected fluid %s and amount %d, got %s".formatted(fluidName, amount, stack), pos);
        }
        if (!foundAny) {
            fail("Expected fluid %s and amount %d, got nothing".formatted(fluidName, amount), pos);
        }
    }

    public void assertNoFluid(BlockPos pos) {
        var fluidHandler = requireCapability(Capabilities.FluidHandler.BLOCK, pos, null);
        for (int i = 0; i < fluidHandler.getTanks(); ++i) {
            var stack = fluidHandler.getFluidInTank(i);
            if (!stack.isEmpty()) {
                fail("Expected no fluid, got %s".formatted(stack), pos);
            }
        }
    }

    public void assertEnergy(BlockPos pos, long energy, @Nullable Direction side) {
        var energyHandler = requireCapability(Capabilities.EnergyStorage.BLOCK, pos, side);
        int storedEnergy = energyHandler.getEnergyStored();
        if (storedEnergy != energy) {
            fail("Expected energy to be " + energy + ", was " + storedEnergy, pos);
        }
    }
}
