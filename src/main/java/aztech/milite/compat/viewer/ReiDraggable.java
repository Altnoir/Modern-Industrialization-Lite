package aztech.milite.compat.viewer;

import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.milite.util.Simulation;

/**
 * Can be implemented by slots or {@code AbstractWidget}
 * if they want to support dragging. For buttons: called client-side only. For
 * slots: called on both sides if the client accepts the simulation. Return true
 * if dragging is accepted.
 */
public interface ReiDraggable {
    boolean dragFluid(FluidVariant fluidKey, Simulation simulation);

    boolean dragItem(ItemVariant itemKey, Simulation simulation);
}
