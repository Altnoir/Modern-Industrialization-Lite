package aztech.milite.blocks.storage.tank;

import aztech.milite.blocks.storage.AbstractStorageBlockItem;
import aztech.milite.blocks.storage.ResourceStorage;
import aztech.milite.MIComponents;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.util.FluidHelper;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class TankItem extends AbstractStorageBlockItem<FluidVariant> {
    public TankItem(TankBlock block, Properties settings) {
        super(block, settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (this.behaviour.isCreative()) {
            tooltip.add(FluidHelper.getFluidName(getResource(stack), true));
        } else {
            long capacity = behaviour.getCapacityForResource(getResource(stack));

            if (isEmpty(stack)) {
                if (!isUnlocked(stack)) {
                    tooltip.addAll(FluidHelper.getTooltipForFluidStorage(getResource(stack), 0, capacity));
                } else {
                    tooltip.addAll(FluidHelper.getTooltipForFluidStorage(FluidVariant.blank(), 0, capacity));
                }
            } else {
                tooltip.addAll(FluidHelper.getTooltipForFluidStorage(getResource(stack), getAmount(stack), capacity));
            }
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public DataComponentType<ResourceStorage<FluidVariant>> getComponentType() {
        return MIComponents.FLUID_STORAGE.get();
    }

    @Override
    public ResourceStorage<FluidVariant> getDefaultComponent() {
        return ResourceStorage.FLUID_EMPTY;
    }
}
