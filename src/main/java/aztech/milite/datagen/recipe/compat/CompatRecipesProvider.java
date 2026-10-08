package aztech.milite.datagen.recipe.compat;

import aztech.milite.datagen.recipe.MIRecipesProvider;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.machines.recipe.MachineRecipeType;
import aztech.milite.recipe.json.MIRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public abstract class CompatRecipesProvider extends MIRecipesProvider {
    private RecipeOutput consumer;
    private final String compatModId;
    protected ICondition[] conditions = null;

    public CompatRecipesProvider(PackOutput packOutput, String compatModId) {
        super(packOutput);
        this.compatModId = compatModId;
        conditions = new ICondition[] { new ModLoadedCondition(compatModId) };
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        this.consumer = consumer;
        generate();
    }

    protected abstract void generate();

    protected final String formatRecipePath(String input, String output) {
        return "%s_to_%s".formatted(input.replace('#', '_').replace(':', '_').replace('/', '_'), output.replace(':', '_'));
    }

    protected final void addCuttingRecipe(String input, String output, int outputAmount) {
        addCompatRecipe("cutting_machine/%s".formatted(formatRecipePath(input, output)), new MachineRecipeBuilder(MIMachineRecipeTypes.CUTTING_MACHINE, 2, 200)
                .addItemInput(input, 1)
                .addItemOutput(output, outputAmount));
    }

    protected final void addMiRecipe(MachineRecipeType machine, String input, String output, int outputAmount) {
        addMiRecipe(machine, input, output, outputAmount, 2, 200);
    }

    protected final void addMiRecipe(MachineRecipeType machine, String input, String output, int outputAmount, int eu, int duration) {
        addMiRecipe(machine, input, 1, output, outputAmount, eu, duration);
    }

    protected final void addMiRecipe(MachineRecipeType machine, String input, int inputAmount, String output, int outputAmount, int eu, int duration) {
        String id = "%s/%s".formatted(machine.getPath(), formatRecipePath(input, output));
        addCompatRecipe(id, new MachineRecipeBuilder(machine, eu, duration).addItemInput(input, inputAmount).addItemOutput(output, outputAmount));
    }

    protected final void addCompatRecipe(String id, MIRecipeBuilder recipeJson) {
        id = "compat/%s/%s".formatted(compatModId, id);
        recipeJson.offerTo(consumer.withConditions(conditions), id);
    }
}
