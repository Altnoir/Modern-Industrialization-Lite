package aztech.milite.machines.helper;

import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.util.Simulation;
import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.List;

public final class SteamHelper {
    public static final TagKey<Fluid> STEAM = FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", "steam"));
    public static final TagKey<Fluid> HIGH_PRESSURE_STEAM = FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", "high_pressure_steam"));

    public static long consumeSteamFe(List<ConfigurableFluidStack> fluidStacks, long maxFe, Simulation simulation) {
        Preconditions.checkArgument(maxFe >= 0, "May not consume < 0 FE.");

        long totalRem = 0;
        for (ConfigurableFluidStack stack : fluidStacks) {
            int fePerMb = fePerMb(stack.getResource().getFluid());
            if (fePerMb == 0 || maxFe <= 0) {
                continue;
            }

            long neededMb = (maxFe + fePerMb - 1) / fePerMb;
            long consumed = Math.min(neededMb, stack.getAmount());
            if (consumed == 0) {
                continue;
            }
            if (simulation.isActing()) {
                stack.decrement(consumed);
            }
            long produced = Math.min(consumed * fePerMb, maxFe);
            maxFe -= produced;
            totalRem += produced;
        }
        return totalRem;
    }

    private static int fePerMb(Fluid fluid) {
        FluidState state = fluid.defaultFluidState();
        if (state.is(HIGH_PRESSURE_STEAM)) {
            return 8;
        }
        if (state.is(STEAM)) {
            return 1;
        }
        return 0;
    }

    private SteamHelper() {}
}
