package aztech.milite.client.machines.gui;

import aztech.milite.client.machines.GuiComponentsClient;
import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.ConfigurableItemStack;
import aztech.milite.inventory.MIInventory;
import aztech.milite.inventory.SlotPositions;
import aztech.milite.machines.ComponentStorage;
import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.gui.MachineMenuCommon;
import aztech.milite.util.NbtHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MachineMenuClient extends MachineMenuCommon {
    @SuppressWarnings("ConstantConditions")
    public static MachineMenuClient create(int syncId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        // Inventory
        List<ConfigurableItemStack> itemStacks = new ArrayList<>();
        List<ConfigurableFluidStack> fluidStacks = new ArrayList<>();
        CompoundTag tag = buf.readNbt();
        NbtHelper.getList(tag, "items", itemStacks, t -> new ConfigurableItemStack(t, buf.registryAccess()));
        NbtHelper.getList(tag, "fluids", fluidStacks, t -> new ConfigurableFluidStack(t, buf.registryAccess()));
        // Slot positions
        SlotPositions itemPositions = SlotPositions.read(buf);
        SlotPositions fluidPositions = SlotPositions.read(buf);
        MIInventory inventory = new MIInventory(itemStacks, fluidStacks, itemPositions, fluidPositions);
        // Components
        int componentCount = buf.readInt();
        List<GuiComponentServer.Type<?, ?>> componentTypes = new ArrayList<>(componentCount);
        ComponentStorage<GuiComponentClient<?, ?>> components = new ComponentStorage<>();
        for (int i = 0; i < componentCount; ++i) {
            var id = buf.readResourceLocation();
            var registration = GuiComponentsClient.get(id);
            componentTypes.add(registration.type());
            components.register(registration.readNewComponent(buf));
        }
        // GUI params
        MachineGuiParameters guiParams = MachineGuiParameters.read(buf);

        return new MachineMenuClient(syncId, playerInventory, inventory, componentTypes, components, guiParams);
    }

    private final List<GuiComponentServer.Type<?, ?>> componentTypes;
    public final ComponentStorage<GuiComponentClient<?, ?>> components;

    private MachineMenuClient(
            int syncId, Inventory playerInventory, MIInventory inventory,
            List<GuiComponentServer.Type<?, ?>> componentTypes, ComponentStorage<GuiComponentClient<?, ?>> components,
            MachineGuiParameters guiParams) {
        super(syncId, playerInventory, inventory, guiParams, components);
        this.componentTypes = componentTypes;
        this.components = components;
    }

    @Nullable
    public <T extends GuiComponentClient<?, ?>> T getComponent(Class<T> klass) {
        return components.getNullable(klass);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void readClientComponentSyncData(int componentIndex, RegistryFriendlyByteBuf buf) {
        readClientComponentSyncData(componentIndex, buf, componentTypes.get(componentIndex));
    }

    @SuppressWarnings("unchecked")
    private <D> void readClientComponentSyncData(
            int componentIndex, RegistryFriendlyByteBuf buf, GuiComponentServer.Type<?, D> type) {
        ((GuiComponentClient<?, D>) components.get(componentIndex)).data = type.dataCodec().decode(buf);
    }
}
