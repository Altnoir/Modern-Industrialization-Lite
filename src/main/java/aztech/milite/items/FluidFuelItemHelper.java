package aztech.milite.items;

import aztech.milite.api.datamaps.MIDataMaps;
import aztech.milite.MIComponents;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.util.FluidHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

/**
 * Helper class for fluid items that can only contain FluidFuels
 */
public interface FluidFuelItemHelper {
    static FluidVariant getFluid(ItemStack stack) {
        return FluidVariant.of(new ItemStorage(stack, 0).getFluid());
    }

    static int getAmount(ItemStack stack) {
        return new ItemStorage(stack, 0).getFluid().getAmount();
    }

    static void decrement(ItemStack stack) {
        new ItemStorage(stack, 0).drain(1, IFluidHandler.FluidAction.EXECUTE);
    }

    class ItemStorage extends FluidHandlerItemStack {
        public ItemStorage(ItemStack container, int capacity) {
            super(MIComponents.FLUID_CONTENT, container, capacity);
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            return fluid.getFluidHolder().getData(MIDataMaps.FLUID_FUELS) != null;
        }

        @Override
        protected void setFluid(FluidStack fluid) {
            if (fluid.isEmpty()) {
                this.setContainerToEmpty();
            } else {
                super.setFluid(fluid);
            }
        }
    }

    static void appendTooltip(ItemStack stack, List<Component> tooltip, long capacity) {
        Style style = Style.EMPTY.withColor(TextColor.fromRgb(0xa9a9a9)).withItalic(true);
        FluidVariant fluid = getFluid(stack);
        tooltip.add(FluidHelper.getFluidName(fluid, true));
        if (!fluid.isBlank()) {
            tooltip.add(FluidHelper.getFluidAmount(getAmount(stack), capacity).setStyle(style));
        }
    }
}
