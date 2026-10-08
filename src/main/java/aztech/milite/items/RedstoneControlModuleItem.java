package aztech.milite.items;

import aztech.milite.MIComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RedstoneControlModuleItem extends Item {
    public RedstoneControlModuleItem(Properties properties) {
        super(properties.component(MIComponents.LOW_SIGNAL, false));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var stack = player.getItemInHand(usedHand);
        setRequiresLowSignal(stack, !isRequiresLowSignal(stack));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public static boolean isRequiresLowSignal(ItemStack stack) {
        return stack.getOrDefault(MIComponents.LOW_SIGNAL, false);
    }

    public static void setRequiresLowSignal(ItemStack stack, boolean requiresLowSignal) {
        stack.set(MIComponents.LOW_SIGNAL, requiresLowSignal);
    }
}
