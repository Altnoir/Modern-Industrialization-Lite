package aztech.milite.compat.kubejs.recipe;

import aztech.milite.machines.recipe.condition.CustomProcessCondition;
import aztech.milite.machines.recipe.condition.MachineProcessCondition;
import aztech.milite.machines.recipe.MachineRecipe;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiPredicate;

public class CustomConditionEventJS implements KubeEvent {
    public void registerWithIcon(String id, BiPredicate<MachineProcessCondition.Context, MachineRecipe> predicate, ItemStack icon,
            Component... description) {
        CustomProcessCondition.register(id, predicate, icon, description);
    }

    public void register(String id, BiPredicate<MachineProcessCondition.Context, MachineRecipe> predicate, Component... description) {
        registerWithIcon(id, predicate, ItemStack.EMPTY, description);
    }
}
