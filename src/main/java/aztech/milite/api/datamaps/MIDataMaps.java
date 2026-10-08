package aztech.milite.api.datamaps;

import aztech.milite.MI;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;

import java.util.ArrayList;

public final class MIDataMaps {
    public static final DataMapType<Fluid, FluidFuel> FLUID_FUELS = DataMapType
            .builder(
                    MI.id("fluid_fuels"), Registries.FLUID, FluidFuel.CODEC)
            .synced(FluidFuel.CODEC, true)
            .build();

    /**
     * Allows attaching a tooltip to arbitrary items.
     */
    public static final AdvancedDataMapType<Item, ItemTooltip, DataMapValueRemover.Default<ItemTooltip, Item>> ITEM_TOOLTIPS = AdvancedDataMapType
            .builder(
                    MI.id("item_tooltips"), Registries.ITEM, ItemTooltip.CODEC)
            .synced(ItemTooltip.CODEC, true)
            .remover(DataMapValueRemover.Default.codec())
            .merger((registry, first, firstValue, second, secondValue) -> {
                var components = new ArrayList<>(firstValue.components());
                components.addAll(secondValue.components());
                return new ItemTooltip(components);
            })
            .build();

    private MIDataMaps() {}
}
