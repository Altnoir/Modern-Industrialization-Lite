package aztech.milite.machines.recipe;

import aztech.milite.MI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.*;

public class FurnaceMachineRecipeType extends ProxyableMachineRecipeType {
    public FurnaceMachineRecipeType(ResourceLocation id) {
        super(id);
    }

    protected void fillRecipeList(Level world, List<RecipeHolder<MachineRecipe>> recipeList) {
        recipeList.addAll(getManagerRecipes(world));
        for (var smeltingRecipe : world.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING)) {
            var recipe = RecipeConversions.ofSmelting(smeltingRecipe, this, world.registryAccess());
            recipeList.add(recipe);
        }

        recipeList.sort(Comparator.comparing(r -> r.id().getNamespace().equals(MI.ID) ? 0 : 1));
    }
}
