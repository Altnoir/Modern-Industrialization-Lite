package aztech.milite.blocks.storage;

import aztech.milite.items.ContainerItem;
import aztech.milite.MIText;
import aztech.milite.thirdparty.fabrictransfer.api.storage.TransferVariant;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public abstract class AbstractStorageBlockItem<T extends TransferVariant<?>> extends BlockItem implements ContainerItem<T> {
    public final StorageBehaviour<T> behaviour;

    public AbstractStorageBlockItem(AbstractStorageBlock<T> block, Properties properties) {
        super(block, properties);
        this.behaviour = block.behavior;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (!isUnlocked(stack)) {
            tooltip.add(MIText.Locked.text());
        }
    }

    public StorageBehaviour<T> getBehaviour() {
        return behaviour;
    }
}
