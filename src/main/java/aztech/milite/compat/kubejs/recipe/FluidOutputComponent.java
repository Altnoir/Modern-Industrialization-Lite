package aztech.milite.compat.kubejs.recipe;

import aztech.milite.machines.recipe.MachineRecipe;
import aztech.milite.MI;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.SimpleRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.FluidMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidOutputComponent extends SimpleRecipeComponent<MachineRecipe.FluidOutput> {
    public static final RecipeComponentType<MachineRecipe.FluidOutput> TYPE = RecipeComponentType.unit(MI.id("fluid_output"),
            FluidOutputComponent::new);

    public FluidOutputComponent(RecipeComponentType<?> type) {
        super(type, MachineRecipe.FluidOutput.CODEC, FluidWrapper.TYPE_INFO);
    }

    @Override
    public MachineRecipe.FluidOutput wrap(RecipeScriptContext cx, Object from) {
        var fs = FluidWrapper.wrap(cx.cx(), from);
        return new MachineRecipe.FluidOutput(fs.getFluid(), fs.getAmount(), 1);
    }

    @Override
    public boolean matches(RecipeMatchContext cx, MachineRecipe.FluidOutput value, ReplacementMatchInfo match) {
        return match.match() instanceof FluidMatch m && m.matches(cx, new FluidStack(value.fluid(), 1), match.exact());
    }

    @Override
    public MachineRecipe.FluidOutput replace(RecipeScriptContext cx, MachineRecipe.FluidOutput original, ReplacementMatchInfo match,
            Object with) {
        if (matches(cx, original, match)) {
            var fs = FluidWrapper.wrap(cx.cx(), with);
            return new MachineRecipe.FluidOutput(fs.getFluid(), original.amount(), original.probability());
        } else {
            return original;
        }
    }

    @Override
    public boolean isEmpty(MachineRecipe.FluidOutput value) {
        return value.amount() <= 0 || value.fluid().isSame(Fluids.EMPTY);
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, MachineRecipe.FluidOutput value) {
        if (!isEmpty(value)) {
            builder.append(value.fluid().builtInRegistryHolder().getKey().location());
        }
    }
}
