package aztech.milite.compat.kubejs.recipe;

import aztech.milite.machines.recipe.MachineRecipe;
import aztech.milite.MI;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import dev.latvian.mods.kubejs.core.ItemStackKJS;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.SimpleRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;

public class ItemOutputComponent extends SimpleRecipeComponent<MachineRecipe.ItemOutput> {
    public static final RecipeComponentType<MachineRecipe.ItemOutput> TYPE = RecipeComponentType.unit(MI.id("item_output"), ItemOutputComponent::new);

    public ItemOutputComponent(RecipeComponentType<?> type) {
        super(type, MachineRecipe.ItemOutput.CODEC, ItemWrapper.TYPE_INFO);
    }

    @Override
    public MachineRecipe.ItemOutput wrap(RecipeScriptContext cx, Object from) {
        var itemStack = ItemWrapper.wrap(cx.cx(), from);
        return new MachineRecipe.ItemOutput(ItemVariant.of(itemStack), itemStack.getCount(), 1);
    }

    @Override
    public boolean matches(RecipeMatchContext cx, MachineRecipe.ItemOutput value, ReplacementMatchInfo match) {
        return match.match() instanceof ItemMatch m && !value.variant().isBlank() && value.amount() > 0
                && m.matches(cx, value.getStack(), match.exact());
    }

    @Override
    public MachineRecipe.ItemOutput replace(RecipeScriptContext cx, MachineRecipe.ItemOutput original, ReplacementMatchInfo match,
            Object with) {
        if (matches(cx, original, match)) {
            var stack = ItemWrapper.wrap(cx.cx(), with);
            return new MachineRecipe.ItemOutput(ItemVariant.of(stack), original.amount(), original.probability());
        } else {
            return original;
        }
    }

    @Override
    public boolean isEmpty(MachineRecipe.ItemOutput value) {
        return value.getStack().isEmpty();
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, MachineRecipe.ItemOutput value) {
        if (!value.getStack().isEmpty()) {
            builder.append(ItemStackKJS.class.cast(value.getStack()).kjs$getIdLocation());
        }
    }
}
