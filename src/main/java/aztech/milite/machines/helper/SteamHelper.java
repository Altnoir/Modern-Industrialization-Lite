package aztech.milite.machines.helper;

import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.MIFluids;
import aztech.milite.util.Simulation;
import com.google.common.base.Preconditions;

import java.util.List;

public final class SteamHelper {
    public static long consumeSteamEu(List<ConfigurableFluidStack> fluidStacks, long maxEu, Simulation simulation) {
        Preconditions.checkArgument(maxEu >= 0, "May not consume < 0 EU.");

        long totalRem = 0;
        for (ConfigurableFluidStack stack : fluidStacks) {
            if (stack.getResource().getFluid() == MIFluids.STEAM.asFluid()) {
                long amount = stack.getAmount();
                long rem = Math.min(maxEu, amount);
                if (simulation.isActing()) {
                    stack.decrement(rem);
                }
                maxEu -= rem;
                totalRem += rem;
            }
        }
        return totalRem;
    }

    private SteamHelper() {}
}
