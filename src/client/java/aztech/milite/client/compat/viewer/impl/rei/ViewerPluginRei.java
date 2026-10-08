package aztech.milite.client.compat.viewer.impl.rei;

import aztech.milite.client.compat.viewer.usage.ViewerSetup;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

import java.util.ArrayList;
import java.util.List;

@REIPluginClient
public class ViewerPluginRei implements REIClientPlugin {
    private final List<ViewerCategoryRei<?>> categories = new ArrayList<>();

    @Override
    public void registerCategories(CategoryRegistry registry) {
        categories.clear(); // needed for reloads

        for (var category : ViewerSetup.setup()) {
            categories.add(new ViewerCategoryRei<>(category));
        }

        registry.add(categories.toArray(new DisplayCategory[0]));

        for (var category : categories) {
            category.wrapped.buildWorkstations(items -> {
                for (var item : items) {
                    registry.addWorkstations(category.identifier, EntryStacks.of(item));
                }
            });
        }
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for (var category : categories) {
            category.registerRecipes(registry);
        }
    }
}
