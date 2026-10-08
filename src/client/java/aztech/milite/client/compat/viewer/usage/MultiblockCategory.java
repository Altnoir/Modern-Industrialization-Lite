package aztech.milite.client.compat.viewer.usage;

import aztech.milite.client.compat.viewer.abstraction.ViewerCategory;
import aztech.milite.compat.rei.machines.ReiMachineRecipes;
import aztech.milite.machines.multiblocks.ShapeTemplate;
import aztech.milite.MI;
import aztech.milite.MIItem;
import aztech.milite.MIText;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiblockCategory extends ViewerCategory<MultiblockCategory.Recipe> {
    private static final int COLUMNS = 6;
    private static final int ROWS = 2;

    protected MultiblockCategory() {
        super(Recipe.class, MI.id("multiblock_shapes"), MIText.MultiblockMaterials.text(), MIItem.WRENCH.stack(), 34 + (COLUMNS * 18),
                28 + (ROWS * 18));
    }

    @Override
    public void buildWorkstations(WorkstationConsumer consumer) {}

    @Override
    public void buildRecipes(RecipeManager recipeManager, RegistryAccess registryAccess, Consumer<Recipe> consumer) {
        for (ReiMachineRecipes.MultiblockShape entry : ReiMachineRecipes.multiblockShapes) {
            consumer.accept(new Recipe(entry.machine(), entry.shapeTemplate(), entry.alternative(), registryAccess));
        }
    }

    @Override
    public void buildLayout(Recipe recipe, LayoutBuilder builder) {
        builder.invisibleInput(Ingredient.of(recipe.controller), 1);
        builder.outputSlot((width / 2) - 8, 5).item(recipe.controller);

        builder.scrollableSlots(COLUMNS, ROWS, recipe.materials);
    }

    @Override
    public void buildWidgets(Recipe recipe, WidgetList widgets) {
        widgets.scrollableSlots(COLUMNS, ROWS, recipe.materials);
    }

    @Override
    public ResourceLocation getRecipeId(Recipe recipe) {
        return recipe.id;
    }

    protected static class Recipe {
        public final ItemStack controller;
        public final List<SizedIngredient> materials = new ArrayList<>();
        public final ResourceLocation id;

        public Recipe(ResourceLocation controller, ShapeTemplate shapeTemplate, @Nullable String alternative, RegistryAccess registries) {
            this.controller = BuiltInRegistries.ITEM.get(controller).getDefaultInstance();
            Map<Ingredient, Integer> materialCounts = new HashMap<>();

            for (var entry : shapeTemplate.simpleMembers.entrySet()) {
                var previewState = entry.getValue().getItemPreviewState(registries);
                if (!previewState.isEmpty()) {
                    materialCounts.merge(previewState, 1, Integer::sum);
                }
            }

            List<SizedIngredient> materials = materialCounts.entrySet().stream()
                    .map((entry) -> new SizedIngredient(entry.getKey(), entry.getValue()))
                    .sorted(Comparator
                            .comparing(SizedIngredient::count)
                            .reversed())
                    .toList();
            this.materials.addAll(materials);
            this.id = ResourceLocation.fromNamespaceAndPath(controller.getNamespace(),
                    "/" + controller.getPath() + "/" + materials.size() + (alternative == null ? "" : "/" + alternative));
        }
    }
}
