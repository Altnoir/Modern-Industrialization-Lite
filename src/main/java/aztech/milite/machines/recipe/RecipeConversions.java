package aztech.milite.machines.recipe;

import aztech.milite.config.MIServerConfig;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.MI;
import aztech.milite.MIFluids;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidType;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class RecipeConversions {
    public static RecipeHolder<MachineRecipe> ofSmelting(RecipeHolder<SmeltingRecipe> holder, MachineRecipeType type, RegistryAccess registryAccess) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(holder.id().getNamespace(), "/" + holder.id().getPath() + "_exported_mi_furnace");
        var smeltingRecipe = holder.value();
        Ingredient ingredient = smeltingRecipe.getIngredients().get(0);
        MachineRecipe recipe = new MachineRecipe(type);
        recipe.energy = 2;
        recipe.duration = smeltingRecipe.getCookingTime();
        recipe.itemInputs = Collections.singletonList(new MachineRecipe.ItemInput(ingredient, 1, 1));
        recipe.fluidInputs = Collections.emptyList();
        var result = smeltingRecipe.getResultItem(registryAccess);
        recipe.itemOutputs = Collections
                .singletonList(new MachineRecipe.ItemOutput(ItemVariant.of(result), result.getCount(), 1));
        recipe.fluidOutputs = Collections.emptyList();
        return new RecipeHolder<>(id, recipe);
    }

    @Nullable
    public static RecipeHolder<MachineRecipe> ofStonecutting(RecipeHolder<StonecutterRecipe> holder, MachineRecipeType type,
            RegistryAccess registryAccess) {
        if (!MIServerConfig.INSTANCE.stonecutterToCuttingMachine.getAsBoolean()) {
            return null;
        }

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(holder.id().getNamespace(),
                "/" + holder.id().getPath() + "_exported_mi_cutting_machine");
        var stonecuttingRecipe = holder.value();
        MachineRecipe recipe = new MachineRecipe(type);
        recipe.energy = 2;
        recipe.duration = 200;
        recipe.itemInputs = Collections.singletonList(new MachineRecipe.ItemInput(stonecuttingRecipe.getIngredients().get(0), 1, 1));
        recipe.itemOutputs = Collections
                .singletonList(
                        new MachineRecipe.ItemOutput(ItemVariant.of(stonecuttingRecipe.getResultItem(null)),
                                stonecuttingRecipe.getResultItem(registryAccess).getCount(), 1));
        recipe.fluidOutputs = Collections.emptyList();
        return new RecipeHolder<>(id, recipe);
    }

    @Nullable
    public static RecipeHolder<MachineRecipe> ofCompostable(ItemLike compostable) {
        if (!MIServerConfig.INSTANCE.compostableToPlantOil.getAsBoolean()) {
            return null;
        }

        if (compostable == null || compostable.asItem() == null) {
            return null; // apparently bad mods do this
        }

        float probability = ComposterBlock.COMPOSTABLES.getOrDefault(compostable.asItem(), 0.0F);
        if (probability > 0.0F) {
            ResourceLocation id = MI.id("/" + BuiltInRegistries.ITEM.getKey(compostable.asItem()).getPath() + "_to_plant_oil");
            MachineRecipe plantOil = new MachineRecipe(MIMachineRecipeTypes.CENTRIFUGE);
            plantOil.energy = 8;
            plantOil.duration = 200;

            plantOil.itemInputs = List.of(new MachineRecipe.ItemInput(
                    Ingredient.of(compostable),
                    1,
                    1.0f));

            plantOil.fluidInputs = Collections.emptyList();

            plantOil.fluidOutputs = List.of(new MachineRecipe.FluidOutput(
                    MIFluids.PLANT_OIL.asFluid(),
                    (long) (probability * FluidType.BUCKET_VOLUME),
                    1.0f));
            plantOil.itemOutputs = Collections.emptyList();
            return new RecipeHolder<>(id, plantOil);
        } else {
            return null;
        }
    }
}
