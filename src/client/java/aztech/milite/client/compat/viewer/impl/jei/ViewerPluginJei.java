package aztech.milite.client.compat.viewer.impl.jei;

import aztech.milite.client.compat.viewer.usage.ViewerSetup;
import aztech.milite.MI;
import me.shedaniel.rei.plugincompatibilities.api.REIPluginCompatIgnore;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
@REIPluginCompatIgnore
public class ViewerPluginJei implements IModPlugin {
    private static final ResourceLocation ID = MI.id("viewer");

    private final List<ViewerCategoryJei<?>> categories = new ArrayList<>();

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        categories.clear(); // needed for reloads

        for (var category : ViewerSetup.setup()) {
            categories.add(new ViewerCategoryJei<>(registration.getJeiHelpers(), category));
        }

        registration.addRecipeCategories(categories.toArray(new IRecipeCategory[0]));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (var category : categories) {
            category.wrapped.buildWorkstations(items -> {
                for (var item : items) {
                    registration.addRecipeCatalyst(item.asItem().getDefaultInstance(), category.recipeType);
                }
            });
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        for (var category : categories) {
            registerCategoryRecipes(registration, category);
        }
    }

    private static <D> void registerCategoryRecipes(IRecipeRegistration registration, ViewerCategoryJei<D> category) {
        var level = Minecraft.getInstance().level;
        List<D> recipes = new ArrayList<>();
        category.wrapped.buildRecipes(level.getRecipeManager(), level.registryAccess(), recipes::add);
        registration.addRecipes(category.recipeType, recipes);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }
}
