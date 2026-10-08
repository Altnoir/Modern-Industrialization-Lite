package aztech.milite.guidebook;

import aztech.milite.MIText;
import aztech.milite.util.TextHelper;
import guideme.GuidesCommon;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class GuideBookItem extends Item {
    public GuideBookItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (world.isClientSide) {
            GuidesCommon.openGuide(user, MIGuide.ID);
        }
        return InteractionResultHolder.consume(user.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(MIText.BookSubtitle.text().setStyle(TextHelper.GRAY_TEXT));
    }
}
