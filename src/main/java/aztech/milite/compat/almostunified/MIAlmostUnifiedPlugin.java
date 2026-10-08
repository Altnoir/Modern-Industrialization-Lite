package aztech.milite.compat.almostunified;

import aztech.milite.MI;
import com.almostreliable.unified.api.plugin.AlmostUnifiedNeoPlugin;
import com.almostreliable.unified.api.plugin.AlmostUnifiedPlugin;
import com.almostreliable.unified.api.unification.recipe.RecipeUnifierRegistry;
import net.minecraft.resources.ResourceLocation;

@AlmostUnifiedNeoPlugin
public class MIAlmostUnifiedPlugin implements AlmostUnifiedPlugin {
    @Override
    public ResourceLocation getPluginId() {
        return MI.id("almost_unified");
    }

    @Override
    public void registerRecipeUnifiers(RecipeUnifierRegistry registry) {
        registry.registerForModId(MI.ID, new MIRecipeUnifier());
    }
}
