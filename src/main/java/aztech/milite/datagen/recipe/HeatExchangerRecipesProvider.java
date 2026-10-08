package aztech.milite.datagen.recipe;

import aztech.milite.definition.FluidDefinition;
import aztech.milite.definition.FluidLike;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.MIFluids;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.material.Fluids;

public class HeatExchangerRecipesProvider extends MIRecipesProvider {
    public HeatExchangerRecipesProvider(PackOutput packOutput) {
        super(packOutput);
    }

    private static String fluidToString(FluidLike f, boolean id) {
        if (f.asFluid() == Fluids.WATER) {
            return (id ? "minecraft:" : "") + "water";
        } else if (f.asFluid() == Fluids.LAVA) {
            return (id ? "minecraft:" : "") + "lava";
        } else {
            return ((FluidDefinition) f).getResourceAsString(id);
        }
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        FluidLike[] hots = { MIFluids.STEAM, MIFluids.HEAVY_WATER_STEAM, MIFluids.HIGH_PRESSURE_HEAVY_WATER_STEAM, MIFluids.HIGH_PRESSURE_STEAM };
        FluidLike[] cold = { FluidLike.of(Fluids.WATER), MIFluids.HEAVY_WATER, MIFluids.HIGH_PRESSURE_HEAVY_WATER, MIFluids.HIGH_PRESSURE_WATER };
        int[] amount = { 1, 1, 8, 8 };

        int amountBaseHot = 16000;
        int amountBaseCold = 1000;

        for (int i = 0; i < hots.length; i++) {
            for (int j = 0; j < cold.length; j++) {
                if (i != j) {

                    var recipe = new MachineRecipeBuilder(MIMachineRecipeTypes.HEAT_EXCHANGER, 2, 300);

                    String path = "heat_exchanger/" + fluidToString(hots[i], false) + "_with_"
                            + fluidToString(cold[j], false);

                    recipe.addFluidInput(fluidToString(hots[i], true), amountBaseHot / amount[i]);
                    recipe.addFluidInput(fluidToString(cold[j], true), amountBaseCold / amount[j]);

                    recipe.addFluidOutput(fluidToString(cold[i], true), amountBaseCold / amount[i]);
                    recipe.addFluidOutput(fluidToString(hots[j], true), amountBaseHot / amount[j]);

                    recipe.offerTo(consumer, path);
                }

            }
        }
    }
}
