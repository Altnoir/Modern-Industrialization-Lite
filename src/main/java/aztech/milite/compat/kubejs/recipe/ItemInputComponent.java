package aztech.milite.compat.kubejs.recipe;

import aztech.milite.machines.recipe.MachineRecipe;
import aztech.milite.MI;
import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.core.RegistryObjectKJS;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.IngredientWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.SizedIngredientWrapper;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.SimpleRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;

public class ItemInputComponent extends SimpleRecipeComponent<MachineRecipe.ItemInput> {
    public static final RecipeComponentType<MachineRecipe.ItemInput> TYPE = RecipeComponentType.unit(MI.id("item_input"), ItemInputComponent::new);

    public ItemInputComponent(RecipeComponentType<?> type) {
        super(type, MachineRecipe.ItemInput.CODEC, SizedIngredientWrapper.TYPE_INFO);
    }

    @Override
    public MachineRecipe.ItemInput wrap(RecipeScriptContext cx, Object from) {
        var sizedIngredient = SizedIngredientWrapper.wrap(cx.cx(), from);
        return new MachineRecipe.ItemInput(sizedIngredient.ingredient(), sizedIngredient.count(), 1);
    }

    @Override
    public boolean matches(RecipeMatchContext cx, MachineRecipe.ItemInput value, ReplacementMatchInfo match) {
        return match.match() instanceof ItemMatch m && m.matches(cx, value.ingredient(), match.exact());
    }

    @Override
    public MachineRecipe.ItemInput replace(RecipeScriptContext cx, MachineRecipe.ItemInput original, ReplacementMatchInfo match,
            Object with) {
        if (matches(cx, original, match)) {
            var withJava = IngredientWrapper.wrap(cx.cx(), with);
            return new MachineRecipe.ItemInput(withJava, original.amount(), original.probability());
        } else {
            return original;
        }
    }

    @Override
    public boolean isEmpty(MachineRecipe.ItemInput value) {
        return value.amount() <= 0 || value.ingredient().isEmpty();
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, MachineRecipe.ItemInput value) {
        var tag = IngredientWrapper.tagKeyOf(value.ingredient());

        if (tag != null) {
            builder.append(tag.location());
        } else {
            var first = IngredientKJS.class.cast(value.ingredient()).kjs$getFirst();

            if (!first.isEmpty()) {
                builder.append(RegistryObjectKJS.class.cast(first).kjs$getIdLocation());
            }
        }
    }
}
