package aztech.milite.datagen.recipe;

import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;

import java.util.List;

/**
 * Recipes that turn one tier of gear into the next.
 *
 * <p>
 * This used to also generate the steel upgrade conversions, where a bronze machine plus an upgrade item became the steel
 * machine. Machines exist at every level on their own now, so those recipes are gone and only the quantum armour
 * conversions remain.
 */
public class UpgradeProvider extends MIRecipesProvider {
    public UpgradeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        buildQuantumArmour(consumer);
    }

    private static final List<String> QUANTUM_ITEMS = List.of("helmet", "chestplate", "leggings", "boots", "sword");

    /**
     * Quantum armour is the top of the armour ladder, so it is only craftable once an iridium packer exists.
     */
    private void buildQuantumArmour(RecipeOutput consumer) {
        for (var itemType : QUANTUM_ITEMS) {
            var packerRecipe = new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 1_000_000, 200)
                    .level(MachineLevel.IRIDIUM)
                    .addItemInput("minecraft:netherite_" + itemType, 1)
                    .addItemInput("moderntech:quantum_upgrade", 1)
                    .addItemOutput("moderntech:quantum_" + itemType, 1);
            packerRecipe.offerTo(consumer, "upgrade/packer/quantum/" + itemType);
        }
    }
}
