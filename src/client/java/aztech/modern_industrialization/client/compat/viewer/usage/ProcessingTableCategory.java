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

package aztech.modern_industrialization.client.compat.viewer.usage;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.MIRegistries;
import aztech.modern_industrialization.blocks.processingtable.ProcessingTableRecipe;
import aztech.modern_industrialization.client.compat.viewer.abstraction.ViewerCategory;
import java.util.Comparator;
import java.util.function.Consumer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

/**
 * Recipe viewer category of the Processing Table: two inputs make one machine.
 *
 * <p>
 * The geometry deliberately matches {@link ForgeHammerCategory}: same 150x40 box, the first input
 * on the Forge Hammer's input position and the arrow on the Forge Hammer's arrow position, so both
 * hand-crafted stations look alike in EMI, JEI and REI. No tooltip is registered.
 */
public class ProcessingTableCategory extends ViewerCategory<RecipeHolder<ProcessingTableRecipe>> {
    public static final ResourceLocation ID = MI.id("processing_table");

    private static final int WIDTH = 150;
    private static final int HEIGHT = 40;

    private final int startPointX;
    private final int startPointY;

    public ProcessingTableCategory() {
        super((Class) RecipeHolder.class, ID, MIBlock.PROCESSING_TABLE.asBlock().getName(),
                MIBlock.PROCESSING_TABLE.asItem().getDefaultInstance(), WIDTH, HEIGHT);

        // Same anchors as the Forge Hammer category.
        this.startPointX = WIDTH / 2 - 25;
        this.startPointY = HEIGHT / 2 - 18;
    }

    @Override
    public void buildWorkstations(WorkstationConsumer consumer) {
        consumer.accept(MIBlock.PROCESSING_TABLE);
    }

    @Override
    public void buildRecipes(RecipeManager recipeManager, RegistryAccess registryAccess, Consumer<RecipeHolder<ProcessingTableRecipe>> consumer) {
        recipeManager.getAllRecipesFor(MIRegistries.PROCESSING_TABLE_RECIPE_TYPE.get())
                .stream()
                .sorted(Comparator.comparing(RecipeHolder::id))
                .forEach(consumer);
    }

    @Override
    public void buildLayout(RecipeHolder<ProcessingTableRecipe> holder, LayoutBuilder builder) {
        var recipe = holder.value();

        // The Forge Hammer puts its input on startPointX + 5. The Processing Table needs a second
        // input, so the two are stacked in the column the Forge Hammer gives to its input and tool.
        builder.inputSlot(startPointX + 5, startPointY + 6).ingredient(recipe.first(), 1, 1);
        builder.inputSlot(startPointX + 5, startPointY + 24).ingredient(recipe.second(), 1, 1);
        builder.outputSlot(startPointX + 62, startPointY + 15).item(recipe.result());
    }

    @Override
    public void buildWidgets(RecipeHolder<ProcessingTableRecipe> holder, WidgetList widgets) {
        // Same arrow position and size as the Forge Hammer category, centred on the two inputs.
        widgets.arrow(startPointX + 27, startPointY + 13);
    }

    @Override
    public ResourceLocation getRecipeId(RecipeHolder<ProcessingTableRecipe> recipe) {
        return recipe.id();
    }
}
