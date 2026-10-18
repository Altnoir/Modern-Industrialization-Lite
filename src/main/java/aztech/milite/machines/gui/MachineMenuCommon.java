package aztech.milite.machines.gui;

import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.ConfigurableItemStack;
import aztech.milite.inventory.ConfigurableScreenHandler;
import aztech.milite.inventory.MIInventory;
import aztech.milite.inventory.SlotGroup;
import aztech.milite.machines.ComponentStorage;
import aztech.milite.MIRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

public abstract class MachineMenuCommon extends ConfigurableScreenHandler implements GuiComponent.MenuFacade {
    public final MachineGuiParameters guiParams;

    protected MachineMenuCommon(int syncId, Inventory playerInventory, MIInventory inventory, MachineGuiParameters guiParams,
            ComponentStorage<? extends GuiComponent> guiComponents) {
        super(MIRegistries.MACHINE_MENU.get(), syncId, playerInventory, inventory);
        this.guiParams = guiParams;

        // Player inventory slots
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, i * 9 + j + 9, guiParams.playerInventoryX + j * 18, guiParams.playerInventoryY + i * 18));
            }
        }
        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j, guiParams.playerInventoryX + j * 18, guiParams.playerInventoryY + 58));
        }

        // Gui components first (we want to prioritize them with shift click)
        for (var component : guiComponents) {
            component.setupMenu(this);
        }

        // Configurable slots
        for (int i = 0; i < inventory.getItemStacks().size(); ++i) {
            ConfigurableItemStack stack = inventory.getItemStacks().get(i);
            this.addSlot(stack.new ConfigurableItemSlot(this::markDirty, inventory.itemPositions.getX(i), inventory.itemPositions.getY(i), s -> true),
                    SlotGroup.CONFIGURABLE_STACKS);
        }
        for (int i = 0; i < inventory.getFluidStacks().size(); ++i) {
            ConfigurableFluidStack stack = inventory.getFluidStacks().get(i);
            this.addSlot(stack.new ConfigurableFluidSlot(this::markDirty, inventory.fluidPositions.getX(i), inventory.fluidPositions.getY(i)),
                    SlotGroup.CONFIGURABLE_STACKS);
        }
    }

    protected void markDirty() {
    }

    @Override
    public void addSlotToMenu(Slot slot, SlotGroup slotGroup) {
        addSlot(slot, slotGroup);
    }

    @Override
    public MIInventory getMachineInventory() {
        return inventory;
    }

    public Player getPlayer() {
        return playerInventory.player;
    }

    @Override
    public MachineGuiParameters getGuiParams() {
        return guiParams;
    }

    public abstract void readClientComponentSyncData(int componentIndex, RegistryFriendlyByteBuf buf);
}
