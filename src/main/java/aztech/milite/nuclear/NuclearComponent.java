package aztech.milite.nuclear;

import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.milite.thirdparty.fabrictransfer.api.storage.TransferVariant;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;

public interface NuclearComponent<T extends TransferVariant> {
    double getHeatConduction();

    NeutronBehaviour getNeutronBehaviour();

    T getVariant();

    @Nullable
    default T getNeutronProduct() {
        return null;
    }

    default long getNeutronProductAmount() {
        return 0;
    }

    default double getNeutronProductProbability() {
        return 1;
    }

    default int getMaxTemperature() {
        return Integer.MAX_VALUE;
    }

    static ResourceLocation getEmiRecipeId(NuclearComponent<?> component, String category, String type) {
        return switch (component.getVariant()) {
            case ItemVariant itemVariant -> BuiltInRegistries.ITEM.getKey(itemVariant.getItem()).withPrefix("/" + category + "/item/").withSuffix("/" + type);
            case FluidVariant fluidVariant -> BuiltInRegistries.FLUID.getKey(fluidVariant.getFluid()).withPrefix("/" + category + "/fluid/").withSuffix("/" + type);
            case Object object -> throw new IllegalArgumentException("Unknown component variant " + object);
        };
    }
}
