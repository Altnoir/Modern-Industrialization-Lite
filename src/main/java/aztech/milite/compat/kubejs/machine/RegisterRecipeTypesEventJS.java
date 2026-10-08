package aztech.milite.compat.kubejs.machine;

import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeType;
import dev.latvian.mods.kubejs.event.KubeEvent;

public class RegisterRecipeTypesEventJS implements KubeEvent {
    public MachineRecipeType register(String type) {
        return MIMachineRecipeTypes.create(type);
    }
}
