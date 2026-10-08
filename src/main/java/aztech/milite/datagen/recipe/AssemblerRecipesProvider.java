package aztech.milite.datagen.recipe;

import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.machines.recipe.MIRecipeJson;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class AssemblerRecipesProvider extends MIRecipesProvider {
    private static final Gson GSON = new Gson();

    private final PackOutput packOutput;

    public AssemblerRecipesProvider(PackOutput packOutput) {
        super(packOutput);
        this.packOutput = packOutput;
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        var nonGeneratedResources = packOutput.getOutputFolder().resolve("../../main/resources");
        try (var manager = new MultiPackResourceManager(PackType.SERVER_DATA, List.of(new PathPackResources(
                new PackLocationInfo("ngr", Component.literal("ngr"), PackSource.DEFAULT, Optional.empty()),
                nonGeneratedResources)))) {
            var possibleTargets = manager.listResources("recipe", path -> path.getPath().endsWith(".json"));
            for (var entry : possibleTargets.entrySet()) {
                var pathId = entry.getKey();
                if (shouldConvertToAssembler(pathId)) {
                    try (var stream = entry.getValue().open()) {
                        convertToAssembler(consumer, pathId, IOUtils.toByteArray(stream));
                    } catch (Exception exception) {
                        throw new RuntimeException("Failed to convert asbl recipe %s. Error: %s".formatted(pathId, exception), exception);
                    }
                }
            }
        }
    }

    public static boolean shouldConvertToAssembler(ResourceLocation pathId) {
        if (pathId.getNamespace().equals("milite")) {
            String path = pathId.toString();
            String postfix = path.substring(path.length() - 10, path.length() - 5);
            if (postfix.equals("_asbl")) {
                return true;
            } else if (postfix.contains("_") && postfix.contains("a") && postfix.contains("s") && postfix.contains("b") && postfix.contains("l")) {
                throw new RuntimeException("Detected potential typo in _asbl.json. Crashing just to be safe. Recipe path: " + path);
            }
        }
        return false;
    }

    public static void convertToAssembler(RecipeOutput consumer, ResourceLocation recipeId, byte[] recipe) {
        String recipeString = new String(recipe, StandardCharsets.UTF_8);
        var recipeJson = JsonParser.parseString(recipeString);
        var parsedRecipe = ShapedRecipe.Serializer.CODEC.codec().parse(JsonOps.INSTANCE, recipeJson);

        var shapedRecipe = parsedRecipe.getOrThrow(m -> new RuntimeException("Failed to parse shaped recipe " + recipeId + ": " + m));

        String outputSuffix = recipeId.getPath().substring("recipe/".length(), recipeId.getPath().length() - "_asbl.json".length());
        new MachineRecipeBuilder(MIRecipeJson.assemblerFromShaped(shapedRecipe))
                .level(MachineLevel.ELECTRIC)
                .offerTo(consumer, "assembler_generated/" + outputSuffix);
    }
}
