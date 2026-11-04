package aztech.milite.client.compat.viewer.impl.jei;

import aztech.milite.blocks.processingtable.ProcessingTableRecipe;
import aztech.milite.client.compat.viewer.abstraction.ViewerCategory;
import aztech.milite.client.blocks.processingtable.ProcessingTableScreen;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.client.screen.MIHandledScreen;
import aztech.milite.compat.rei.machines.ReiMachineRecipes;
import aztech.milite.machines.recipe.MachineRecipe;
import aztech.milite.MI;
import me.shedaniel.rei.plugincompatibilities.api.REIPluginCompatIgnore;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

@JeiPlugin
@REIPluginCompatIgnore
public class ManualPluginJei implements IModPlugin {
    private static final ResourceLocation ID = MI.id("manual");

    private IJeiRuntime jeiRuntime;

    private RecipeType<RecipeHolder<MachineRecipe>> getMachineCategory(ResourceLocation category) {
        return RecipeType.create(category.getNamespace(), category.getPath(), ViewerCategory.recipeHolderClass());
    }

    static RecipeType<RecipeHolder<ProcessingTableRecipe>> processingTableType() {
        return RecipeType.create(ID.getNamespace(), "processing_table", ViewerCategory.recipeHolderClass());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        this.jeiRuntime = jeiRuntime;
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        var helper = registration.getTransferHelper();
        for (var entry : ReiMachineRecipes.categories.entrySet()) {
            var type = getMachineCategory(entry.getKey());

            registration.addRecipeTransferHandler(new MachineSlotLockingHandler(helper, () -> jeiRuntime, type), type);
        }

        var processingTableType = processingTableType();
        registration.addRecipeTransferHandler(new ProcessingTableTransferHandler(processingTableType), processingTableType);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler((Class) MIHandledScreen.class, new MIGhostIngredientHandler());
        registration.addGuiContainerHandler(MachineScreen.class,
                new MachineGuiContainerHandler(registration.getJeiHelpers().getIngredientManager(), () -> jeiRuntime));
        registration.addGuiContainerHandler(ProcessingTableScreen.class, new ProcessingTableGuiContainerHandler());
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }
}
