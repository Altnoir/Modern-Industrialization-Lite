package aztech.milite.fluid;

import aztech.milite.util.FluidHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

public class MIBucketItem extends BucketItem {
    public final int color;

    public MIBucketItem(MIFluid fluid, int color, Properties settings) {
        super(fluid, settings.stacksTo(1).craftRemainder(Items.BUCKET));
        this.color = FluidHelper.getColorMinLuminance(color);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
    }
}
