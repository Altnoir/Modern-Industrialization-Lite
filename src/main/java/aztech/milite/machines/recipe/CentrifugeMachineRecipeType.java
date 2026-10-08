package aztech.milite.machines.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.Level;

import java.util.List;

public class CentrifugeMachineRecipeType extends ProxyableMachineRecipeType {
    public CentrifugeMachineRecipeType(ResourceLocation id) {
        super(id);
    }

    @Override
    protected void fillRecipeList(Level world, List<RecipeHolder<MachineRecipe>> recipeList) {
        recipeList.addAll(getManagerRecipes(world));

        for (var itemCompostable : ComposterBlock.COMPOSTABLES.keySet()) {
            var recipe = RecipeConversions.ofCompostable(itemCompostable);
            if (recipe != null) {
                recipeList.add(recipe);
            }
        }
    }
}
