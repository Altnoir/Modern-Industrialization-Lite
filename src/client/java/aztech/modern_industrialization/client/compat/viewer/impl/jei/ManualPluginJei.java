/*
 * MIT License
 *
 * Copyright (c) 2026 Altnoir Null々
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package aztech.modern_industrialization.client.compat.viewer.impl.jei;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIRegistries;
import aztech.modern_industrialization.blocks.processingtable.ProcessingTableRecipe;
import aztech.modern_industrialization.blocks.processingtable.ProcessingTableScreenHandler;
import aztech.modern_industrialization.client.machines.gui.MachineScreen;
import aztech.modern_industrialization.client.screen.MIHandledScreen;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
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
        return RecipeType.create(category.getNamespace(), category.getPath(), (Class) RecipeHolder.class);
    }

    private RecipeType<RecipeHolder<ProcessingTableRecipe>> getProcessingTableCategory() {
        return RecipeType.create(ID.getNamespace(), "processing_table", (Class) RecipeHolder.class);
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

        // The Processing Table has two input slots (36 and 37) and one output slot (38).
        registration.addRecipeTransferHandler(helper.createBasicRecipeTransferInfo(
                ProcessingTableScreenHandler.class, MIRegistries.PROCESSING_TABLE_MENU.get(), getProcessingTableCategory(),
                36, 2, 0, 36));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler((Class) MIHandledScreen.class, new MIGhostIngredientHandler());
        registration.addGuiContainerHandler(MachineScreen.class,
                new MachineGuiContainerHandler(registration.getJeiHelpers().getIngredientManager(), () -> jeiRuntime));
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }
}
