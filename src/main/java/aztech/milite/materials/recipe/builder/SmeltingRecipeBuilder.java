package aztech.milite.materials.recipe.builder;

import aztech.milite.materials.MaterialBuilder;
import aztech.milite.materials.part.PartKeyProvider;
import aztech.milite.MI;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.jspecify.annotations.Nullable;

@SuppressWarnings({ "FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection", "UnusedDeclaration" })
public class SmeltingRecipeBuilder implements MaterialRecipeBuilder {
    public final String recipeId;
    private final MaterialBuilder.RecipeContext context;
    private boolean canceled = false;
    @Nullable
    private final AbstractCookingRecipe smeltingRecipe;

    public static void smeltAndBlast(MaterialBuilder.RecipeContext context, PartKeyProvider inputPart, PartKeyProvider outputPart,
            float experience) {
        if (context.hasInternalPart(inputPart) || context.hasInternalPart(outputPart)) {
            // Skip if both are external, vanilla already has the recipe
            new SmeltingRecipeBuilder(context, inputPart, outputPart, experience, false);
            new SmeltingRecipeBuilder(context, inputPart, outputPart, experience, true);
        }
    }

    public SmeltingRecipeBuilder(MaterialBuilder.RecipeContext context, PartKeyProvider inputPart, PartKeyProvider outputPart, int cookingtime,
            float experience,
            boolean blasting) {
        if (blasting) {
            this.recipeId = "smelting/" + inputPart.key() + "_to_" + outputPart.key() + "_blasting";
        } else {
            this.recipeId = "smelting/" + inputPart.key() + "_to_" + outputPart.key() + "_smelting";
        }

        this.context = context;
        var input = context.getPart(inputPart);
        var output = context.getPart(outputPart);
        if (input == null || output == null) {
            canceled = true;
            this.smeltingRecipe = null;
        } else {
            AbstractCookingRecipe.Factory<?> factory = blasting ? BlastingRecipe::new : SmeltingRecipe::new;
            this.smeltingRecipe = factory.create(
                    "",
                    CookingBookCategory.MISC,
                    Ingredient.of(input.asItem()),
                    output.asItem().getDefaultInstance(),
                    experience,
                    cookingtime);
            context.addRecipe(this);
        }
    }

    public SmeltingRecipeBuilder(MaterialBuilder.RecipeContext context, PartKeyProvider partInput, PartKeyProvider partOutput, float experience,
            boolean blasting) {
        this(context, partInput, partOutput, blasting ? 100 : 200, experience, blasting);
    }

    @Override
    public String getRecipeId() {
        return recipeId;
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void save(RecipeOutput recipeOutput) {
        if (!canceled) {
            String fullId = "materials/" + context.getMaterialName() + "/" + recipeId;
            recipeOutput.accept(MI.id(fullId), smeltingRecipe, null);
        }
    }
}
