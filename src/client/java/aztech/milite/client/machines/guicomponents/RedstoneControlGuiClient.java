package aztech.milite.client.machines.guicomponents;

import aztech.milite.client.machines.gui.ClientComponentRenderer;
import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.machines.guicomponents.RedstoneControlGui;
import aztech.milite.MIText;
import aztech.milite.network.machines.SetRedstoneControlPacket;
import aztech.milite.util.TextHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;

import java.util.List;

public class RedstoneControlGuiClient extends GuiComponentClient<Unit, RedstoneControlGui.Data> {
    private static final int SIGNAL_U = 0;
    private static final int ENABLE_U = 12;
    private static final int V_TOP = 64;
    private static final int V_BOTTOM = 76;
    private static final int BUTTON_SIZE = 12;

    public RedstoneControlGuiClient(Unit params, RedstoneControlGui.Data data) {
        super(params, data);
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        return new Renderer();
    }

    private class Renderer implements ClientComponentRenderer {
        @Override
        public void addButtons(ButtonContainer container) {
            addEnableButton(container);
            addSignalButton(container);
        }

        private void addEnableButton(ButtonContainer container) {
            container.addButton((screen, button, guiGraphics, mouseX, mouseY, partialTicks) -> guiGraphics.blit(MachineScreen.SLOT_ATLAS,
                    button.getX(), button.getY(), ENABLE_U, data.enabled() ? V_BOTTOM : V_TOP, BUTTON_SIZE, BUTTON_SIZE),
                    syncId -> {
                        data = new RedstoneControlGui.Data(!data.enabled(), data.requiresLowSignal());
                        new SetRedstoneControlPacket(syncId, data.enabled(), data.requiresLowSignal()).sendToServer();
                    }, () -> List.of(
                            (data.enabled() ? MIText.RedstoneControlOn : MIText.RedstoneControlOff).text(),
                            (data.enabled() ? MIText.ClickToDisable : MIText.ClickToEnable).text().setStyle(TextHelper.GRAY_TEXT)),
                    () -> true);
        }

        private void addSignalButton(ButtonContainer container) {
            container.addButton((screen, button, guiGraphics, mouseX, mouseY, partialTicks) -> guiGraphics.blit(MachineScreen.SLOT_ATLAS,
                    button.getX(), button.getY(), SIGNAL_U, data.requiresLowSignal() ? V_BOTTOM : V_TOP, BUTTON_SIZE, BUTTON_SIZE),
                    syncId -> {
                        data = new RedstoneControlGui.Data(data.enabled(), !data.requiresLowSignal());
                        new SetRedstoneControlPacket(syncId, data.enabled(), data.requiresLowSignal()).sendToServer();
                    }, () -> List.of(
                            (data.requiresLowSignal() ? MIText.SignalLow : MIText.SignalHigh).text(),
                            MIText.ClickToSwitch.text(data.requiresLowSignal() ? MIText.SignalHigh.text()
                                    : MIText.SignalLow.text()).setStyle(TextHelper.GRAY_TEXT)),
                    () -> data.enabled());
        }

        @Override
        public void renderBackground(GuiGraphics guiGraphics, int x, int y) {}
    }
}
