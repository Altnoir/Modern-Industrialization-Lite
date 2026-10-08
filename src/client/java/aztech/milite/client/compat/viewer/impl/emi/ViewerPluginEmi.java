package aztech.milite.client.compat.viewer.impl.emi;

import aztech.milite.client.compat.viewer.usage.ViewerSetup;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;

@EmiEntrypoint
public class ViewerPluginEmi implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        for (var category : ViewerSetup.setup()) {
            var emiCategory = new ViewerCategoryEmi<>(category);
            registry.addCategory(emiCategory);

            category.buildWorkstations(items -> {
                for (var item : items) {
                    registry.addWorkstation(emiCategory, EmiStack.of(item));
                }
            });

            emiCategory.registerRecipes(registry);
        }
    }
}
