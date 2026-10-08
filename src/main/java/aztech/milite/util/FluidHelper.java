package aztech.milite.util;

import aztech.milite.MICommonProxy;
import aztech.milite.MIText;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariantAttributes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.ArrayList;
import java.util.List;

public class FluidHelper {
    public static Component getFluidName(FluidVariant fluid, boolean grayIfEmpty) {
        if (fluid.isBlank()) {
            Style style = grayIfEmpty ? Style.EMPTY.withColor(TextColor.fromRgb(0xa9a9a9)).withItalic(false) : Style.EMPTY;
            return MIText.Empty.text().setStyle(style);
        } else {
            return FluidVariantAttributes.getName(fluid);
        }
    }

    public static MutableComponent getFluidAmount(long amount, long capacity) {
        if (capacity < 100 * FluidType.BUCKET_VOLUME || MICommonProxy.INSTANCE.hasShiftDown()) {
            String text = amount + " / " + capacity;
            return Component.literal(text + " mB");
        } else {
            var maxedAmount = TextHelper.getMaxedAmount((double) amount / FluidType.BUCKET_VOLUME,
                    (double) capacity / FluidType.BUCKET_VOLUME);
            return Component.literal(maxedAmount.digit() + " / " + maxedAmount.maxDigit() + " " + maxedAmount.unit() + "B");
        }
    }

    public static MutableComponent getFluidAmount(long amount) {
        if (amount < 100 * FluidType.BUCKET_VOLUME || MICommonProxy.INSTANCE.hasShiftDown()) {
            String text = String.valueOf(amount);
            return Component.literal(text + " mB");
        } else {
            return getFluidAmountLarge(amount);
        }
    }

    public static MutableComponent getFluidAmountLarge(long amount) {
        var amountUnit = TextHelper.getAmount((double) amount / FluidType.BUCKET_VOLUME);
        return Component.literal(amountUnit.digit() + " " + amountUnit.unit() + "B");
    }

    public static int getColorMinLuminance(int color) {
        int r = (color & 0xFF);
        int g = (color & 0xFF00) >> 8;
        int b = (color & 0xFF0000) >> 16;
        double lum = (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255d;
        if (lum < 0.3) {
            if (lum == 0) {
                return 0x4C4C4C;
            } else {
                r = Math.min((int) (r * 0.3 / lum), 255);
                g = Math.min((int) (g * 0.3 / lum), 255);
                b = Math.min((int) (b * 0.3 / lum), 255);
                return r + (g << 8) + (b << 16);
            }
        } else {
            return color;
        }
    }

    public static List<Component> getTooltip(FluidVariant fluid, boolean grayIfEmpty) {
        if (fluid.isBlank()) {
            ArrayList<Component> list = new ArrayList();
            list.add(getFluidName(fluid, grayIfEmpty));
            return list;
        }
        return MICommonProxy.INSTANCE.getFluidTooltip(fluid);
    }

    public static List<Component> getTooltipForFluidStorage(FluidVariant fluid, long amount, long capacity, boolean grayIfEmpty) {
        List<Component> tooltip = getTooltip(fluid, grayIfEmpty);
        tooltip.add(getFluidAmount(amount, capacity));
        return tooltip;
    }

    public static List<Component> getTooltipForFluidStorage(FluidVariant fluid, long amount, long capacity) {
        return getTooltipForFluidStorage(fluid, amount, capacity, true);
    }
}
