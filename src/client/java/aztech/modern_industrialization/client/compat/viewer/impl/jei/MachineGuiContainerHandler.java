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

import aztech.modern_industrialization.client.compat.viewer.impl.MachineScreenPredicateTest;
import aztech.modern_industrialization.client.machines.gui.MachineMenuClient;
import aztech.modern_industrialization.client.machines.gui.MachineScreen;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.util.Rectangle;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

record MachineGuiContainerHandler(IIngredientManager ingredientManager, Supplier<IJeiRuntime> jeiRuntime)
        implements IGuiContainerHandler<MachineScreen> {
    @Override
    public List<Rect2i> getGuiExtraAreas(MachineScreen screen) {
        return screen.getExtraBoxes().stream().map(r -> new Rect2i(r.x(), r.y(), r.w(), r.h())).toList();
    }

    @Override
    public Collection<IGuiClickableArea> getGuiClickableAreas(MachineScreen screen, double guiMouseX, double guiMouseY) {
        MachineMenuClient screenHandler = screen.getMenu();
        ResourceLocation blockId = screenHandler.guiParams.blockId;
        List<ReiMachineRecipes.ClickAreaCategory> categories = ReiMachineRecipes.machineToClickAreaCategory.getOrDefault(blockId,
                Collections.emptyList());
        Rectangle rectangle = ReiMachineRecipes.machineToClickArea.get(blockId);

        if (categories.size() > 0 && rectangle != null && contains(rectangle, guiMouseX, guiMouseY)) {
            boolean foundSome = false;
            var result = new ArrayList<RecipeType<?>>();
            for (ReiMachineRecipes.ClickAreaCategory cac : categories) {
                if (!MachineScreenPredicateTest.test(cac.predicate, screen))
                    continue;

                var recipeManager = jeiRuntime.get().getRecipeManager();
                var recipeType = recipeManager.getRecipeType(cac.category).orElse(null);
                if (recipeType != null) {
                    if (recipeManager.createRecipeLookup(recipeType).get().anyMatch(t -> true)) {
                        result.add(recipeType);
                        foundSome = true;
                    }
                }
            }

            if (foundSome) {
                return List.of(
                        new IGuiClickableArea() {
                            @Override
                            public Rect2i getArea() {
                                return new Rect2i(rectangle.x(), rectangle.y(), rectangle.w(), rectangle.h());
                            }

                            @Override
                            public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
                                recipesGui.showTypes(result);
                            }
                        });
            }
        }

        return Collections.emptyList();
    }

    @Override
    public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(IClickableIngredientFactory factory,
            MachineScreen screen, double mouseX, double mouseY) {
        Slot slot = screen.getFocusedSlot();
        Object ingredient = getIngredientUnderMouse(slot);
        if (ingredient == null) {
            return Optional.empty();
        }

        // The slot area is in absolute screen coordinates, not gui relative ones.
        var area = new Rect2i(screen.x() + slot.x, screen.y() + slot.y, 16, 16);

        // Declared as IIngredientType<?> on purpose: the wildcard is captured when it is passed to the helper below,
        // so the helper's type variable is not widened to Object and the builder keeps its wildcard.
        IIngredientType<?> type = ingredientManager.getIngredientTypeChecked(ingredient).orElse(null);
        if (type == null) {
            return Optional.empty();
        }
        return buildClickable(factory, type, ingredient, area);
    }

    /**
     * Builds the clickable ingredient from an ingredient whose static type is {@code Object}.
     *
     * <p>
     * The shape of this method is forced by {@code Optional} being invariant while JEI's interface returns
     * {@code Optional<IClickableIngredient<?>>}. {@code factory.createBuilder} can only produce an
     * {@code IClickableIngredient<V>} for the captured type {@code V}, and {@code Optional<IClickableIngredient<V>>} is
     * not a subtype of {@code Optional<IClickableIngredient<?>>}. The element itself converts fine, so {@code map} does
     * that part; only the enclosing {@code Optional} needs an unchecked cast, which is safe because both optionals wrap
     * the very same object and {@code Optional} carries no state of its own.
     *
     * <p>
     * The cast of the ingredient to {@code V} is what lets the {@code Object} argument in; the caller has already
     * established with {@code getIngredientTypeChecked} that the ingredient really is of this type.
     */
    @SuppressWarnings("unchecked")
    private static <V> Optional<IClickableIngredient<?>> buildClickable(IClickableIngredientFactory factory, IIngredientType<V> type,
            Object ingredient, Rect2i area) {
        Optional<IClickableIngredient<V>> built = factory.createBuilder(type, (V) ingredient).buildWithArea(area);
        return (Optional<IClickableIngredient<?>>) (Optional<?>) built;
    }

    private @Nullable Object getIngredientUnderMouse(Slot slot) {
        if (slot instanceof ConfigurableFluidStack.ConfigurableFluidSlot) {
            ConfigurableFluidStack stack = ((ConfigurableFluidStack.ConfigurableFluidSlot) slot).getConfStack();
            if (stack.getAmount() > 0) {
                var fluid = stack.getResource();
                if (!fluid.isBlank()) {
                    return fluid.toStack(1);
                }
            } else if (stack.getLockedInstance() != null) {
                Fluid fluid = stack.getLockedInstance();
                if (fluid != null) {
                    return new FluidStack(fluid, 1);
                }
            }
        } else if (slot instanceof ConfigurableItemStack.ConfigurableItemSlot) {
            ConfigurableItemStack stack = ((ConfigurableItemStack.ConfigurableItemSlot) slot).getConfStack();
            // the normal stack is already handled by REI, we just need to handle the locked
            // item!
            if (stack.getLockedInstance() != null) {
                return stack.getLockedInstance().getDefaultInstance();
            }
        }
        return null;
    }

    private static boolean contains(Rectangle rectangle, double x, double y) {
        return rectangle.x() <= x && x <= rectangle.x() + rectangle.w() && rectangle.y() <= y
                && y <= rectangle.y() + rectangle.h();
    }
}
