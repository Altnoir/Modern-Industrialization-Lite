package aztech.milite.materials.recipe.builder;

import static aztech.milite.materials.property.MaterialProperty.HARDNESS;

import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeType;
import aztech.milite.machines.recipe.MIRecipeJson;
import aztech.milite.materials.MaterialBuilder;
import aztech.milite.materials.part.PartKeyProvider;
import aztech.milite.MI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.HashMap;
import java.util.Map;

public class ShapedRecipeBuilder implements MaterialRecipeBuilder {
    public final String recipeId;
    private final MaterialBuilder.RecipeContext context;
    private boolean canceled = false;
    private final String id;
    private final ItemStack result;
    private final String[] pattern;
    private final Map<Character, Ingredient> inputs = new HashMap<>();

    public ShapedRecipeBuilder(MaterialBuilder.RecipeContext context, PartKeyProvider result, int count, String id, String... pattern) {
        this.recipeId = "craft/" + id;
        this.context = context;
        this.id = id;
        var output = context.getPart(result);
        if (output == null) {
            this.result = null;
            this.pattern = null;
            canceled = true;
        } else {
            this.result = new ItemStack(output.asItem(), count);
            this.pattern = pattern;
        }
        context.addRecipe(this);
    }

    public ShapedRecipeBuilder addPart(char key, PartKeyProvider part) {
        if (context.getPart(part) != null) {
            addInput(key, context.getPart(part).getItemId());
        } else {
            canceled = true;
        }
        return this;
    }

    public ShapedRecipeBuilder addTaggedPart(char key, PartKeyProvider part) {
        if (context.getPart(part) != null) {
            addInput(key, context.getPart(part).getTaggedItemId());
        } else {
            canceled = true;
        }
        return this;
    }

    public ShapedRecipeBuilder addInput(char key, TagKey<Item> tag) {
        return addInput(key, "#" + tag.location());
    }

    public ShapedRecipeBuilder addInput(char key, String maybeTag) {
        if (!canceled) {
            if (inputs.containsKey(key)) {
                throw new IllegalArgumentException("Key mapping is already registered: " + key);
            }
            Ingredient ingredient = maybeTag.startsWith("#")
                    ? Ingredient.of(ItemTags.create(ResourceLocation.parse(maybeTag.substring(1))))
                    : Ingredient.of(BuiltInRegistries.ITEM.get(ResourceLocation.parse(maybeTag)));
            inputs.put(key, ingredient);
        }
        return this;
    }

    public ShapedRecipeBuilder addInput(char key, ItemLike item) {
        return addInput(key, BuiltInRegistries.ITEM.getKey(item.asItem()).toString());
    }

    public ShapedRecipeBuilder exportToAssembler(int eu, int duration) {
        return exportToMachine(MIMachineRecipeTypes.ASSEMBLER, eu, duration, 1);
    }

    public ShapedRecipeBuilder exportToAssembler() {
        return exportToAssembler(8, 200);
    }

    public ShapedRecipeBuilder exportToMachine(MachineRecipeType machine, int eu, int duration, int division) {
        if (!canceled) {
            new MIRecipeBuilder(context, id, MIRecipeJson.fromShaped(machine, eu, duration, division, result, pattern, inputs));
        }

        return this;
    }

    public ShapedRecipeBuilder exportToMachine(MachineRecipeType machine) {
        return exportToMachine(machine, 2, (int) (200 * context.get(HARDNESS).timeFactor), 1);
    }

    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public String getRecipeId() {
        return recipeId;
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public void save(RecipeOutput recipeOutput) {
        if (!canceled) {
            int totalItems = 0;
            for (String row : pattern) {
                for (char c : row.toCharArray()) {
                    if (c != ' ') {
                        totalItems++;
                    }
                }
            }
            if (totalItems <= 1) {
                throw new IllegalStateException("Shaped recipe should have at least 2 items.");
            }

            String fullId = "materials/" + context.getMaterialName() + "/" + recipeId;
            recipeOutput.accept(MI.id(fullId), new ShapedRecipe(
                    "",
                    CraftingBookCategory.MISC,
                    ShapedRecipePattern.of(inputs, pattern),
                    result), null);
        }
    }
}
