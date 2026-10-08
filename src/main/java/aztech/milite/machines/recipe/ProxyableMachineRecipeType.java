package aztech.milite.machines.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A machine recipe type that allows adding proxies
 */
public abstract class ProxyableMachineRecipeType extends MachineRecipeType {
    public ProxyableMachineRecipeType(ResourceLocation id) {
        super(id);
    }

    private long lastUpdate = 0;
    private static final long UPDATE_INTERVAL = 20 * 1000;
    private final List<RecipeHolder<MachineRecipe>> recipeList = new ArrayList<>();

    protected abstract void fillRecipeList(Level world, List<RecipeHolder<MachineRecipe>> recipeList);

    @Override
    public final Collection<RecipeHolder<MachineRecipe>> getRecipesWithoutCache(Level level) {
        var recipes = new ArrayList<RecipeHolder<MachineRecipe>>();
        fillRecipeList(level, recipes);
        return recipes;
    }

    @Override
    public final Collection<RecipeHolder<MachineRecipe>> getRecipesWithCache(ServerLevel level) {
        long time = System.currentTimeMillis();
        if (time - lastUpdate > UPDATE_INTERVAL) {
            lastUpdate = time;
            recipeList.clear();
            fillRecipeList(level, recipeList);
        }
        return recipeList;
    }
}
