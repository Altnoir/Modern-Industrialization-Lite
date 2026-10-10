package aztech.milite.machines.gui;

import aztech.milite.compat.rei.machines.ReiMachineRecipes;
import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.ConfigurableItemStack;
import aztech.milite.inventory.MoveRecipeHandler;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.network.machines.MachineComponentSyncPacket;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.milite.thirdparty.fabrictransfer.api.transaction.Transaction;
import com.google.common.primitives.Ints;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MachineMenuServer extends MachineMenuCommon implements MoveRecipeHandler {
    public final MachineBlockEntity blockEntity;
    protected final List<Object> trackedData;

    public MachineMenuServer(int syncId, Inventory playerInventory, MachineBlockEntity blockEntity, MachineGuiParameters guiParams) {
        super(syncId, playerInventory, blockEntity.getInventory(), guiParams, blockEntity.guiComponents);
        this.blockEntity = blockEntity;
        trackedData = new ArrayList<>();
        for (GuiComponentServer component : blockEntity.guiComponents) {
            trackedData.add(component.extractData());
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        blockEntity.guiComponents.forEachIndexed((i, component) -> {
            var newData = component.extractData();
            if (!Objects.equals(trackedData.get(i), newData)) {
                var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), blockEntity.getLevel().registryAccess());
                ((GuiComponentServer.Type<?, Object>) component.getType()).dataCodec().encode(buf, newData);
                byte[] bytes = new byte[buf.writerIndex()];
                buf.readBytes(bytes);
                new MachineComponentSyncPacket(containerId, i, bytes).sendToClient((ServerPlayer) playerInventory.player);
                trackedData.set(i, newData);
                buf.release();
            }
        });
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(blockEntity, player);
    }

    @Override
    protected void markDirty() {
        blockEntity.setChanged();
    }

    @Override
    public void readClientComponentSyncData(int componentIndex, RegistryFriendlyByteBuf buf) {
        throw new UnsupportedOperationException("Data can only be read on the client side!");
    }

    @Override
    public void moveRecipe(ResourceLocation recipeId, int fillAction, int amount) {
        var machineCategories = ReiMachineRecipes.machineToClickAreaCategory.get(guiParams.blockId);
        if (machineCategories == null) {
            return;
        }
        var world = playerInventory.player.level();
        var recipeHolder = machineCategories.stream()
                .map(entry -> ReiMachineRecipes.categories.get(entry.category))
                .filter(Objects::nonNull)
                .map(params -> params.recipeType)
                .distinct()
                .flatMap(recipeType -> world.getRecipeManager().getAllRecipesFor(recipeType).stream())
                .filter(r -> r.id().equals(recipeId)).findFirst().orElse(null);
        if (recipeHolder == null) {
            return;
        }
        var recipe = recipeHolder.value();

        var playerInventory = new PlayerMainInvWrapper(this.playerInventory);
        var machineItemInventory = this.getMachineInventory().itemStorage;
        var machineFluidInventory = this.getMachineInventory().fluidStorage;

        // Handle item inputs
        itemLoop:
        for (var itemInput : recipe.itemInputs) {
            int remainingAmount = itemInput.amount() * amount;
            // Check contents of the machine first
            for (int slot = 0; slot < machineItemInventory.itemHandler.getSlots(); slot++) {
                if (remainingAmount <= 0) {
                    continue itemLoop;
                }
                var configurableStack = inventory.getItemStacks().get(slot);
                if (!configurableStack.canPlayerInsert()) {
                    continue;
                }
                var stack = machineItemInventory.itemHandler.getStackInSlot(slot);
                if (itemInput.matches(stack)) {
                    remainingAmount -= stack.getCount();
                }
            }
            // Try to insert from player inventory
            for (int slot = 0; slot < playerInventory.getSlots(); slot++) {
                if (remainingAmount <= 0) {
                    continue itemLoop;
                }
                var stack = playerInventory.getStackInSlot(slot);
                if (itemInput.matches(stack)) {
                    var tryExtracted = playerInventory.extractItem(slot, remainingAmount, true);
                    if (tryExtracted.isEmpty()) {
                        continue;
                    }
                    try (var transaction = Transaction.openRoot()) {
                        int insertedAmount = Ints.saturatedCast(machineItemInventory.insert(ItemVariant.of(tryExtracted), tryExtracted.getCount(), transaction, ConfigurableItemStack::canPlayerInsert, false));
                        if (insertedAmount > 0) {
                            playerInventory.extractItem(slot, insertedAmount, false);
                            transaction.commit();
                            remainingAmount -= insertedAmount;
                        }
                    }
                }
            }
        }

        // Handle fluid inputs
        fluidLoop:
        for (var fluidInput : recipe.fluidInputs) {
            int remainingAmount = Ints.saturatedCast(fluidInput.amount() * amount);
            // Check contents of the machine first
            for (int tank = 0; tank < machineFluidInventory.fluidHandler.getTanks(); tank++) {
                if (remainingAmount <= 0) {
                    continue fluidLoop;
                }
                var configurableStack = inventory.getFluidStacks().get(tank);
                if (!configurableStack.canPlayerInsert()) {
                    continue;
                }
                var fluidStack = machineFluidInventory.fluidHandler.getFluidInTank(tank);
                if (fluidInput.fluid().test(fluidStack)) {
                    remainingAmount -= fluidStack.getAmount();
                }
            }
            // Try to insert from fluid containers in player inventory
            for (int slot = 0; slot < playerInventory.getSlots(); slot++) {
                if (remainingAmount <= 0) {
                    continue fluidLoop;
                }
                var stack = playerInventory.getStackInSlot(slot);
                if (stack.getCapability(Capabilities.FluidHandler.ITEM) != null) {
                    var fluidContainerStack = stack.copy();
                    var fluidContainer = fluidContainerStack.getCapability(Capabilities.FluidHandler.ITEM);
                    for (int tank = 0; tank < fluidContainer.getTanks(); tank++) {
                        var fluidStack = fluidContainer.getFluidInTank(tank);
                        if (fluidInput.fluid().test(fluidStack)) {
                            var tryExtracted = fluidContainer.drain(fluidStack.copyWithAmount(remainingAmount), IFluidHandler.FluidAction.SIMULATE);
                            if (tryExtracted.isEmpty()) {
                                continue;
                            }
                            try (var transaction = Transaction.openRoot()) {
                                int insertedAmount = Ints.saturatedCast(machineFluidInventory.insert(FluidVariant.of(tryExtracted), tryExtracted.getAmount(), transaction, ConfigurableFluidStack::canPlayerInsert, false));
                                if (insertedAmount > 0) {
                                    fluidContainer.drain(fluidStack.copyWithAmount(remainingAmount), IFluidHandler.FluidAction.EXECUTE);
                                    playerInventory.setStackInSlot(slot, fluidContainerStack);
                                    transaction.commit();
                                    remainingAmount -= insertedAmount;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
