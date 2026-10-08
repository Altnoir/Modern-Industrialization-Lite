package aztech.milite.client.machines;

import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.guicomponents.AutoExtractClient;
import aztech.milite.client.machines.guicomponents.CraftingMultiblockGuiClient;
import aztech.milite.client.machines.guicomponents.EnergyBarClient;
import aztech.milite.client.machines.guicomponents.GeneratorMultiblockGuiClient;
import aztech.milite.client.machines.guicomponents.GunpowderOverclockGuiClient;
import aztech.milite.client.machines.guicomponents.LargeTankFluidDisplayClient;
import aztech.milite.client.machines.guicomponents.MachineProblemsDisplayClient;
import aztech.milite.client.machines.guicomponents.NuclearReactorGuiClient;
import aztech.milite.client.machines.guicomponents.ProgressBarClient;
import aztech.milite.client.machines.guicomponents.ReiSlotLockingClient;
import aztech.milite.client.machines.guicomponents.ShapeSelectionClient;
import aztech.milite.client.machines.guicomponents.SlotPanelClient;
import aztech.milite.client.machines.guicomponents.TemperatureBarClient;
import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.machines.guicomponents.AutoExtract;
import aztech.milite.machines.guicomponents.CraftingMultiblockGui;
import aztech.milite.machines.guicomponents.EnergyBar;
import aztech.milite.machines.guicomponents.GeneratorMultiblockGui;
import aztech.milite.machines.guicomponents.GunpowderOverclockGui;
import aztech.milite.machines.guicomponents.LargeTankFluidDisplay;
import aztech.milite.machines.guicomponents.MachineProblemsDisplay;
import aztech.milite.machines.guicomponents.NuclearReactorGui;
import aztech.milite.machines.guicomponents.ProgressBar;
import aztech.milite.machines.guicomponents.ReiSlotLocking;
import aztech.milite.machines.guicomponents.ShapeSelection;
import aztech.milite.machines.guicomponents.SlotPanel;
import aztech.milite.machines.guicomponents.TemperatureBar;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;
import java.util.HashMap;
import java.util.Map;

public final class GuiComponentsClient {
    private static final Map<ResourceLocation, Registration<?, ?>> components = new HashMap<>();

    public static Registration<?, ?> get(ResourceLocation id) {
        var registration = components.get(id);
        if (registration == null) {
            throw new IllegalArgumentException("Unknown GUI component ID: " + id);
        }
        return registration;
    }

    public static <P, D> void register(GuiComponentServer.Type<P, D> type, BiFunction<P, D, GuiComponentClient<P, D>> clientFactory) {
        if (components.put(type.id(), new Registration<>(type, clientFactory)) != null) {
            throw new RuntimeException("Duplicate registration of component identifier.");
        }
    }

    static {
        register(AutoExtract.TYPE, AutoExtractClient::new);
        register(CraftingMultiblockGui.TYPE, CraftingMultiblockGuiClient::new);
        register(EnergyBar.TYPE, EnergyBarClient::new);
        register(LargeTankFluidDisplay.TYPE, LargeTankFluidDisplayClient::new);
        register(MachineProblemsDisplay.TYPE, MachineProblemsDisplayClient::new);
        register(GeneratorMultiblockGui.TYPE, GeneratorMultiblockGuiClient::new);
        register(GunpowderOverclockGui.TYPE, GunpowderOverclockGuiClient::new);
        register(NuclearReactorGui.TYPE, NuclearReactorGuiClient::new);
        register(ProgressBar.TYPE, ProgressBarClient::new);
        register(ReiSlotLocking.TYPE, ReiSlotLockingClient::new);
        register(ShapeSelection.TYPE, ShapeSelectionClient::new);
        register(SlotPanel.TYPE, SlotPanelClient::new);
        register(TemperatureBar.TYPE, TemperatureBarClient::new);
    }

    public record Registration<P, D>(GuiComponentServer.Type<P, D> type, BiFunction<P, D, GuiComponentClient<P, D>> clientFactory) {
        public GuiComponentClient<P, D> readNewComponent(RegistryFriendlyByteBuf buf) {
            P params = type.paramsCodec().decode(buf);
            D data = type.dataCodec().decode(buf);
            return clientFactory.apply(params, data);
        }
    }
}
