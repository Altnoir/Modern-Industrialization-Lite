package aztech.milite.client.machines.guicomponents;

import aztech.milite.client.machines.gui.ClientComponentRenderer;
import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.gui.MachineScreen;
import net.minecraft.util.Unit;

public class ReiSlotLockingClient extends GuiComponentClient<Unit, Boolean> {
    public ReiSlotLockingClient(Unit params, Boolean data) {
        super(params, data);
    }

    public boolean isLockingAllowed() {
        return data;
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        // nothing to do;
        return (guiGraphics, x, y) -> {};
    }
}
