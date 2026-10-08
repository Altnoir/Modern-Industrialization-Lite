package aztech.milite.compat.kubejs.recipe;

import aztech.milite.machines.recipe.condition.MachineProcessCondition;
import aztech.milite.MI;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.SimpleRecipeComponent;
import dev.latvian.mods.rhino.type.TypeInfo;

public class MachineProcessConditionComponent extends SimpleRecipeComponent<MachineProcessCondition> {
    public static final RecipeComponentType<MachineProcessCondition> TYPE = RecipeComponentType.unit(MI.id("machine_process_condition"),
            MachineProcessConditionComponent::new);

    public MachineProcessConditionComponent(RecipeComponentType<?> type) {
        super(type, MachineProcessCondition.CODEC, TypeInfo.of(MachineProcessCondition.class));
    }
}
