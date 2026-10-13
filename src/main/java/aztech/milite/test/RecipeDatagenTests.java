package aztech.milite.test;

import aztech.milite.MI;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.recipe.MachineRecipe;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.test.framework.MIGameTest;
import aztech.milite.test.framework.MIGameTestHelper;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecipeDatagenTests {
    @MIGameTest
    public void machineRecipeLevelStringsRoundTrip(MIGameTestHelper helper) {
        var captured = new ArrayList<MachineRecipe>();
        RecipeOutput output = new RecipeOutput() {
            @Override
            public Advancement.Builder advancement() {
                return Advancement.Builder.recipeAdvancement();
            }

            @Override
            public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
                captured.add((MachineRecipe) recipe);
            }
        };
        var codec = MIMachineRecipeTypes.MACERATOR.codec().codec();
        for (MachineLevel level : MachineLevel.values()) {
            new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 8, 100)
                    .level(level.getSerializedName())
                    .addItemInput(Items.STONE, 1)
                    .addItemOutput(Items.COBBLESTONE, 1)
                    .offerTo(output, "test/level");
            var json = codec.encodeStart(JsonOps.INSTANCE, captured.getLast()).getOrThrow();
            if (codec.parse(JsonOps.INSTANCE, json).getOrThrow().level != level) {
                helper.fail("String level did not survive serialization: " + level);
            }
        }
        try {
            new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 8, 100).level("electirc");
            helper.fail("Unknown string level was accepted");
        } catch (IllegalArgumentException expected) {
            // Typos must fail rather than fall back to a different machine level.
        }
        helper.succeed();
    }

    @MIGameTest
    public void generatedShapedRecipesKeepAssemblerCounterparts(MIGameTestHelper helper) {
        var manager = helper.getLevel().getRecipeManager();
        int converted = 0;
        for (var holder : manager.getRecipes()) {
            if (!holder.id().getNamespace().equals(MI.ID) || !holder.id().getPath().endsWith("_asbl")) {
                continue;
            }
            if (!(holder.value() instanceof ShapedRecipe shaped)) {
                helper.fail("Assembler source is not a shaped recipe: " + holder.id());
                return;
            }
            String path = holder.id().getPath();
            var counterpartId = MI.id("assembler_generated/" + path.substring(0, path.length() - "_asbl".length()));
            var counterpart = manager.byKey(counterpartId).orElse(null);
            if (counterpart == null || !(counterpart.value() instanceof MachineRecipe assembler)
                    || assembler.getType() != MIMachineRecipeTypes.ASSEMBLER) {
                helper.fail("Missing assembler counterpart " + counterpartId);
                return;
            }
            if (assembler.energy != 8 || assembler.duration != 200 || assembler.level != MachineLevel.ELECTRIC) {
                helper.fail("Assembler timing or level changed: " + counterpartId);
            }
            var result = shaped.getResultItem(helper.getLevel().registryAccess());
            if (assembler.itemOutputs.size() != 1 || assembler.itemOutputs.getFirst().probability() != 1
                    || assembler.itemOutputs.getFirst().amount() != result.getCount()
                    || assembler.itemOutputs.getFirst().variant().getItem() != result.getItem()) {
                helper.fail("Assembler result differs from shaped recipe: " + counterpartId);
            }

            Map<String, Integer> expected = new HashMap<>();
            for (String row : shaped.pattern.data.get().pattern()) {
                for (char key : row.toCharArray()) {
                    if (key != ' ') {
                        expected.merge(ingredientKey(shaped.pattern.data.get().key().get(key)), 1, Integer::sum);
                    }
                }
            }
            Map<String, Integer> actual = new HashMap<>();
            for (var input : assembler.itemInputs) {
                if (input.probability() != 1) {
                    helper.fail("Assembler input probability changed: " + counterpartId);
                }
                actual.merge(ingredientKey(input.ingredient()), input.amount(), Integer::sum);
            }
            if (!actual.equals(expected) || !assembler.fluidInputs.isEmpty() || !assembler.fluidOutputs.isEmpty()) {
                helper.fail("Assembler ingredients differ from shaped recipe: " + counterpartId);
            }
            converted++;
        }
        if (converted == 0) {
            helper.fail("No generated _asbl recipes were loaded");
        }
        helper.succeed();
    }

    private static String ingredientKey(Ingredient ingredient) {
        return Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, ingredient).getOrThrow().toString();
    }

    @MIGameTest
    public void generatedRecipesPreserveLegacyDefaultLevel(MIGameTestHelper helper) {
        // Energy used to set the builder's default to electric, while these JSON recipes loaded as bronze.
        for (String path : new String[] {"trash_can_assembler", "quarry/bronze", "quarry/steel", "quarry/titanium"}) {
            var holder = helper.getLevel().getRecipeManager().byKey(MI.id(path)).orElse(null);
            if (holder == null || !(holder.value() instanceof MachineRecipe recipe) || recipe.level != MachineLevel.BRONZE) {
                helper.fail("Migration changed the legacy bronze level of " + path);
            }
        }
        helper.succeed();
    }
}
