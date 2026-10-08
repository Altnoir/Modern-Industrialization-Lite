package aztech.milite.thirdparty.fabrictransfer.api.bridge;

import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.storage.base.SingleSlotStorage;
import aztech.milite.thirdparty.fabrictransfer.api.transaction.Transaction;
import com.google.common.primitives.Ints;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.FluidStack;

public record SlotFluidHandler(SingleSlotStorage<FluidVariant> storage) implements IFluidHandler {

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int slot) {
        return storage.getResource().toStack(Ints.saturatedCast(storage.getAmount()));
    }

    @Override
    public int fill(FluidStack stack, FluidAction action) {
        if (disallowIo() || stack.isEmpty()) {
            return 0;
        }
        try (var tx = Transaction.hackyOpen()) {
            var inserted = storage.insert(FluidVariant.of(stack), stack.getAmount(), tx);
            if (action.execute()) {
                tx.commit();
            }
            return (int) inserted;
        }
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (disallowIo() || resource.isEmpty() || storage.isResourceBlank() || !storage.getResource().matches(resource)) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int amount, FluidAction action) {
        if (disallowIo() || amount <= 0) {
            return FluidStack.EMPTY;
        }
        try (var tx = Transaction.hackyOpen()) {
            var resource = storage.getResource();
            if (resource.isBlank()) {
                return FluidStack.EMPTY;
            }
            var extracted = storage.extract(resource, amount, tx);
            if (action.execute()) {
                tx.commit();
            }
            return extracted == 0 ? FluidStack.EMPTY : resource.toStack((int) extracted);
        }
    }

    @Override
    public int getTankCapacity(int slot) {
        return Ints.saturatedCast(storage.getCapacity());
    }

    @Override
    public boolean isFluidValid(int slot, FluidStack stack) {
        return true;
    }

    protected boolean disallowIo() {
        return false;
    }
}
