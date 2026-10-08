package aztech.milite.recipe.json;

import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.machines.recipe.MachineRecipeType;
import aztech.milite.MI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection", "UnusedDeclaration" })
public class ShapedRecipeJson implements MIRecipeBuilder {
    public final String type = "minecraft:crafting_shaped";
    public final String[] pattern;
    public final Map<Character, Ingredient> key = new HashMap<>();
    public final ItemStack result;

    public ShapedRecipeJson(String resultItem, int count, String... pattern) {
        this.pattern = pattern;
        this.result = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(resultItem)), count);
    }

    public ShapedRecipeJson addInput(char key, String maybeTag) {
        Ingredient input;
        if (maybeTag.startsWith("#")) {
            input = Ingredient.of(ItemTags.create(ResourceLocation.parse(maybeTag.substring(1))));
        } else {
            input = Ingredient.of(BuiltInRegistries.ITEM.get(ResourceLocation.parse(maybeTag)));
        }
        if (this.key.put(key, input) != null) {
            throw new IllegalStateException("Key mapping is already registered: " + key);
        }
        return this;
    }

    public void validate() {
        // check pattern size
        if (pattern.length == 0 || pattern.length > 3) {
            throw new IllegalArgumentException("Invalid length " + pattern.length);
        }
        for (String string : pattern) {
            if (string.length() != pattern[0].length()) {
                throw new IllegalArgumentException("Pattern length mismatch: " + string.length() + ", expected " + pattern[0].length());
            }
        }
        // check mapping
        for (String string : pattern) {
            for (int i = 0; i < string.length(); ++i) {
                if (string.charAt(i) != ' ' && !key.containsKey(string.charAt(i))) {
                    throw new IllegalArgumentException("Key " + string.charAt(i) + " is missing a mapping.");
                }
            }
        }
        for (char c : key.keySet()) {
            boolean ok = false;
            for (String string : pattern) {
                for (int i = 0; i < string.length(); ++i) {
                    if (string.charAt(i) == c) {
                        ok = true;
                        break;
                    }
                }
            }
            if (!ok) {
                throw new IllegalArgumentException("Key mapping '" + c + "' is not used in the pattern.");
            }
        }
    }

    public MachineRecipeBuilder exportToAssembler() {
        return exportToMachine(MIMachineRecipeTypes.ASSEMBLER, 8, 200, 1).level(MachineLevel.ELECTRIC);
    }

    /**
     * @param level the machine level the exported recipe needs, since the machine it is exported to cannot be inferred
     *              from the recipe itself
     */
    public MachineRecipeBuilder exportToMachine(MachineRecipeType machine, int rf, int duration, int division,
            MachineLevel level) {
        return exportToMachine(machine, rf, duration, division).level(level);
    }

    public MachineRecipeBuilder exportToMachine(MachineRecipeType machine, int rf, int duration, int division) {
        if (result.getCount() % division != 0) {
            throw new IllegalArgumentException("Output must be divisible by division");
        }

        var assemblerJson = new MachineRecipeBuilder(machine, rf, duration).addItemOutput(result.getItem(), result.getCount() / division);
        for (Map.Entry<Character, Ingredient> entry : key.entrySet()) {
            int count = 0;
            for (String row : pattern) {
                for (char c : row.toCharArray()) {
                    if (c == entry.getKey()) {
                        count++;
                    }
                }
            }

            if (count % division != 0) {
                throw new IllegalArgumentException("Input must be divisible by division");
            }

            assemblerJson.addItemInput(entry.getValue(), count / division, 1);
        }

        return assemblerJson;
    }

    @Override
    public void offerTo(RecipeOutput recipeOutput, String path) {
        recipeOutput.accept(MI.id(path), new ShapedRecipe(
                "",
                CraftingBookCategory.MISC,
                ShapedRecipePattern.of(key, pattern),
                result), null);
    }
}
