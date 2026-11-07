package aztech.milite.datagen.recipe;

import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.recipe.json.ShapedRecipeJson;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;

public class FixedVanillaRecipesProvider extends FixedRecipesProvider {
    public FixedVanillaRecipesProvider(PackOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput(ItemTags.PLANKS, 6, 1.0f)
                .addItemInput(ItemTags.WOODEN_SLABS, 2, 1.0f)
                .addItemOutput(Blocks.BARREL, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/assembler/barrel");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput(ItemTags.PLANKS, 8, 1.0f)
                .addItemInput(Blocks.CHEST, 1, 0.0f)
                .addItemOutput(Blocks.CHEST, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/assembler/chest");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput("#c:plates/steel", 5, 1.0f)
                .addItemInput("modern_tech:motor", 1, 1.0f)
                .addItemOutput(Blocks.HOPPER, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/assembler/hopper");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput("#c:rods/gold", 6, 1.0f)
                .addItemInput(Items.STICK, 1, 1.0f)
                .addItemInput(Items.REDSTONE, 1, 1.0f)
                .addFluidInput(fluidTag("c:creosote"), 100, 1.0f)
                .addItemOutput(Blocks.POWERED_RAIL, 8, 1.0f)
                .offerTo(consumer, "vanilla_recipes/assembler/powered_rail");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput("#c:rods/steel", 6, 1.0f)
                .addItemInput(Items.STICK, 1, 1.0f)
                .addFluidInput(fluidTag("c:creosote"), 100, 1.0f)
                .addItemOutput(Blocks.RAIL, 16, 1.0f)
                .offerTo(consumer, "vanilla_recipes/assembler/rail");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput(Items.PRISMARINE_CRYSTALS, 5, 1.0f)
                .addItemInput(Items.PRISMARINE_SHARD, 4, 1.0f)
                .addItemOutput(Blocks.SEA_LANTERN, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/assembler/sea_lantern");

        blasting(consumer, "vanilla_recipes/bauxite_to_aluminum_nugget_blasting", "#c:dusts/bauxite",
                "modern_tech:aluminum_nugget", 1, 0.08f, 100);

        smelting(consumer, "vanilla_recipes/bauxite_to_aluminum_nugget_smelting", "#c:dusts/bauxite",
                "modern_tech:aluminum_nugget", 1, 0.08f, 200);

        shaped(consumer, "vanilla_recipes/blast_furnace_asbl",
                new ShapedRecipeJson("minecraft:blast_furnace", 2, "ppp", "pfp", "sss")
                        .addInput('f', "minecraft:furnace")
                        .addInput('p', "#c:plates/steel")
                        .addInput('s', "minecraft:smooth_stone"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 32, 600)
                .level("aluminum_alloy")
                .addFluidInput(Fluids.LAVA, 1000, 1.0f)
                .addItemOutput("modern_tech:sulfur_dust", 17, 1.0f)
                .addItemOutput("modern_tech:copper_nugget", 11, 1.0f)
                .addItemOutput("modern_tech:aluminum_nugget", 3, 0.5f)
                .addItemOutput("modern_tech:chromium_nugget", 1, 0.25f)
                .offerTo(consumer, "vanilla_recipes/centrifuge/lava");

        shaped(consumer, "vanilla_recipes/chainmail_boots",
                new ShapedRecipeJson("minecraft:chainmail_boots", 1, "x x", "x x")
                        .addInput('x', "#c:ingots/iron"));

        shaped(consumer, "vanilla_recipes/chainmail_chestplate",
                new ShapedRecipeJson("minecraft:chainmail_chestplate", 1, "x x", "xxx", "xxx")
                        .addInput('x', "#c:ingots/iron"));

        shaped(consumer, "vanilla_recipes/chainmail_helmet",
                new ShapedRecipeJson("minecraft:chainmail_helmet", 1, "xxx", "x x")
                        .addInput('x', "#c:ingots/iron"));

        shaped(consumer, "vanilla_recipes/chainmail_leggings",
                new ShapedRecipeJson("minecraft:chainmail_leggings", 1, "xxx", "x x", "x x")
                        .addInput('x', "#c:ingots/iron"));

        shaped(consumer, "vanilla_recipes/chains",
                new ShapedRecipeJson("minecraft:chain", 6, " R ", " R ", " R ")
                        .addInput('R', "#c:ingots/steel"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 4, 400)
                .level("aluminum_alloy")
                .addItemInput("#c:dusts/quartz", 4, 1.0f)
                .addItemInput("#c:tiny_dusts/iron", 1, 1.0f)
                .addFluidInput(Fluids.WATER, 250, 1.0f)
                .addFluidInput("modern_tech:sodium_hydroxide", 500, 1.0f)
                .addItemOutput(Items.AMETHYST_SHARD, 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/chemical_reactor/amethyst");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 8, 100)
                .level("aluminum_alloy")
                .addItemInput(Items.ROTTEN_FLESH, 5, 1.0f)
                .addFluidInput("modern_tech:oxygen", 100, 1.0f)
                .addItemOutput(Items.LEATHER, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/chemical_reactor/leather");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 4, 400)
                .level("aluminum_alloy")
                .addItemInput("#c:dusts/quartz", 4, 1.0f)
                .addItemInput("#c:tiny_dusts/beryllium", 3, 1.0f)
                .addItemInput("#c:tiny_dusts/aluminum", 2, 1.0f)
                .addFluidInput("modern_tech:hydrogen", 4000, 1.0f)
                .addFluidInput("modern_tech:sodium_hydroxide", 4000, 1.0f)
                .addFluidInput("modern_tech:chlorine", 250, 1.0f)
                .addItemOutput(Items.PRISMARINE_CRYSTALS, 16, 1.0f)
                .offerTo(consumer, "vanilla_recipes/chemical_reactor/prismarine_crystals");

        shaped(consumer, "vanilla_recipes/clay_uncrafting",
                new ShapedRecipeJson("minecraft:clay_ball", 4, "C")
                        .addInput('C', "minecraft:clay"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.COMPRESSOR, 4, 6000)
                .level("bronze")
                .addItemInput(Blocks.COAL_BLOCK, 16, 1.0f)
                .addItemOutput(Items.DIAMOND, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/compressor/diamond_from_coal");

        new MachineRecipeBuilder(MIMachineRecipeTypes.COMPRESSOR, 2, 100)
                .level("bronze")
                .addItemInput("#c:glass_blocks", 1, 1.0f)
                .addItemOutput(Items.GLASS_BOTTLE, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/compressor/glass_bottle");

        new MachineRecipeBuilder(MIMachineRecipeTypes.COMPRESSOR, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.MUD, 1, 1.0f)
                .addItemOutput(Blocks.PACKED_MUD, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/compressor/packed_mud");

        new MachineRecipeBuilder(MIMachineRecipeTypes.COMPRESSOR, 2, 100)
                .level("bronze")
                .addItemInput(Items.SUGAR_CANE, 1, 1.0f)
                .addItemOutput(Items.PAPER, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/compressor/paper_with_sugarcane");

        shaped(consumer, "vanilla_recipes/easy_chest",
                new ShapedRecipeJson("minecraft:chest", 4, "lll", "l l", "lll")
                        .addInput('l', "#minecraft:logs"));

        forgeHammer(consumer, "vanilla_recipes/forge_hammer/bone_meal_from_block", "minecraft:bone_block", 1,
                "minecraft:bone_meal", 9);

        forgeHammer(consumer, "vanilla_recipes/forge_hammer/bone_meal_from_bone", "minecraft:bone", 1,
                "minecraft:bone_meal", 6);

        forgeHammer(consumer, "vanilla_recipes/forge_hammer/gravel", "minecraft:cobblestone", 1,
                "minecraft:gravel", 1);

        forgeHammer(consumer, "vanilla_recipes/forge_hammer/sand", "minecraft:gravel", 1,
                "minecraft:sand", 1);

        new MachineRecipeBuilder(MIMachineRecipeTypes.IMPLOSION_COMPRESSOR, 1, 10)
                .level("bronze")
                .addItemInput("#c:storage_blocks/gold", 64, 1.0f)
                .addItemInput(Items.APPLE, 1, 1.0f)
                .addItemInput("modern_tech:industrial_tnt", 1, 1.0f)
                .addItemOutput(Items.ENCHANTED_GOLDEN_APPLE, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/implosion_compressor/enchanted_golden_apple");

        new MachineRecipeBuilder(MIMachineRecipeTypes.IMPLOSION_COMPRESSOR, 64, 10)
                .level("bronze")
                .addItemInput("modern_tech:singularity", 1, 1.0f)
                .addItemInput(Items.LAVA_BUCKET, 1, 1.0f)
                .addItemInput("modern_tech:nuke", 64, 1.0f)
                .addItemInput("modern_tech:nuke", 64, 1.0f)
                .addItemOutput(Items.NETHER_STAR, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/implosion_compressor/nether_star");

        shaped(consumer, "vanilla_recipes/lignite_torch",
                new ShapedRecipeJson("minecraft:torch", 4, "X", "#")
                        .addInput('#', "minecraft:stick")
                        .addInput('X', "modern_tech:lignite_coal"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput(Items.BLAZE_ROD, 1, 1.0f)
                .addItemOutput(Items.BLAZE_POWDER, 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/blaze_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("#c:dusts/lapis", 1, 1.0f)
                .addItemOutput(Items.BLUE_DYE, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/blue_dye1");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.BONE_BLOCK, 1, 1.0f)
                .addItemOutput(Items.BONE_MEAL, 9, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/bone_meal_from_block");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput(Items.BONE, 1, 1.0f)
                .addItemOutput(Items.BONE_MEAL, 6, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/bone_meal_from_bone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.CLAY, 1, 1.0f)
                .addItemOutput(Items.CLAY_BALL, 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/clay_ball");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.DEEPSLATE, 1, 1.0f)
                .addItemOutput(Blocks.COBBLED_DEEPSLATE, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/cobbled_deepslate");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.GILDED_BLACKSTONE, 1, 1.0f)
                .addItemOutput(Items.GOLD_NUGGET, 8, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/gilded_blackstone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.GLOWSTONE, 1, 1.0f)
                .addItemOutput(Items.GLOWSTONE_DUST, 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/glowstone_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.COBBLESTONE, 1, 1.0f)
                .addItemOutput(Blocks.GRAVEL, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/gravel");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 800)
                .level("bronze")
                .addItemInput(Blocks.ANCIENT_DEBRIS, 1, 1.0f)
                .addItemOutput(Items.NETHERITE_SCRAP, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/netherite_scrap");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.RED_SANDSTONE, 1, 1.0f)
                .addItemOutput(Blocks.RED_SAND, 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/red_sandstone_to_sand");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.GRAVEL, 1, 1.0f)
                .addItemOutput(Blocks.SAND, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/sand");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.SANDSTONE, 1, 1.0f)
                .addItemOutput(Blocks.SAND, 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/sandstone_to_sand");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput(ItemTags.WOOL, 1, 1.0f)
                .addItemOutput(Items.STRING, 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/strings");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput(Items.SUGAR_CANE, 1, 1.0f)
                .addItemOutput(Items.SUGAR, 3, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/sugar");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.ANDESITE, 1, 1.0f)
                .addFluidInput(Fluids.WATER, 1, 0.0f)
                .addFluidInput(Fluids.LAVA, 1, 0.0f)
                .addItemOutput(Blocks.ANDESITE, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/andesite");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.CALCITE, 1, 1.0f)
                .addFluidInput(Fluids.WATER, 1, 0.0f)
                .addFluidInput(Fluids.LAVA, 1, 0.0f)
                .addItemOutput(Blocks.CALCITE, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/calcite");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.SAND, 1, 1.0f)
                .addFluidInput(Fluids.WATER, 1000, 1.0f)
                .addItemOutput(Blocks.CLAY, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/clay");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.DIRT, 1, 1.0f)
                .addItemInput(Blocks.GRAVEL, 1, 1.0f)
                .addItemOutput(Blocks.COARSE_DIRT, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/coarse_dirt");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.COBBLED_DEEPSLATE, 1, 1.0f)
                .addFluidInput(Fluids.WATER, 1, 0.0f)
                .addFluidInput(Fluids.LAVA, 1, 0.0f)
                .addItemOutput(Blocks.COBBLED_DEEPSLATE, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/cobbled_deepslate");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.COBBLESTONE, 1, 1.0f)
                .addFluidInput(Fluids.WATER, 1, 0.0f)
                .addFluidInput(Fluids.LAVA, 1, 0.0f)
                .addItemOutput(Blocks.COBBLESTONE, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/cobblestone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.PRISMARINE, 8, 1.0f)
                .addItemInput(Items.BLACK_DYE, 1, 1.0f)
                .addItemOutput(Blocks.DARK_PRISMARINE, 8, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/dark_prismarine");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.DIORITE, 1, 1.0f)
                .addFluidInput(Fluids.WATER, 1, 0.0f)
                .addFluidInput(Fluids.LAVA, 1, 0.0f)
                .addItemOutput(Blocks.DIORITE, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/diorite");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 600)
                .level("bronze")
                .addItemInput(Blocks.COARSE_DIRT, 1, 1.0f)
                .addFluidInput(Fluids.WATER, 6000, 1.0f)
                .addItemOutput(Blocks.DIRT, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/dirt");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.DRIPSTONE_BLOCK, 1, 1.0f)
                .addFluidInput(Fluids.WATER, 1, 0.0f)
                .addFluidInput(Fluids.LAVA, 1, 0.0f)
                .addItemOutput(Blocks.DRIPSTONE_BLOCK, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/dripstone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 8, 200)
                .level("bronze")
                .addItemInput(Blocks.END_STONE, 1, 1.0f)
                .addFluidInput("modern_tech:heavy_water", 1, 1.0f)
                .addFluidInput(Fluids.LAVA, 1, 0.0f)
                .addItemOutput(Blocks.END_STONE, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/endstone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput(Items.SPIDER_EYE, 1, 1.0f)
                .addItemInput(Blocks.BROWN_MUSHROOM, 1, 1.0f)
                .addItemInput(Items.SUGAR, 1, 1.0f)
                .addItemOutput(Items.FERMENTED_SPIDER_EYE, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/fermented_spider_eye");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.GRANITE, 1, 1.0f)
                .addFluidInput(Fluids.WATER, 1, 0.0f)
                .addFluidInput(Fluids.LAVA, 1, 0.0f)
                .addItemOutput(Blocks.GRANITE, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/granite");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 32, 1250)
                .level("bronze")
                .addItemInput(Blocks.STONE, 1, 1.0f)
                .addFluidInput(Fluids.LAVA, 1000, 0.0f)
                .addFluidOutput(Fluids.LAVA, 50, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/lava");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.DIRT, 1, 1.0f)
                .addFluidInput(Fluids.WATER, 1000, 1.0f)
                .addItemOutput(Blocks.MUD, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/mud");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 16, 200)
                .level("bronze")
                .addItemInput(Items.NETHERITE_SCRAP, 4, 1.0f)
                .addItemInput(Items.GOLD_INGOT, 4, 1.0f)
                .addItemOutput(Items.NETHERITE_INGOT, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/netherite");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("modern_tech:wood_pulp", 1, 1.0f)
                .addFluidInput(Fluids.WATER, 100, 1.0f)
                .addItemOutput(Items.PAPER, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/paper");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput(Items.REDSTONE, 1, 1.0f)
                .addItemInput(Blocks.SAND, 1, 1.0f)
                .addItemOutput(Blocks.RED_SAND, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/red_sand");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput(ItemTags.SAND, 4, 1.0f)
                .addItemInput(Items.GUNPOWDER, 5, 1.0f)
                .addItemOutput(Blocks.TNT, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/tnt");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.RAIL, 4, 1.0f)
                .addItemInput(Blocks.REDSTONE_TORCH, 1, 1.0f)
                .addItemOutput(Blocks.ACTIVATOR_RAIL, 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/activator_rail");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.AMETHYST_SHARD, 4, 1.0f)
                .addItemOutput(Blocks.AMETHYST_BLOCK, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/amethyst_block");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.PACKED_ICE, 9, 1.0f)
                .addItemOutput(Blocks.BLUE_ICE, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/blue_ice");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.BONE_MEAL, 9, 1.0f)
                .addItemOutput(Blocks.BONE_BLOCK, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/bone_block");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.BRICK, 4, 1.0f)
                .addItemOutput(Blocks.BRICKS, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/bricks");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("modern_tech:steel_plate", 3, 1.0f)
                .addItemOutput(Items.BUCKET, 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/bucket");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.STRING, 1, 1.0f)
                .addItemInput("modern_tech:wax", 1, 1.0f)
                .addItemOutput(Blocks.CANDLE, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/candle");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("#c:ingots/steel", 3, 1.0f)
                .addItemOutput(Blocks.CHAIN, 6, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/chains");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.CHEST, 1, 1.0f)
                .addItemInput(Items.MINECART, 1, 1.0f)
                .addItemOutput(Items.CHEST_MINECART, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/chest_minecart");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.RAIL, 4, 1.0f)
                .addItemInput(Blocks.STONE_PRESSURE_PLATE, 1, 1.0f)
                .addItemInput(Items.REDSTONE, 1, 1.0f)
                .addItemOutput(Blocks.DETECTOR_RAIL, 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/detector_rail");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.DRIED_KELP, 9, 1.0f)
                .addItemOutput(Blocks.DRIED_KELP_BLOCK, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/dried_kelp");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.POINTED_DRIPSTONE, 4, 1.0f)
                .addItemOutput(Blocks.DRIPSTONE_BLOCK, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/dripstone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.FURNACE, 1, 1.0f)
                .addItemInput(Items.MINECART, 1, 1.0f)
                .addItemOutput(Items.FURNACE_MINECART, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/furnace_minecart");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(ItemTags.STONE_CRAFTING_MATERIALS, 8, 1.0f)
                .addItemOutput(Blocks.FURNACE, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/furnace");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.GOLD_NUGGET, 4, 1.0f)
                .addItemInput(Items.MELON_SLICE, 1, 1.0f)
                .addItemOutput(Items.GLISTERING_MELON_SLICE, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/glistering_melon_slice");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.GLOWSTONE_DUST, 4, 1.0f)
                .addItemOutput(Blocks.GLOWSTONE, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/glowstone_block");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.GOLD_NUGGET, 4, 1.0f)
                .addItemInput(Items.CARROT, 1, 1.0f)
                .addItemOutput(Items.GOLDEN_CARROT, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/golden_carrot");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.WHEAT, 9, 1.0f)
                .addItemOutput(Blocks.HAY_BLOCK, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/hayblock");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.HONEYCOMB, 4, 1.0f)
                .addItemOutput(Blocks.HONEYCOMB_BLOCK, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/honeycomb");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.HOPPER, 1, 1.0f)
                .addItemInput(Items.MINECART, 1, 1.0f)
                .addItemOutput(Items.HOPPER_MINECART, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/hopper_minecart");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.MAGMA_CREAM, 4, 1.0f)
                .addItemOutput(Blocks.MAGMA_BLOCK, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/magma_block");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.BLAZE_POWDER, 1, 1.0f)
                .addItemInput(Items.SLIME_BALL, 1, 1.0f)
                .addItemOutput(Items.MAGMA_CREAM, 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/magma_cream");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.MELON_SLICE, 9, 1.0f)
                .addItemOutput(Blocks.MELON, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/melon");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("modern_tech:steel_large_plate", 1, 1.0f)
                .addItemInput(Blocks.RAIL, 1, 1.0f)
                .addItemOutput(Items.MINECART, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/minecart");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.PACKED_MUD, 4, 1.0f)
                .addItemOutput(Blocks.MUD_BRICKS, 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/mud_bricks");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.NETHER_BRICK, 4, 1.0f)
                .addItemOutput(Blocks.NETHER_BRICKS, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/nether_bricks");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.NETHER_WART, 9, 1.0f)
                .addItemOutput(Blocks.NETHER_WART_BLOCK, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/nether_warts");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 200)
                .level("bronze")
                .addItemInput(Items.NETHERITE_INGOT, 9, 1.0f)
                .addItemOutput(Blocks.NETHERITE_BLOCK, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/netherite");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.ICE, 9, 1.0f)
                .addItemOutput(Blocks.PACKED_ICE, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/packed_ice");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.PRISMARINE_SHARD, 9, 1.0f)
                .addItemInput("modern_tech:packer_block_template", 1, 0.0f)
                .addItemOutput(Blocks.PRISMARINE_BRICKS, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/prismarine_bricks");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.PRISMARINE_SHARD, 4, 1.0f)
                .addItemOutput(Blocks.PRISMARINE, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/prismarine");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.POPPED_CHORUS_FRUIT, 4, 1.0f)
                .addItemOutput(Blocks.PURPUR_BLOCK, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/purpur");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.QUARTZ, 4, 1.0f)
                .addItemOutput(Blocks.QUARTZ_BLOCK, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/quartz_block");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.RED_SAND, 4, 1.0f)
                .addItemOutput(Blocks.RED_SANDSTONE, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/red_sandstone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.SAND, 4, 1.0f)
                .addItemOutput(Blocks.SANDSTONE, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/sandstone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.SLIME_BALL, 9, 1.0f)
                .addItemOutput(Blocks.SLIME_BLOCK, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/slimeblock");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Items.SNOWBALL, 4, 1.0f)
                .addItemOutput(Blocks.SNOW_BLOCK, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/snowblock");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput(Blocks.TNT, 1, 1.0f)
                .addItemInput(Items.MINECART, 1, 1.0f)
                .addItemOutput(Items.TNT_MINECART, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/tnt_minecart");

        smelting(consumer.withConditions(new NotCondition(new ModLoadedCondition("dehydration"))), "vanilla_recipes/steam_bucket", "minecraft:water_bucket",
                "modern_tech:steam_bucket", 1, 0.0f, 500);

        shaped(consumer, "vanilla_recipes/steel_anvil_asbl",
                new ShapedRecipeJson("minecraft:anvil", 2, "III", " i ", "iii")
                        .addInput('I', "modern_tech:steel_block")
                        .addInput('i', "modern_tech:steel_ingot"));

        shaped(consumer, "vanilla_recipes/steel_forge_hammer_asbl",
                new ShapedRecipeJson("modern_tech:forge_hammer", 2, "___", " i ", "iii")
                        .addInput('_', "#c:plates/steel")
                        .addInput('i', "#c:ingots/steel"));

        shaped(consumer, "vanilla_recipes/steel_hopper_asbl",
                new ShapedRecipeJson("minecraft:hopper", 2, "p p", "pcp", " p ")
                        .addInput('c', "minecraft:chest")
                        .addInput('p', "#c:plates/steel"));

        shaped(consumer, "vanilla_recipes/torch",
                new ShapedRecipeJson("minecraft:torch", 8, "C", "s")
                        .addInput('C', "modern_tech:coke")
                        .addInput('s', "minecraft:stick"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.VACUUM_FREEZER, 16, 200)
                .level("bronze")
                .addFluidInput(Fluids.WATER, 1000, 1.0f)
                .addItemOutput(Blocks.ICE, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/vacuum_freezer/ice");

        new MachineRecipeBuilder(MIMachineRecipeTypes.VACUUM_FREEZER, 2, 2400)
                .level("bronze")
                .addFluidInput(Fluids.LAVA, 1000, 1.0f)
                .addItemOutput(Blocks.OBSIDIAN, 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/vacuum_freezer/obsidian");
    }
}
