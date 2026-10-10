package aztech.milite.client.compat.viewer.usage;

import aztech.milite.api.datamaps.FluidFuel;
import aztech.milite.api.datamaps.MIDataMaps;
import aztech.milite.client.compat.viewer.abstraction.ViewerCategory;
import aztech.milite.MI;
import aztech.milite.MIFluids;
import aztech.milite.MIItem;
import aztech.milite.MIText;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Consumer;

public class FluidFuelsCategory extends ViewerCategory<Fluid> {
    public FluidFuelsCategory() {
        super(Fluid.class, MI.id("fluid_fuels"), MIText.FluidFuels.text(), MIFluids.DIESEL.getBucket().getDefaultInstance(), 150, 35);
    }

    @Override
    public void buildWorkstations(WorkstationConsumer consumer) {
        consumer.accept("electric_diesel_generator", "aluminum_alloy_diesel_generator", "stainless_steel_diesel_generator", "large_diesel_generator", "large_steam_boiler",
                "advanced_large_steam_boiler",
                "high_pressure_large_steam_boiler",
                "high_pressure_advanced_large_steam_boiler");
        consumer.accept(MIItem.DIESEL_JETPACK, MIItem.DIESEL_CHAINSAW, MIItem.DIESEL_MINING_DRILL);
    }

    @Override
    public void buildRecipes(RecipeManager recipeManager, RegistryAccess registryAccess, Consumer<Fluid> consumer) {
        for (var fluid : registryAccess.registryOrThrow(Registries.FLUID)) {
            if (fluid.builtInRegistryHolder().getData(MIDataMaps.FLUID_FUELS) != null) {
                consumer.accept(fluid);
            }
        }
    }

    @Override
    public void buildLayout(Fluid recipe, LayoutBuilder builder) {
        builder.inputSlot(15, 10).variant(FluidVariant.of(recipe));
    }

    @Override
    public void buildWidgets(Fluid recipe, WidgetList widgets) {
        int totalEnergy = FluidFuel.getFe(recipe);
        Component text = MIText.FeInDieselGenerator.text(totalEnergy);
        widgets.secondaryText(text, 40, 14);
    }

    @Override
    public ResourceLocation getRecipeId(Fluid recipe) {
        return MI.id("/fluid_fuels/" + BuiltInRegistries.FLUID.getKey(recipe).toString().replace(':', '_'));
    }
}
