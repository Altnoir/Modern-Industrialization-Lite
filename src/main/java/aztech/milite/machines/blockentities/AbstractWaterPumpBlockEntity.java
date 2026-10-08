package aztech.milite.machines.blockentities;

import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.machines.BEP;
import aztech.milite.machines.components.IsActiveComponent;
import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.guicomponents.AutoExtract;
import aztech.milite.machines.guicomponents.ProgressBar;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.MachineComponent;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.util.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.List;

public abstract class AbstractWaterPumpBlockEntity extends MachineBlockEntity implements Tickable {
    protected static final int OUTPUT_SLOT_X = 110;
    protected static final int OUTPUT_SLOT_Y = 30;
    private static final ProgressBar.Params PROGRESS_BAR = new ProgressBar.Params(79, 29, "extract");
    private static final int OPERATION_TICKS = 100;

    public AbstractWaterPumpBlockEntity(BEP bep, String blockName) {
        super(bep, new MachineGuiParameters.Builder(blockName, false).build(), new OrientationComponent.Params(true, false, true));

        isActiveComponent = new IsActiveComponent();
        registerGuiComponent(new AutoExtract(orientation));
        registerGuiComponent(new ProgressBar(PROGRESS_BAR, () -> (float) pumpingTicks / OPERATION_TICKS));
        this.registerComponents(isActiveComponent, new MachineComponent() {
            @Override
            public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
                tag.putInt("pumpingTicks", pumpingTicks);
            }

            @Override
            public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
                pumpingTicks = tag.getInt("pumpingTicks");
            }
        });
    }

    abstract protected long consumeRf(long max);

    abstract protected int getWaterMultiplier();

    protected int pumpingTicks = 0; // number of ticks spent pumping this iteration
    protected IsActiveComponent isActiveComponent;

    @Override
    public void tick() {
        if (!level.isClientSide) {
            List<ConfigurableFluidStack> fluidStacks = getInventory().getFluidStacks();
            ConfigurableFluidStack waterStack = fluidStacks.get(fluidStacks.size() - 1);
            if (waterStack.getRemainingSpace() < FluidType.BUCKET_VOLUME / 8) {
                updateActive(false);
            } else {
                long rf = consumeRf(1);
                pumpingTicks += rf;
                updateActive(rf > 0);

                if (pumpingTicks == OPERATION_TICKS) {
                    waterStack.setKey(FluidVariant.of(Fluids.WATER));
                    waterStack.increment(Math.min((long) getWaterMultiplier() * getWaterSourceCount() * FluidType.BUCKET_VOLUME / 8,
                            waterStack.getRemainingSpace()));
                    pumpingTicks = 0;
                }
            }
            if (orientation.extractFluids) {
                getInventory().autoExtractFluids(level, worldPosition, orientation.outputDirection);
            }
            setChanged();
        }
    }

    private void updateActive(boolean newActive) {
        isActiveComponent.updateActive(newActive, this);
    }

    private static final int[] DX = new int[] { -1, 0, 1, 1, 1, 0, -1, -1 };
    private static final int[] DZ = new int[] { -1, -1, -1, 0, 1, 1, 1, 0 };

    @SuppressWarnings("ConstantConditions")
    private int getWaterSourceCount() {
        boolean[] adjWater = new boolean[] { false, false, false, false, false, false, false, false };
        for (int i = 0; i < 8; ++i) {
            BlockPos adjPos = worldPosition.offset(DX[i], 0, DZ[i]);
            FluidState adjState = level.getFluidState(adjPos);
            if (adjState.isSource() && adjState.getType() == Fluids.WATER) {
                adjWater[i] = true;
            }
        }
        int count = 0;
        for (int i = 0; i < 8; ++i) {
            if (adjWater[i]) {
                if (i % 2 == 1 || (adjWater[(i + 7) % 8] || adjWater[(i + 1) % 8])) {
                    count++;
                }
            }
        }
        return count;
    }
}
