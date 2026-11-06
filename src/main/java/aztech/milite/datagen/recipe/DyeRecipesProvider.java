package aztech.milite.datagen.recipe;

import aztech.milite.MIFluids;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;

public class DyeRecipesProvider extends MIRecipesProvider {
    public DyeRecipesProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        for (DyeColor color : DyeColor.values()) {
            String pathPrefix = "dyes/" + color.getName() + "/";
            var dyeTag = "#" + color.getTag().location();
            new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 200).addFluidInput(MIFluids.SYNTHETIC_OIL, 100)
                    .addItemInput(dyeTag, 1, 0).addItemOutput("minecraft:" + color.getName() + "_dye", 1)
                    .offerTo(consumer, pathPrefix + "mixer/synthetic_oil");
            new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 200).addFluidInput(MIFluids.BENZENE, 25)
                    .addItemInput(dyeTag, 1, 0).addItemOutput("minecraft:" + color.getName() + "_dye", 1)
                    .offerTo(consumer, pathPrefix + "mixer/benzene");

            new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100).addItemInput(dyeTag, 1)
                    .addItemInput(ItemTags.WOOL, 8).addItemOutput("minecraft:" + color.getName() + "_wool", 8)
                    .offerTo(consumer, pathPrefix + "mixer/wool");

            new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100).addItemInput(dyeTag, 1)
                    .addItemInput("#c:glass_blocks", 8)
                    .addItemOutput("minecraft:" + color.getName() + "_stained_glass", 8)
                    .offerTo(consumer, pathPrefix + "mixer/glass");

            new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100).addItemInput(dyeTag, 1)
                    .addItemInput("#c:glass_panes", 8).addItemOutput("minecraft:" + color.getName() + "_stained_glass_pane", 8)
                    .offerTo(consumer, pathPrefix + "mixer/glass_pane");

            new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100).addItemInput(dyeTag, 1)
                    .addItemInput("#c:shulker_boxes", 1).addItemOutput("minecraft:" + color.getName() + "_shulker_box", 1)
                    .offerTo(consumer, pathPrefix + "mixer/shulker_box");

            new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100).addItemInput(dyeTag, 1)
                    .addItemInput(ItemTags.BEDS, 1)
                    .addItemOutput("minecraft:" + color.getName() + "_bed", 1)
                    .offerTo(consumer, pathPrefix + "mixer/bed");

            new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100).addItemInput(dyeTag, 1)
                    .addItemInput(ItemTags.CANDLES, 1).addItemOutput("minecraft:" + color.getName() + "_candle", 1)
                    .offerTo(consumer, pathPrefix + "mixer/candle");

            new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100).addItemInput(dyeTag, 1)
                    .addItemInput(ItemTags.WOOL_CARPETS, 8).addItemOutput("minecraft:" + color.getName() + "_carpet", 8)
                    .offerTo(consumer, pathPrefix + "mixer/carpet");

            new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100).addItemInput(dyeTag, 1)
                    .addItemInput(ItemTags.TERRACOTTA, 8).addItemOutput("minecraft:" + color.getName() + "_terracotta", 8)
                    .offerTo(consumer, pathPrefix + "mixer/terracotta");

            new MachineRecipeBuilder(MIMachineRecipeTypes.CUTTING_MACHINE, 2, 100)
                    .addItemInput("minecraft:" + color.getName() + "_stained_glass", 6)
                    .addItemOutput("minecraft:" + color.getName() + "_stained_glass_pane", 16)
                    .offerTo(consumer, pathPrefix + "cutting_machine/glass_pane");

            new MachineRecipeBuilder(MIMachineRecipeTypes.CUTTING_MACHINE, 2, 100)
                    .addItemInput("minecraft:" + color.getName() + "_wool", 1)
                    .addItemOutput("minecraft:" + color.getName() + "_carpet", 4)
                    .offerTo(consumer, pathPrefix + "cutting_machine/carpet");
        }
    }
}
