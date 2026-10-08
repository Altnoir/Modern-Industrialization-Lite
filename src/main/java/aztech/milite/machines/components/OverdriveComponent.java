package aztech.milite.machines.components;

import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.MachineComponent;
import aztech.milite.MI;
import aztech.milite.MIItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.ItemInteractionResult;

public class OverdriveComponent implements MachineComponent.ServerOnly, DropableComponent {
    public static final ResourceLocation ID = MI.id("overdrive");

    private ItemStack overdriveModule = ItemStack.EMPTY;

    public boolean shouldOverdrive() {
        return !overdriveModule.isEmpty();
    }

    @Override
    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("overdriveModuleStack", overdriveModule.saveOptional(registries));
    }

    @Override
    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        overdriveModule = ItemStack.parseOptional(registries, tag.getCompound("overdriveModuleStack"));
    }

    public ItemInteractionResult onUse(MachineBlockEntity be, Player player, InteractionHand hand) {
        ItemStack stackInHand = player.getItemInHand(hand);
        if (stackInHand.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (MIItem.OVERDRIVE_MODULE.is(stackInHand) && overdriveModule.isEmpty()) {
            overdriveModule = stackInHand.copyWithCount(1);
            stackInHand.consume(1, player);

            be.setChanged();
            return ItemInteractionResult.sidedSuccess(player.level().isClientSide);
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public ItemStack getDrop() {
        return overdriveModule;
    }

    public void setStackServer(MachineBlockEntity be, ItemStack stack) {
        overdriveModule = stack;
        be.setChanged();
    }
}
