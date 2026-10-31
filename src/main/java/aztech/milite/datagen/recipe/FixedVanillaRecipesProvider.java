package aztech.milite.datagen.recipe;

import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.recipe.json.ShapedRecipeJson;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
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
                .addItemInput("#minecraft:planks", 6, 1.0f)
                .addItemInput("#minecraft:wooden_slabs", 2, 1.0f)
                .addItemOutput("minecraft:barrel", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/assembler/barrel");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput("#minecraft:planks", 8, 1.0f)
                .addItemInput("minecraft:chest", 1, 0.0f)
                .addItemOutput("minecraft:chest", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/assembler/chest");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput("#c:plates/steel", 5, 1.0f)
                .addItemInput("milite:motor", 1, 1.0f)
                .addItemOutput("minecraft:hopper", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/assembler/hopper");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput("#c:rods/gold", 6, 1.0f)
                .addItemInput("minecraft:stick", 1, 1.0f)
                .addItemInput("minecraft:redstone", 1, 1.0f)
                .addFluidInput(fluidTag("c:creosote"), 100, 1.0f)
                .addItemOutput("minecraft:powered_rail", 8, 1.0f)
                .offerTo(consumer, "vanilla_recipes/assembler/powered_rail");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput("#c:rods/steel", 6, 1.0f)
                .addItemInput("minecraft:stick", 1, 1.0f)
                .addFluidInput(fluidTag("c:creosote"), 100, 1.0f)
                .addItemOutput("minecraft:rail", 16, 1.0f)
                .offerTo(consumer, "vanilla_recipes/assembler/rail");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput("minecraft:prismarine_crystals", 5, 1.0f)
                .addItemInput("minecraft:prismarine_shard", 4, 1.0f)
                .addItemOutput("minecraft:sea_lantern", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/assembler/sea_lantern");

        blasting(consumer, "vanilla_recipes/bauxite_to_aluminum_nugget_blasting", "#c:dusts/bauxite",
                "milite:aluminum_nugget", 1, 0.08f, 100);

        smelting(consumer, "vanilla_recipes/bauxite_to_aluminum_nugget_smelting", "#c:dusts/bauxite",
                "milite:aluminum_nugget", 1, 0.08f, 200);

        shaped(consumer, "vanilla_recipes/blast_furnace_asbl",
                new ShapedRecipeJson("minecraft:blast_furnace", 2, "ppp", "pfp", "sss")
                        .addInput('f', "minecraft:furnace")
                        .addInput('p', "#c:plates/steel")
                        .addInput('s', "minecraft:smooth_stone"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 32, 600)
                .level("bronze")
                .addFluidInput("minecraft:lava", 1000, 1.0f)
                .addItemOutput("milite:sulfur_dust", 17, 1.0f)
                .addItemOutput("milite:copper_nugget", 11, 1.0f)
                .addItemOutput("milite:aluminum_nugget", 3, 0.5f)
                .addItemOutput("milite:chromium_nugget", 1, 0.25f)
                .offerTo(consumer, "vanilla_recipes/centrifuge/lava");

        shaped(consumer, "vanilla_recipes/chainmail_boots",
                new ShapedRecipeJson("minecraft:chainmail_boots", 1, "x x", "x x")
                        .addInput('x', "milite:iron_ring"));

        shaped(consumer, "vanilla_recipes/chainmail_chestplate",
                new ShapedRecipeJson("minecraft:chainmail_chestplate", 1, "x x", "xxx", "xxx")
                        .addInput('x', "milite:iron_ring"));

        shaped(consumer, "vanilla_recipes/chainmail_helmet",
                new ShapedRecipeJson("minecraft:chainmail_helmet", 1, "xxx", "x x")
                        .addInput('x', "milite:iron_ring"));

        shaped(consumer, "vanilla_recipes/chainmail_leggings",
                new ShapedRecipeJson("minecraft:chainmail_leggings", 1, "xxx", "x x", "x x")
                        .addInput('x', "milite:iron_ring"));

        shaped(consumer, "vanilla_recipes/chains",
                new ShapedRecipeJson("minecraft:chain", 6, " R ", " R ", " R ")
                        .addInput('R', "milite:steel_ring"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 4, 400)
                .level("bronze")
                .addItemInput("#c:dusts/quartz", 4, 1.0f)
                .addItemInput("#c:tiny_dusts/iron", 1, 1.0f)
                .addFluidInput("minecraft:water", 250, 1.0f)
                .addFluidInput("milite:sodium_hydroxide", 500, 1.0f)
                .addItemOutput("minecraft:amethyst_shard", 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/chemical_reactor/amethyst");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 8, 100)
                .level("bronze")
                .addItemInput("minecraft:rotten_flesh", 5, 1.0f)
                .addFluidInput("milite:oxygen", 100, 1.0f)
                .addItemOutput("minecraft:leather", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/chemical_reactor/leather");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 4, 400)
                .level("bronze")
                .addItemInput("#c:dusts/quartz", 4, 1.0f)
                .addItemInput("#c:tiny_dusts/beryllium", 3, 1.0f)
                .addItemInput("#c:tiny_dusts/aluminum", 2, 1.0f)
                .addFluidInput("milite:hydrogen", 4000, 1.0f)
                .addFluidInput("milite:sodium_hydroxide", 4000, 1.0f)
                .addFluidInput("milite:chlorine", 250, 1.0f)
                .addItemOutput("minecraft:prismarine_crystals", 16, 1.0f)
                .offerTo(consumer, "vanilla_recipes/chemical_reactor/prismarine_crystals");

        shaped(consumer, "vanilla_recipes/clay_uncrafting",
                new ShapedRecipeJson("minecraft:clay_ball", 4, "C")
                        .addInput('C', "minecraft:clay"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.COMPRESSOR, 4, 6000)
                .level("bronze")
                .addItemInput("minecraft:coal_block", 16, 1.0f)
                .addItemOutput("minecraft:diamond", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/compressor/diamond_from_coal");

        new MachineRecipeBuilder(MIMachineRecipeTypes.COMPRESSOR, 2, 100)
                .level("bronze")
                .addItemInput("#c:glass_blocks", 1, 1.0f)
                .addItemOutput("minecraft:glass_bottle", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/compressor/glass_bottle");

        new MachineRecipeBuilder(MIMachineRecipeTypes.COMPRESSOR, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:mud", 1, 1.0f)
                .addItemOutput("minecraft:packed_mud", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/compressor/packed_mud");

        new MachineRecipeBuilder(MIMachineRecipeTypes.COMPRESSOR, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:sugar_cane", 1, 1.0f)
                .addItemOutput("minecraft:paper", 2, 1.0f)
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
                .addItemInput("minecraft:apple", 1, 1.0f)
                .addItemInput("milite:industrial_tnt", 1, 1.0f)
                .addItemOutput("minecraft:enchanted_golden_apple", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/implosion_compressor/enchanted_golden_apple");

        new MachineRecipeBuilder(MIMachineRecipeTypes.IMPLOSION_COMPRESSOR, 64, 10)
                .level("bronze")
                .addItemInput("milite:singularity", 1, 1.0f)
                .addItemInput("minecraft:lava_bucket", 1, 1.0f)
                .addItemInput("milite:nuke", 64, 1.0f)
                .addItemInput("milite:nuke", 64, 1.0f)
                .addItemOutput("minecraft:nether_star", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/implosion_compressor/nether_star");

        shaped(consumer, "vanilla_recipes/lignite_torch",
                new ShapedRecipeJson("minecraft:torch", 4, "X", "#")
                        .addInput('#', "minecraft:stick")
                        .addInput('X', "milite:lignite_coal"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:blaze_rod", 1, 1.0f)
                .addItemOutput("minecraft:blaze_powder", 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/blaze_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("#c:dusts/lapis", 1, 1.0f)
                .addItemOutput("minecraft:blue_dye", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/blue_dye1");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:bone_block", 1, 1.0f)
                .addItemOutput("minecraft:bone_meal", 9, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/bone_meal_from_block");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:bone", 1, 1.0f)
                .addItemOutput("minecraft:bone_meal", 6, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/bone_meal_from_bone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:clay", 1, 1.0f)
                .addItemOutput("minecraft:clay_ball", 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/clay_ball");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:deepslate", 1, 1.0f)
                .addItemOutput("minecraft:cobbled_deepslate", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/cobbled_deepslate");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:gilded_blackstone", 1, 1.0f)
                .addItemOutput("minecraft:gold_nugget", 8, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/gilded_blackstone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:glowstone", 1, 1.0f)
                .addItemOutput("minecraft:glowstone_dust", 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/glowstone_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:cobblestone", 1, 1.0f)
                .addItemOutput("minecraft:gravel", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/gravel");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 800)
                .level("bronze")
                .addItemInput("minecraft:ancient_debris", 1, 1.0f)
                .addItemOutput("minecraft:netherite_scrap", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/netherite_scrap");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:red_sandstone", 1, 1.0f)
                .addItemOutput("minecraft:red_sand", 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/red_sandstone_to_sand");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:gravel", 1, 1.0f)
                .addItemOutput("minecraft:sand", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/sand");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:sandstone", 1, 1.0f)
                .addItemOutput("minecraft:sand", 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/sandstone_to_sand");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("#minecraft:wool", 1, 1.0f)
                .addItemOutput("minecraft:string", 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/strings");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:sugar_cane", 1, 1.0f)
                .addItemOutput("minecraft:sugar", 3, 1.0f)
                .offerTo(consumer, "vanilla_recipes/macerator/sugar");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:andesite", 1, 1.0f)
                .addFluidInput("minecraft:water", 1, 0.0f)
                .addFluidInput("minecraft:lava", 1, 0.0f)
                .addItemOutput("minecraft:andesite", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/andesite");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:calcite", 1, 1.0f)
                .addFluidInput("minecraft:water", 1, 0.0f)
                .addFluidInput("minecraft:lava", 1, 0.0f)
                .addItemOutput("minecraft:calcite", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/calcite");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:sand", 1, 1.0f)
                .addFluidInput("minecraft:water", 1000, 1.0f)
                .addItemOutput("minecraft:clay", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/clay");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:dirt", 1, 1.0f)
                .addItemInput("minecraft:gravel", 1, 1.0f)
                .addItemOutput("minecraft:coarse_dirt", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/coarse_dirt");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:cobbled_deepslate", 1, 1.0f)
                .addFluidInput("minecraft:water", 1, 0.0f)
                .addFluidInput("minecraft:lava", 1, 0.0f)
                .addItemOutput("minecraft:cobbled_deepslate", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/cobbled_deepslate");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:cobblestone", 1, 1.0f)
                .addFluidInput("minecraft:water", 1, 0.0f)
                .addFluidInput("minecraft:lava", 1, 0.0f)
                .addItemOutput("minecraft:cobblestone", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/cobblestone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:prismarine", 8, 1.0f)
                .addItemInput("minecraft:black_dye", 1, 1.0f)
                .addItemOutput("minecraft:dark_prismarine", 8, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/dark_prismarine");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:diorite", 1, 1.0f)
                .addFluidInput("minecraft:water", 1, 0.0f)
                .addFluidInput("minecraft:lava", 1, 0.0f)
                .addItemOutput("minecraft:diorite", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/diorite");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 600)
                .level("bronze")
                .addItemInput("minecraft:coarse_dirt", 1, 1.0f)
                .addFluidInput("minecraft:water", 6000, 1.0f)
                .addItemOutput("minecraft:dirt", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/dirt");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:dripstone_block", 1, 1.0f)
                .addFluidInput("minecraft:water", 1, 0.0f)
                .addFluidInput("minecraft:lava", 1, 0.0f)
                .addItemOutput("minecraft:dripstone_block", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/dripstone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 8, 200)
                .level("bronze")
                .addItemInput("minecraft:end_stone", 1, 1.0f)
                .addFluidInput("milite:heavy_water", 1, 1.0f)
                .addFluidInput("minecraft:lava", 1, 0.0f)
                .addItemOutput("minecraft:end_stone", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/endstone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:spider_eye", 1, 1.0f)
                .addItemInput("minecraft:brown_mushroom", 1, 1.0f)
                .addItemInput("minecraft:sugar", 1, 1.0f)
                .addItemOutput("minecraft:fermented_spider_eye", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/fermented_spider_eye");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:granite", 1, 1.0f)
                .addFluidInput("minecraft:water", 1, 0.0f)
                .addFluidInput("minecraft:lava", 1, 0.0f)
                .addItemOutput("minecraft:granite", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/granite");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 32, 1250)
                .level("bronze")
                .addItemInput("minecraft:stone", 1, 1.0f)
                .addFluidInput("minecraft:lava", 1000, 0.0f)
                .addFluidOutput("minecraft:lava", 50, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/lava");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:dirt", 1, 1.0f)
                .addFluidInput("minecraft:water", 1000, 1.0f)
                .addItemOutput("minecraft:mud", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/mud");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 16, 200)
                .level("bronze")
                .addItemInput("minecraft:netherite_scrap", 4, 1.0f)
                .addItemInput("minecraft:gold_ingot", 4, 1.0f)
                .addItemOutput("minecraft:netherite_ingot", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/netherite");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("milite:wood_pulp", 1, 1.0f)
                .addFluidInput("minecraft:water", 100, 1.0f)
                .addItemOutput("minecraft:paper", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/paper");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:redstone", 1, 1.0f)
                .addItemInput("minecraft:sand", 1, 1.0f)
                .addItemOutput("minecraft:red_sand", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/red_sand");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("#minecraft:sand", 4, 1.0f)
                .addItemInput("minecraft:gunpowder", 5, 1.0f)
                .addItemOutput("minecraft:tnt", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/mixer/tnt");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:rail", 4, 1.0f)
                .addItemInput("minecraft:redstone_torch", 1, 1.0f)
                .addItemOutput("minecraft:activator_rail", 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/activator_rail");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:amethyst_shard", 4, 1.0f)
                .addItemOutput("minecraft:amethyst_block", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/amethyst_block");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:packed_ice", 9, 1.0f)
                .addItemOutput("minecraft:blue_ice", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/blue_ice");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:bone_meal", 9, 1.0f)
                .addItemOutput("minecraft:bone_block", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/bone_block");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:brick", 4, 1.0f)
                .addItemOutput("minecraft:bricks", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/bricks");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("milite:steel_plate", 3, 1.0f)
                .addItemOutput("minecraft:bucket", 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/bucket");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:string", 1, 1.0f)
                .addItemInput("milite:wax", 1, 1.0f)
                .addItemOutput("minecraft:candle", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/candle");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("milite:steel_ring", 3, 1.0f)
                .addItemOutput("minecraft:chain", 6, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/chains");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:chest", 1, 1.0f)
                .addItemInput("minecraft:minecart", 1, 1.0f)
                .addItemOutput("minecraft:chest_minecart", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/chest_minecart");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:rail", 4, 1.0f)
                .addItemInput("minecraft:stone_pressure_plate", 1, 1.0f)
                .addItemInput("minecraft:redstone", 1, 1.0f)
                .addItemOutput("minecraft:detector_rail", 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/detector_rail");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:dried_kelp", 9, 1.0f)
                .addItemOutput("minecraft:dried_kelp_block", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/dried_kelp");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:pointed_dripstone", 4, 1.0f)
                .addItemOutput("minecraft:dripstone_block", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/dripstone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:furnace", 1, 1.0f)
                .addItemInput("minecraft:minecart", 1, 1.0f)
                .addItemOutput("minecraft:furnace_minecart", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/furnace_minecart");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("#minecraft:stone_crafting_materials", 8, 1.0f)
                .addItemOutput("minecraft:furnace", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/furnace");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:gold_nugget", 4, 1.0f)
                .addItemInput("minecraft:melon_slice", 1, 1.0f)
                .addItemOutput("minecraft:glistering_melon_slice", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/glistering_melon_slice");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:glowstone_dust", 4, 1.0f)
                .addItemOutput("minecraft:glowstone", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/glowstone_block");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:gold_nugget", 4, 1.0f)
                .addItemInput("minecraft:carrot", 1, 1.0f)
                .addItemOutput("minecraft:golden_carrot", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/golden_carrot");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:wheat", 9, 1.0f)
                .addItemOutput("minecraft:hay_block", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/hayblock");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:honeycomb", 4, 1.0f)
                .addItemOutput("minecraft:honeycomb_block", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/honeycomb");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:hopper", 1, 1.0f)
                .addItemInput("minecraft:minecart", 1, 1.0f)
                .addItemOutput("minecraft:hopper_minecart", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/hopper_minecart");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:magma_cream", 4, 1.0f)
                .addItemOutput("minecraft:magma_block", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/magma_block");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:blaze_powder", 1, 1.0f)
                .addItemInput("minecraft:slime_ball", 1, 1.0f)
                .addItemOutput("minecraft:magma_cream", 2, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/magma_cream");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:melon_slice", 9, 1.0f)
                .addItemOutput("minecraft:melon", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/melon");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("milite:steel_large_plate", 1, 1.0f)
                .addItemInput("minecraft:rail", 1, 1.0f)
                .addItemOutput("minecraft:minecart", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/minecart");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:packed_mud", 4, 1.0f)
                .addItemOutput("minecraft:mud_bricks", 4, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/mud_bricks");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:nether_brick", 4, 1.0f)
                .addItemOutput("minecraft:nether_bricks", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/nether_bricks");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:nether_wart", 9, 1.0f)
                .addItemOutput("minecraft:nether_wart_block", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/nether_warts");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 200)
                .level("bronze")
                .addItemInput("minecraft:netherite_ingot", 9, 1.0f)
                .addItemOutput("minecraft:netherite_block", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/netherite");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:ice", 9, 1.0f)
                .addItemOutput("minecraft:packed_ice", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/packed_ice");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:prismarine_shard", 9, 1.0f)
                .addItemInput("milite:packer_block_template", 1, 0.0f)
                .addItemOutput("minecraft:prismarine_bricks", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/prismarine_bricks");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:prismarine_shard", 4, 1.0f)
                .addItemOutput("minecraft:prismarine", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/prismarine");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:popped_chorus_fruit", 4, 1.0f)
                .addItemOutput("minecraft:purpur_block", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/purpur");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:quartz", 4, 1.0f)
                .addItemOutput("minecraft:quartz_block", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/quartz_block");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:red_sand", 4, 1.0f)
                .addItemOutput("minecraft:red_sandstone", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/red_sandstone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:sand", 4, 1.0f)
                .addItemOutput("minecraft:sandstone", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/sandstone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:slime_ball", 9, 1.0f)
                .addItemOutput("minecraft:slime_block", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/slimeblock");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:snowball", 4, 1.0f)
                .addItemOutput("minecraft:snow_block", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/snowblock");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("minecraft:tnt", 1, 1.0f)
                .addItemInput("minecraft:minecart", 1, 1.0f)
                .addItemOutput("minecraft:tnt_minecart", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/packer/tnt_minecart");

        smelting(consumer.withConditions(new NotCondition(new ModLoadedCondition("dehydration"))), "vanilla_recipes/steam_bucket", "minecraft:water_bucket",
                "milite:steam_bucket", 1, 0.0f, 500);

        shaped(consumer, "vanilla_recipes/steel_anvil_asbl",
                new ShapedRecipeJson("minecraft:anvil", 2, "III", " i ", "iii")
                        .addInput('I', "milite:steel_block")
                        .addInput('i', "milite:steel_ingot"));

        shaped(consumer, "vanilla_recipes/steel_forge_hammer_asbl",
                new ShapedRecipeJson("milite:forge_hammer", 2, "___", " i ", "iii")
                        .addInput('_', "#c:plates/steel")
                        .addInput('i', "#c:ingots/steel"));

        shaped(consumer, "vanilla_recipes/steel_hopper_asbl",
                new ShapedRecipeJson("minecraft:hopper", 2, "p p", "pcp", " p ")
                        .addInput('c', "minecraft:chest")
                        .addInput('p', "#c:plates/steel"));

        shaped(consumer, "vanilla_recipes/torch",
                new ShapedRecipeJson("minecraft:torch", 8, "C", "s")
                        .addInput('C', "milite:coke")
                        .addInput('s', "minecraft:stick"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.VACUUM_FREEZER, 16, 200)
                .level("bronze")
                .addFluidInput("minecraft:water", 1000, 1.0f)
                .addItemOutput("minecraft:ice", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/vacuum_freezer/ice");

        new MachineRecipeBuilder(MIMachineRecipeTypes.VACUUM_FREEZER, 2, 2400)
                .level("bronze")
                .addFluidInput("minecraft:lava", 1000, 1.0f)
                .addItemOutput("minecraft:obsidian", 1, 1.0f)
                .offerTo(consumer, "vanilla_recipes/vacuum_freezer/obsidian");
    }
}
