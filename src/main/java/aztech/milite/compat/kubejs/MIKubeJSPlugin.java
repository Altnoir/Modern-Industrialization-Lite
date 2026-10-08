package aztech.milite.compat.kubejs;

import aztech.milite.compat.kubejs.machine.MIMachineKubeJSEvents;
import aztech.milite.compat.kubejs.material.MIMaterialKubeJSEvents;
import aztech.milite.compat.kubejs.recipe.*;
import aztech.milite.compat.kubejs.registration.MIRegistrationKubeJSEvents;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.condition.CustomProcessCondition;
import aztech.milite.MIRegistries;
import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.core.RecipeManagerKJS;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentTypeRegistry;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeFactoryRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class MIKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void init() {}

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(MIMachineKubeJSEvents.EVENT_GROUP);
        registry.register(MIMaterialKubeJSEvents.EVENT_GROUP);
        registry.register(MIRecipeKubeJSEvents.EVENT_GROUP);
        registry.register(MIRegistrationKubeJSEvents.EVENT_GROUP);
    }

    @Override
    public void initStartup() {
        KubeJSProxy.instance = new LoadedKubeJSProxy();
    }

    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry registry) {
        for (var mrt : MIMachineRecipeTypes.getRecipeTypes()) {
            registry.register(mrt.getId(), MachineRecipeSchema.SCHEMA);
        }
        registry.register(MIRegistries.FORGE_HAMMER_RECIPE_TYPE.getId(), MachineRecipeSchema.FORGE_HAMMER_SCHEMA);
    }

    @Override
    public void registerRecipeFactories(RecipeFactoryRegistry registry) {
        registry.register(MachineKubeRecipe.FACTORY);
    }

    @Override
    public void registerRecipeComponents(RecipeComponentTypeRegistry registry) {
        registry.register(ItemOutputComponent.TYPE);
        registry.register(FluidOutputComponent.TYPE);
        registry.register(ItemInputComponent.TYPE);
        registry.register(FluidInputComponent.TYPE);
        registry.register(MachineProcessConditionComponent.TYPE);
    }

    @Override
    public void beforeRecipeLoading(RecipesKubeEvent event, RecipeManagerKJS manager, Map<ResourceLocation, JsonElement> recipeJsons) {
        CustomProcessCondition.onReload();
    }
}
