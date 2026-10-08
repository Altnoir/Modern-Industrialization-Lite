package aztech.milite.machines.helper;

import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.MIFluids;
import aztech.milite.util.Simulation;
import com.google.common.base.Preconditions;

import java.util.List;

public final class SteamHelper {
    public static long consumeSteamRf(List<ConfigurableFluidStack> fluidStacks, long maxRf, Simulation simulation) {
        Preconditions.checkArgument(maxRf >= 0, "May not consume < 0 EU.");

        long totalRem = 0;
        for (ConfigurableFluidStack stack : fluidStacks) {
            if (stack.getResource().getFluid() == MIFluids.STEAM.asFluid()) {
                long amount = stack.getAmount();
                long rem = Math.min(maxRf, amount);
                if (simulation.isActing()) {
                    stack.decrement(rem);
                }
                maxRf -= rem;
                totalRem += rem;
            }
        }
        return totalRem;
    }

    private SteamHelper() {}
}
