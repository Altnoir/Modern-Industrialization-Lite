package aztech.milite.client.machines.gui;

import aztech.milite.machines.gui.GuiComponent;

/**
 * Client part of a synced component.
 */
public abstract class GuiComponentClient<P, D> implements GuiComponent {
    protected final P params;
    protected D data;

    protected GuiComponentClient(P params, D data) {
        this.params = params;
        this.data = data;
    }

    /**
     * @return A new renderer linked to this client-side component.
     */
    public abstract ClientComponentRenderer createRenderer(MachineScreen machineScreen);
}
