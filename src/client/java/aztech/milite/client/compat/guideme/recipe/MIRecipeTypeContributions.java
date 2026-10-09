package aztech.milite.client.compat.guideme.recipe;

import aztech.milite.blocks.forgehammer.ForgeHammerRecipe;
import aztech.milite.blocks.processingtable.ProcessingTableRecipe;
import aztech.milite.client.compat.guideme.recipe.header.LytMachineRecipeHeader;
import aztech.milite.client.compat.guideme.recipe.header.LytMachineRecipeHeaderConditionItemImage;
import aztech.milite.client.compat.guideme.recipe.header.LytMachineRecipeHeaderRfCostImage;
import aztech.milite.client.compat.guideme.recipe.item.LytMIItemSlot;
import aztech.milite.compat.rei.machines.MachineCategoryParams;
import aztech.milite.compat.rei.machines.ReiMachineRecipes;
import aztech.milite.items.ForgeTool;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipe;
import aztech.milite.MIBlock;
import aztech.milite.MIRegistries;
import aztech.milite.MIText;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import guideme.compiler.tags.RecipeTypeMappingSupplier;
import guideme.document.block.*;
import guideme.document.block.recipes.LytStandardRecipeBox;
import guideme.document.DefaultStyles;
import guideme.document.LytSize;
import guideme.render.GuiAssets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class MIRecipeTypeContributions implements RecipeTypeMappingSupplier {
    @Override
    public void collect(RecipeTypeMappings mappings) {
        mappings.add(MIRegistries.FORGE_HAMMER_RECIPE_TYPE.get(), MIRecipeTypeContributions::forgeHammer);
        mappings.add(MIRegistries.PROCESSING_TABLE_RECIPE_TYPE.get(), MIRecipeTypeContributions::processingTable);

        for (var recipeType : MIMachineRecipeTypes.getRecipeTypes()) {
            mappings.add(recipeType, MIRecipeTypeContributions::machine);
        }
    }

    private static LytStandardRecipeBox<ProcessingTableRecipe> processingTable(RecipeHolder<ProcessingTableRecipe> holder) {
        return LytStandardRecipeBox.builder()
                .icon(MIBlock.PROCESSING_TABLE)
                .title(MIBlock.PROCESSING_TABLE.asItem().getDescription().getString())
                .customBody(processingTableGrid(holder.value()))
                .build(holder);
    }

    private static LytBlock processingTableGrid(ProcessingTableRecipe recipe) {
        var grid = new LytHBox();
        grid.setGap(2);
        grid.setAlignItems(AlignItems.CENTER);
        grid.setWrap(false);

        grid.append(new LytMIItemSlot(recipe.key(), 1, 1, true));
        grid.append(new LytMIItemSlot(recipe.first(), 1, 1, true));
        grid.append(new LytMIItemSlot(recipe.second(), 1, 1, true));

        grid.append(new LytGuiSprite(GuiAssets.ARROW, new LytSize(24, 17)));

        grid.append(new LytMIItemSlot(ItemVariant.of(recipe.result()), recipe.result().getCount(), 1, false));

        return grid;
    }

    private static LytStandardRecipeBox<ForgeHammerRecipe> forgeHammer(RecipeHolder<ForgeHammerRecipe> holder) {
        var recipe = holder.value();
        var requiresTool = recipe.hammerDamage() > 0;

        var durabilityText = new LytParagraph();
        durabilityText.setStyle(DefaultStyles.CRAFTING_RECIPE_TYPE);
        durabilityText.appendText(requiresTool ? MIText.DurabilityCost.text("" + recipe.hammerDamage()).getString() : MIText.NoToolRequired.text().getString());

        return LytStandardRecipeBox.builder()
                .icon(MIBlock.FORGE_HAMMER)
                .title(MIBlock.FORGE_HAMMER.asItem().getDescription().getString())
                .addTop(durabilityText)
                .customBody(forgeHammerGrid(recipe))
                .build(holder);
    }

    private static LytBlock forgeHammerGrid(ForgeHammerRecipe recipe) {
        var requiresTool = recipe.hammerDamage() > 0;

        var grid = new LytHBox();
        grid.setGap(2);
        grid.setAlignItems(AlignItems.CENTER);
        grid.setWrap(false);

        grid.append(requiresTool ? new LytMIItemSlot(Ingredient.of(ForgeTool.TAG), 1, 1, true) : new LytMIItemSlot(true));
        grid.append(new LytMIItemSlot(recipe.ingredient(), recipe.count(), 1, true));

        grid.append(new LytGuiSprite(GuiAssets.ARROW, new LytSize(24, 17)));

        grid.append(new LytMIItemSlot(ItemVariant.of(recipe.result()), recipe.result().getCount(), 1, false));

        return grid;
    }

    private static LytStandardRecipeBox<MachineRecipe> machine(RecipeHolder<MachineRecipe> holder) {
        var recipe = holder.value();
        for (var params : ReiMachineRecipes.categories.values()) {
            if (params.recipeType == recipe.getType() && params.recipePredicate.test(recipe)) {
                var paramsDimensions = params.calculateDimensions();
                return LytStandardRecipeBox.builder()
                        .icon(BuiltInRegistries.ITEM.get(params.workstations.getFirst()).getDefaultInstance())
                        .title(Component.translatable("rei_categories.%s.%s".formatted(params.category.getNamespace(), params.category.getPath())).getString())
                        .addTop(machineHeader(params, paramsDimensions, recipe))
                        .customBody(new LytMachineRecipeSlots(params, paramsDimensions, recipe))
                        .build(holder);
            }
        }
        throw new IllegalStateException("Could not find fitting machine category params for recipe " + holder.id());
    }

    private static LytBlock machineHeader(MachineCategoryParams params, MachineCategoryParams.Dimensions paramsDimensions, MachineRecipe recipe) {
        var header = new LytMachineRecipeHeader(params, recipe, paramsDimensions.width());
        header.setGap(2);
        header.setAlignItems(AlignItems.CENTER);

        header.append(new LytMachineRecipeHeaderRfCostImage(params.steamMode));

        var rfCost = new LytParagraph();
        rfCost.setStyle(DefaultStyles.CRAFTING_RECIPE_TYPE);
        rfCost.appendText(recipe.level.getDisplayName().getString());
        header.append(rfCost);

        boolean includeRecipeConditionDisplay = params.includeRecipeConditionDisplay(recipe);
        if (includeRecipeConditionDisplay) {
            var condition = new LytMachineRecipeHeaderConditionItemImage(params.getRecipeConditionDisplayItems(recipe));
            condition.setMarginLeft(5);
            condition.setMarginRight(5);
            header.append(condition);
        }

        var duration = new LytParagraph();
        duration.setStyle(DefaultStyles.CRAFTING_RECIPE_TYPE);
        duration.appendText(MIText.BaseDurationSeconds.text(recipe.duration / 20.0).getString());
        if (!includeRecipeConditionDisplay) {
            duration.setMarginLeft(10);
        }
        header.append(duration);

        return header;
    }
}
