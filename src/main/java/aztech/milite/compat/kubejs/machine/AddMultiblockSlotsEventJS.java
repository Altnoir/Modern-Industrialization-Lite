package aztech.milite.compat.kubejs.machine;

import aztech.milite.inventory.SlotPositions;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.util.HideFromJS;

public record AddMultiblockSlotsEventJS(SlotPositions.Builder itemInputs, SlotPositions.Builder itemOutputs,
                                        SlotPositions.Builder fluidInputs,
                                        SlotPositions.Builder fluidOutputs) implements KubeEvent {
    @HideFromJS
    public AddMultiblockSlotsEventJS {
    }
}
