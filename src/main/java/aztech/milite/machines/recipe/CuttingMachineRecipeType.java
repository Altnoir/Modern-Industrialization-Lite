package aztech.milite.machines.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public class CuttingMachineRecipeType extends ProxyableMachineRecipeType {
    public CuttingMachineRecipeType(ResourceLocation id) {
        super(id);
    }

    @Override
    protected void fillRecipeList(Level world, List<RecipeHolder<MachineRecipe>> recipeList) {
        // Add all regular cutting machine recipes
        recipeList.addAll(getManagerRecipes(world));
        // Add all stone cutter recipes
        for (var stonecuttingRecipe : world.getRecipeManager().getAllRecipesFor(RecipeType.STONECUTTING)) {
            var recipe = RecipeConversions.ofStonecutting(stonecuttingRecipe, this, world.registryAccess());
            if (recipe != null) {
                recipeList.add(recipe);
            }
        }
    }
}
