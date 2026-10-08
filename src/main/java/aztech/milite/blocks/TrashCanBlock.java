package aztech.milite.blocks;

import aztech.milite.MICapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;

public class TrashCanBlock extends Block {
    public TrashCanBlock(Properties properties) {
        super(properties);
    }

    // We have no BE, so we must invalidate caps on placement and removal
    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        if (!pOldState.is(this)) {
            pLevel.invalidateCapabilities(pPos);
        }
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())) {
            pLevel.invalidateCapabilities(pPos);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    private static class TrashItemHandler implements IItemHandler {
        private static final TrashItemHandler INSTANCE = new TrashItemHandler();

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return true;
        }
    }

    private static class TrashFluidHandler implements IFluidHandler {
        private static final TrashFluidHandler INSTANCE = new TrashFluidHandler();

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return true;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return resource.getAmount();
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
        }
    }

    private static class TrashFluidHandlerItem extends TrashFluidHandler implements IFluidHandlerItem {
        private final ItemStack container;

        private TrashFluidHandlerItem(ItemStack container) {
            this.container = container;
        }

        @Override
        public ItemStack getContainer() {
            return container;
        }
    }

    private static class TrashEnergyStorage implements IEnergyStorage {
        private static final TrashEnergyStorage INSTANCE = new TrashEnergyStorage();

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return maxReceive;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return 0;
        }

        @Override
        public int getMaxEnergyStored() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    }

    public static void onRegister(Block block, Item blockItem) {
        MICapabilities.onEvent(event -> {
            event.registerBlock(Capabilities.ItemHandler.BLOCK, (level, pos, state, be, direction) -> TrashItemHandler.INSTANCE, block);
            event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> TrashItemHandler.INSTANCE, blockItem);
            event.registerBlock(Capabilities.FluidHandler.BLOCK, (level, pos, state, be, direction) -> TrashFluidHandler.INSTANCE, block);
            event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new TrashFluidHandlerItem(stack), blockItem);
            event.registerBlock(Capabilities.EnergyStorage.BLOCK, (level, pos, state, be, direction) -> TrashEnergyStorage.INSTANCE, block);
        });
    }
}
