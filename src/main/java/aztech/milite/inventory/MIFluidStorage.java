package aztech.milite.inventory;

import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.storage.StorageUtil;
import aztech.milite.thirdparty.fabrictransfer.api.transaction.Transaction;
import com.google.common.primitives.Ints;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class MIFluidStorage extends MIStorage<Fluid, FluidVariant, ConfigurableFluidStack> {
    public final IFluidHandler fluidHandler = new FluidHandler();

    public MIFluidStorage(List<ConfigurableFluidStack> stacks) {
        super(stacks, true);
    }

    public class FluidHandler implements IFluidHandler {
        @Override
        public int getTanks() {
            return stacks.size();
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            var stack = stacks.get(tank);
            return stack.getVariant().toStack(Ints.saturatedCast(stack.getAmount()));
        }

        @Override
        public int getTankCapacity(int tank) {
            return Ints.saturatedCast(stacks.get(tank).getCapacity());
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return stacks.get(tank).isResourceAllowedByLock(stack.getFluid());
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.isEmpty()) {
                return 0;
            }
            try (var tx = Transaction.hackyOpen()) {
                long result = insert(FluidVariant.of(resource), resource.getAmount(), tx);
                if (result > 0 && action.execute()) {
                    tx.commit();
                }
                return (int) result;
            }
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource.isEmpty()) {
                return FluidStack.EMPTY;
            }
            try (var tx = Transaction.hackyOpen()) {
                long result = extract(FluidVariant.of(resource), resource.getAmount(), tx);
                if (result > 0 && action.execute()) {
                    tx.commit();
                }
                var ret = resource.copy();
                ret.setAmount((int) result);
                return ret;
            }
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (maxDrain <= 0) {
                return FluidStack.EMPTY;
            }
            try (var tx = Transaction.hackyOpen()) {
                var result = StorageUtil.extractAny(MIFluidStorage.this, maxDrain, tx);
                if (result == null) {
                    return FluidStack.EMPTY;
                }
                if (action.execute()) {
                    tx.commit();
                }
                return result.resource().toStack((int) result.amount());
            }
        }
    }
}
