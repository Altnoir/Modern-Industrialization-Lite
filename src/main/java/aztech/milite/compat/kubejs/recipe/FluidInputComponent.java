package aztech.milite.compat.kubejs.recipe;

import aztech.milite.machines.recipe.MachineRecipe;
import aztech.milite.MI;
import dev.latvian.mods.kubejs.core.RegistryObjectKJS;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.SimpleRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.FluidMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;

public class FluidInputComponent extends SimpleRecipeComponent<MachineRecipe.FluidInput> {
    public static final RecipeComponentType<MachineRecipe.FluidInput> TYPE = RecipeComponentType.unit(MI.id("fluid_input"), FluidInputComponent::new);

    public FluidInputComponent(RecipeComponentType<?> type) {
        super(type, MachineRecipe.FluidInput.CODEC, FluidWrapper.SIZED_INGREDIENT_TYPE_INFO);
    }

    @Override
    public MachineRecipe.FluidInput wrap(RecipeScriptContext cx, Object from) {
        var fs = FluidWrapper.wrapSizedIngredient(cx.cx(), from);
        return new MachineRecipe.FluidInput(fs.ingredient(), fs.amount(), 1);
    }

    @Override
    public boolean matches(RecipeMatchContext cx, MachineRecipe.FluidInput value, ReplacementMatchInfo match) {
        return match.match() instanceof FluidMatch m && m.matches(cx, value.fluid(), match.exact());
    }

    @Override
    public MachineRecipe.FluidInput replace(RecipeScriptContext cx, MachineRecipe.FluidInput original, ReplacementMatchInfo match,
            Object with) {
        if (matches(cx, original, match)) {
            var fi = FluidWrapper.wrapIngredient(cx.cx(), with);
            return new MachineRecipe.FluidInput(fi, original.amount(), original.probability());
        } else {
            return original;
        }
    }

    @Override
    public boolean isEmpty(MachineRecipe.FluidInput value) {
        return value.amount() <= 0 || value.fluid().isEmpty();
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, MachineRecipe.FluidInput value) {
        if (!isEmpty(value)) {
            builder.append(((RegistryObjectKJS) value.fluid().getStacks()[0].getFluid()).kjs$getIdLocation());
        }
    }
}
