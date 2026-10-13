package aztech.milite.machines.init;

import aztech.milite.config.MIStartupConfig;
import aztech.milite.machines.recipe.MachineRecipe;
import aztech.milite.machines.recipe.MachineRecipeType;
import aztech.milite.MI;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Reports machine recipes that no machine can ever craft.
 *
 * <p>
 * A recipe states the level it needs, and a machine states the level it is. When a recipe asks for a level above the
 * highest level its machine type reaches, nothing can run it and the recipe is dead weight. This cannot be caught by the
 * compiler because the two halves live in different places, so it is checked once the recipes are loaded.
 *
 * <p>
 * Controlled by the {@code validateRecipeLevels} startup config option. It only logs, it never removes or alters a
 * recipe.
 */
public final class MachineLevelValidator {
    private MachineLevelValidator() {}

    public static void validate(MinecraftServer server) {
        if (!MIStartupConfig.INSTANCE.validateRecipeLevels.getAsBoolean()) {
            return;
        }

        var recipeManager = server.getRecipeManager();
        // Group by level so the log stays readable when a whole tier is wrong.
        Map<MachineLevel, List<String>> unreachable = new TreeMap<>();
        List<String> uncheckedTypes = new ArrayList<>();

        for (MachineRecipeType type : MIMachineRecipeTypes.getRecipeTypes()) {
            MachineLevel highest = MachineRegistrationHelper.highestLevel(type);
            if (highest == null) {
                if (!recipeManager.getAllRecipesFor(type).isEmpty()) {
                    uncheckedTypes.add(type.getId().toString());
                }
                continue;
            }
            for (var holder : recipeManager.getAllRecipesFor(type)) {
                MachineRecipe recipe = holder.value();
                if (!highest.canCraft(recipe.level)) {
                    unreachable.computeIfAbsent(recipe.level, level -> new ArrayList<>())
                            .add(holder.id() + " needs " + recipe.level + " but " + type.getId() + " only reaches " + highest);
                }
            }
        }

        if (!uncheckedTypes.isEmpty()) {
            MI.LOGGER.warn("Machine level check: no machine level registered for recipe type(s): {}", uncheckedTypes);
        }

        if (unreachable.isEmpty()) {
            MI.LOGGER.info("Machine level check: recipes with registered machine levels are reachable.");
            return;
        }

        int total = unreachable.values().stream().mapToInt(List::size).sum();
        MI.LOGGER.warn("Machine level check: {} machine recipe(s) ask for a level no machine of their type reaches.", total);
        unreachable.forEach((level, recipes) -> {
            MI.LOGGER.warn("  {} recipe(s) need the {} level:", recipes.size(), level);
            recipes.forEach(recipe -> MI.LOGGER.warn("    {}", recipe));
        });
    }
}
