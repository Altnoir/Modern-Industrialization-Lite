package aztech.milite.datagen.recipe;

import aztech.milite.MIItem;

import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.recipe.json.ShapedRecipeJson;
import aztech.milite.recipe.json.ShapelessRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

public class FixedMaterialRecipesProvider extends FixedRecipesProvider {
    public FixedMaterialRecipesProvider(PackOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 32, 600)
                .level("bronze")
                .addItemInput("modern_tech:industrial_tnt", 6, 1.0f)
                .addItemInput("modern_tech:plutonium_ingot", 4, 1.0f)
                .addItemInput(MIItem.PROCESSOR, 2, 1.0f)
                .addItemInput("modern_tech:nuclear_alloy_plate", 4, 1.0f)
                .addFluidInput("modern_tech:tritium", 50, 1.0f)
                .addFluidInput("modern_tech:deuterium", 100, 1.0f)
                .addItemOutput("modern_tech:nuke", 2, 1.0f)
                .offerTo(consumer, "materials/assembler/nuke");

        new MachineRecipeBuilder(MIMachineRecipeTypes.BLAST_FURNACE, 64, 600)
                .level("stainless_steel")
                .addItemInput("#c:dusts/copper", 1, 1.0f)
                .addFluidInput("modern_tech:oxygen", 250, 1.0f)
                .addItemOutput("modern_tech:annealed_copper_hot_ingot", 1, 1.0f)
                .offerTo(consumer, "materials/blast_furnace/annealed_copper");

        new MachineRecipeBuilder(MIMachineRecipeTypes.STEAM_BLAST_FURNACE, 4, 100)
                .level("bronze")
                .addItemInput(Items.REDSTONE, 1, 1.0f)
                .addFluidOutput("modern_tech:molten_redstone", 90, 1.0f)
                .offerTo(consumer, "materials/blast_furnace/molten_redstone");

        new MachineRecipeBuilder(MIMachineRecipeTypes.STEAM_BLAST_FURNACE, 2, 200)
                .level("bronze")
                .addFluidInput("modern_tech:raw_synthetic_oil", 100, 1.0f)
                .addFluidOutput("modern_tech:synthetic_oil", 100, 1.0f)
                .offerTo(consumer, "materials/blast_furnace/raw_oil_to_oil");

        new MachineRecipeBuilder(MIMachineRecipeTypes.BLAST_FURNACE, 32, 400)
                .level("electric")
                .addItemInput("#c:dusts/quartz", 2, 1.0f)
                .addItemInput("modern_tech:carbon_dust", 1, 1.0f)
                .addItemOutput("modern_tech:silicon_dust", 2, 1.0f)
                .offerTo(consumer, "materials/blast_furnace/silicium_from_quartz_with_carbon");

        new MachineRecipeBuilder(MIMachineRecipeTypes.BLAST_FURNACE, 32, 400)
                .level("electric")
                .addItemInput("#c:dusts/quartz", 1, 1.0f)
                .addItemInput("modern_tech:coke_dust", 1, 1.0f)
                .addItemOutput("modern_tech:silicon_dust", 1, 1.0f)
                .offerTo(consumer, "materials/blast_furnace/silicium_from_quartz");

        new MachineRecipeBuilder(MIMachineRecipeTypes.STEAM_BLAST_FURNACE, 4, 60)
                .level("bronze")
                .addItemInput("#c:tiny_dusts/soldering_alloy", 1, 1.0f)
                .addFluidOutput("modern_tech:soldering_alloy", 10, 1.0f)
                .offerTo(consumer, "materials/blast_furnace/soldering_alloy_tiny_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.STEAM_BLAST_FURNACE, 4, 300)
                .level("bronze")
                .addItemInput("#c:dusts/soldering_alloy", 1, 1.0f)
                .addFluidOutput("modern_tech:soldering_alloy", 90, 1.0f)
                .offerTo(consumer, "materials/blast_furnace/soldering_alloy");

        new MachineRecipeBuilder(MIMachineRecipeTypes.BLAST_FURNACE, 16, 400)
                .level("electric")
                .addItemInput("modern_tech:carbon_dust", 1, 1.0f)
                .addItemInput("#c:dusts/iron", 4, 1.0f)
                .addItemOutput("modern_tech:steel_ingot", 5, 1.0f)
                .offerTo(consumer, "materials/blast_furnace/steel_with_carbon");

        new MachineRecipeBuilder(MIMachineRecipeTypes.STEAM_BLAST_FURNACE, 2, 600)
                .level("bronze")
                .addItemInput("modern_tech:uncooked_steel_dust", 1, 1.0f)
                .addItemOutput("modern_tech:steel_ingot", 1, 1.0f)
                .offerTo(consumer, "materials/blast_furnace/steel");

        new MachineRecipeBuilder(MIMachineRecipeTypes.BLAST_FURNACE, 128, 1200)
                .level("stainless_steel")
                .addItemInput("#c:dusts/superconductor", 1, 1.0f)
                .addItemOutput("modern_tech:superconductor_hot_ingot", 1, 1.0f)
                .offerTo(consumer, "materials/blast_furnace/superconductor");

        new MachineRecipeBuilder(MIMachineRecipeTypes.BLAST_FURNACE, 128, 400)
                .level("stainless_steel")
                .addItemInput("#c:raw_materials/titanium", 3, 1.0f)
                .addFluidInput("modern_tech:manganese_sulfuric_solution", 3000, 1.0f)
                .addItemOutput("modern_tech:titanium_hot_ingot", 4, 1.0f)
                .addFluidOutput("modern_tech:manganese_sulfuric_solution", 2500, 1.0f)
                .offerTo(consumer, "materials/blast_furnace/titanium_from_raw");

        new ShapelessRecipeBuilder("modern_tech:bronze_dust", 3)
                .requires("#c:dusts/copper")
                .requires("#c:dusts/copper")
                .requires("#c:dusts/copper")
                .requires("#c:dusts/tin")
                .offerTo(consumer, "materials/bronze_dust");

        new ShapelessRecipeBuilder("modern_tech:bronze_tiny_dust", 3)
                .requires("#c:tiny_dusts/copper")
                .requires("#c:tiny_dusts/copper")
                .requires("#c:tiny_dusts/copper")
                .requires("#c:tiny_dusts/tin")
                .offerTo(consumer, "materials/bronze_tiny_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 16, 400)
                .level("aluminum_alloy")
                .addItemInput("#c:dusts/coal", 1, 1.0f)
                .addItemOutput("modern_tech:carbon_dust", 1, 1.0f)
                .offerTo(consumer, "materials/centrifuge/carbon");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 16, 2400)
                .level("aluminum_alloy")
                .addItemInput("#c:dusts/ruby", 6, 1.0f)
                .addItemOutput("modern_tech:chromium_crushed_dust", 1, 1.0f)
                .addItemOutput("modern_tech:aluminum_dust", 2, 1.0f)
                .offerTo(consumer, "materials/centrifuge/chromium_crushed_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 32, 1200)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:hydrogen", 1000, 1.0f)
                .addFluidOutput("modern_tech:deuterium", 20, 1.0f)
                .addFluidOutput("modern_tech:tritium", 1, 1.0f)
                .offerTo(consumer, "materials/centrifuge/deuterium");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 8, 300)
                .level("aluminum_alloy")
                .addItemInput(Blocks.GRAVEL, 1, 1.0f)
                .addItemOutput(Items.FLINT, 2, 1.0f)
                .offerTo(consumer, "materials/centrifuge/flint");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 32, 1200)
                .level("aluminum_alloy")
                .addFluidInput(Fluids.WATER, 1000, 1.0f)
                .addFluidOutput("modern_tech:heavy_water", 20, 1.0f)
                .offerTo(consumer, "materials/centrifuge/heavy_water");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 32, 2000)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:helium", 1000, 1.0f)
                .addFluidOutput("modern_tech:helium_3", 5, 1.0f)
                .offerTo(consumer, "materials/centrifuge/helium_3");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 32, 100)
                .level("aluminum_alloy")
                .addItemInput("#c:dusts/lignite_coal", 27, 1.0f)
                .addItemOutput("modern_tech:coal_dust", 10, 1.0f)
                .offerTo(consumer, "materials/centrifuge/lignite_coal");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 24, 600)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:liquid_air", 3000, 1.0f)
                .addFluidOutput("modern_tech:oxygen", 650, 1.0f)
                .addFluidOutput("modern_tech:nitrogen", 2315, 1.0f)
                .addFluidOutput("modern_tech:argon", 35, 1.0f)
                .offerTo(consumer, "materials/centrifuge/liquid_air");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 8, 1200)
                .level("aluminum_alloy")
                .addItemInput(Items.RAW_IRON, 6, 1.0f)
                .addItemOutput("modern_tech:iron_dust", 8, 1.0f)
                .addItemOutput("modern_tech:manganese_crushed_dust", 1, 1.0f)
                .offerTo(consumer, "materials/centrifuge/manganese_crushed_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 10, 2400)
                .level("aluminum_alloy")
                .addItemInput("#c:dusts/monazite", 9, 1.0f)
                .addItemOutput("modern_tech:neodymium_dust", 3, 1.0f)
                .addItemOutput("modern_tech:yttrium_dust", 3, 1.0f)
                .addItemOutput("modern_tech:cadmium_dust", 3, 1.0f)
                .addFluidOutput("modern_tech:helium", 300, 0.5f)
                .offerTo(consumer, "materials/centrifuge/monazite_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 10, 240)
                .level("aluminum_alloy")
                .addItemInput("#c:tiny_dusts/monazite", 9, 1.0f)
                .addItemOutput("modern_tech:neodymium_tiny_dust", 3, 1.0f)
                .addItemOutput("modern_tech:yttrium_tiny_dust", 3, 1.0f)
                .addItemOutput("modern_tech:cadmium_tiny_dust", 3, 1.0f)
                .offerTo(consumer, "materials/centrifuge/monazite_tiny_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 32, 6000)
                .level("aluminum_alloy")
                .addItemInput("modern_tech:he_mox_fuel_rod_depleted", 1, 1.0f)
                .addItemOutput("modern_tech:uranium_238_tiny_dust", 36, 1.0f)
                .addItemOutput("modern_tech:plutonium_tiny_dust", 36, 1.0f)
                .offerTo(consumer, "materials/centrifuge/nuclear_recycling/he_mox_fuel_rod");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 32, 6000)
                .level("aluminum_alloy")
                .addItemInput("modern_tech:he_uranium_fuel_rod_depleted", 1, 1.0f)
                .addItemOutput("modern_tech:uranium_238_tiny_dust", 36, 1.0f)
                .addItemOutput("modern_tech:plutonium_tiny_dust", 18, 1.0f)
                .addItemOutput("modern_tech:uranium_235_tiny_dust", 18, 1.0f)
                .offerTo(consumer, "materials/centrifuge/nuclear_recycling/he_uranium_fuel_rod");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 32, 6000)
                .level("aluminum_alloy")
                .addItemInput("modern_tech:le_mox_fuel_rod_depleted", 1, 1.0f)
                .addItemOutput("modern_tech:uranium_238_tiny_dust", 48, 1.0f)
                .addItemOutput("modern_tech:plutonium_tiny_dust", 30, 1.0f)
                .offerTo(consumer, "materials/centrifuge/nuclear_recycling/le_mox_fuel_rod");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 32, 6000)
                .level("aluminum_alloy")
                .addItemInput("modern_tech:le_uranium_fuel_rod_depleted", 1, 1.0f)
                .addItemOutput("modern_tech:uranium_238_tiny_dust", 48, 1.0f)
                .addItemOutput("modern_tech:plutonium_tiny_dust", 24, 1.0f)
                .addItemOutput("modern_tech:uranium_235_tiny_dust", 6, 1.0f)
                .offerTo(consumer, "materials/centrifuge/nuclear_recycling/le_uranium_fuel_rod");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 32, 6000)
                .level("aluminum_alloy")
                .addItemInput("modern_tech:uranium_fuel_rod_depleted", 1, 1.0f)
                .addItemOutput("modern_tech:uranium_238_tiny_dust", 53, 1.0f)
                .addItemOutput("modern_tech:plutonium_tiny_dust", 27, 1.0f)
                .addItemOutput("modern_tech:uranium_235_tiny_dust", 1, 0.66f)
                .offerTo(consumer, "materials/centrifuge/nuclear_recycling/uranium_fuel_rod");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 8, 1200)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:platinum_sulfuric_solution", 1000, 1.0f)
                .addItemOutput("modern_tech:iridium_tiny_dust", 1, 0.05f)
                .addFluidOutput("modern_tech:purified_platinum_sulfuric_solution", 1000, 1.0f)
                .offerTo(consumer, "materials/centrifuge/purified_platinum_sulfuric_solution");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 8, 1200)
                .level("aluminum_alloy")
                .addItemInput(Items.REDSTONE, 10, 1.0f)
                .addItemOutput("modern_tech:iron_dust", 5, 1.0f)
                .addItemOutput("modern_tech:ruby_dust", 1, 1.0f)
                .addItemOutput("modern_tech:quartz_dust", 1, 1.0f)
                .addItemOutput("modern_tech:neodymium_tiny_dust", 1, 0.5f)
                .offerTo(consumer, "materials/centrifuge/ruby_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CENTRIFUGE, 64, 6000)
                .level("aluminum_alloy")
                .addItemInput("#c:dusts/uranium", 9, 1.0f)
                .addItemOutput("modern_tech:uranium_235_tiny_dust", 1, 1.0f)
                .addItemOutput("modern_tech:uranium_238_tiny_dust", 64, 1.0f)
                .addItemOutput("modern_tech:uranium_238_tiny_dust", 16, 1.0f)
                .addFluidOutput("modern_tech:helium", 150, 0.5f)
                .offerTo(consumer, "materials/centrifuge/uranium");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 8, 200)
                .level("aluminum_alloy")
                .addItemInput("modern_tech:chromium_crushed_dust", 1, 1.0f)
                .addFluidInput("modern_tech:hydrochloric_acid", 9000, 1.0f)
                .addFluidOutput("modern_tech:chromium_hydrochloric_solution", 9000, 1.0f)
                .offerTo(consumer, "materials/chemical_reactor/chromium_hydrochloric_solution");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 8, 200)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:oxygen", 1000, 1.0f)
                .addFluidInput("modern_tech:deuterium", 2000, 1.0f)
                .addFluidOutput("modern_tech:heavy_water", 3000, 1.0f)
                .offerTo(consumer, "materials/chemical_reactor/heavy_water");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 8, 100)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:hydrogen", 1000, 1.0f)
                .addFluidInput("modern_tech:chlorine", 1000, 1.0f)
                .addFluidOutput("modern_tech:hydrochloric_acid", 2000, 1.0f)
                .offerTo(consumer, "materials/chemical_reactor/hydrochloric_acid");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 8, 200)
                .level("aluminum_alloy")
                .addItemInput("modern_tech:manganese_crushed_dust", 1, 1.0f)
                .addFluidInput("modern_tech:sulfuric_acid", 9000, 1.0f)
                .addFluidOutput("modern_tech:manganese_sulfuric_solution", 9000, 1.0f)
                .offerTo(consumer, "materials/chemical_reactor/manganese_sulfuric_solution");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 8, 200)
                .level("aluminum_alloy")
                .addItemInput("#c:raw_materials/platinum", 1, 1.0f)
                .addFluidInput("modern_tech:sulfuric_acid", 9000, 1.0f)
                .addFluidOutput("modern_tech:platinum_sulfuric_solution", 9000, 1.0f)
                .offerTo(consumer, "materials/chemical_reactor/platinum_sulfuric_solution");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 8, 200)
                .level("aluminum_alloy")
                .addItemInput("#c:dusts/sodium", 1, 1.0f)
                .addFluidInput("modern_tech:oxygen", 1000, 1.0f)
                .addFluidInput("modern_tech:hydrogen", 1000, 1.0f)
                .addFluidOutput("modern_tech:sodium_hydroxide", 2000, 1.0f)
                .offerTo(consumer, "materials/chemical_reactor/sodium_hydroxide");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 16, 200)
                .level("aluminum_alloy")
                .addItemInput("#c:dusts/sulfur", 1, 1.0f)
                .addFluidInput("modern_tech:oxygen", 200, 1.0f)
                .addFluidInput(Fluids.WATER, 200, 1.0f)
                .addFluidOutput("modern_tech:sulfuric_acid", 500, 1.0f)
                .offerTo(consumer, "materials/chemical_reactor/sulfuric_acid");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 8, 200)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:oxygen", 1000, 1.0f)
                .addFluidInput("modern_tech:hydrogen", 2000, 1.0f)
                .addFluidOutput(Fluids.WATER, 3000, 1.0f)
                .offerTo(consumer, "materials/chemical_reactor/water");

        new MachineRecipeBuilder(MIMachineRecipeTypes.COKE_OVEN, 2, 600)
                .level("bronze")
                .addItemInput("#c:dusts/coal", 1, 1.0f)
                .addItemOutput("modern_tech:coke_dust", 1, 1.0f)
                .addFluidOutput("modern_tech:creosote", 500, 0.5f)
                .offerTo(consumer, "materials/coke_oven/coke_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.COKE_OVEN, 2, 600)
                .level("bronze")
                .addItemInput(Items.COAL, 1, 1.0f)
                .addItemOutput("modern_tech:coke", 1, 1.0f)
                .addFluidOutput("modern_tech:creosote", 500, 0.5f)
                .offerTo(consumer, "materials/coke_oven/coke");

        new MachineRecipeBuilder(MIMachineRecipeTypes.COMPRESSOR, 32, 600)
                .level("bronze")
                .addItemInput("modern_tech:mixed_ingot_blastproof", 2, 1.0f)
                .addItemOutput("modern_tech:blastproof_alloy_plate", 1, 1.0f)
                .offerTo(consumer, "materials/compressor/blastproof_alloy_plate");

        new MachineRecipeBuilder(MIMachineRecipeTypes.COMPRESSOR, 32, 2400)
                .level("bronze")
                .addItemInput("#c:tiny_dusts/tungsten", 1, 1.0f)
                .addItemOutput("modern_tech:tungsten_nugget", 1, 1.0f)
                .offerTo(consumer, "materials/compressor/tungsten_tiny_dust_to_ingot");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ELECTROLYZER, 32, 1200)
                .level("aluminum_alloy")
                .addItemInput("#c:dusts/bauxite", 10, 1.0f)
                .addItemOutput("modern_tech:aluminum_dust", 4, 1.0f)
                .addItemOutput("modern_tech:titanium_tiny_dust", 3, 1.0f)
                .addFluidOutput("modern_tech:oxygen", 2000, 0.5f)
                .offerTo(consumer, "materials/electrolyzer/bauxite");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ELECTROLYZER, 32, 400)
                .level("aluminum_alloy")
                .addItemInput("#c:dusts/emerald", 23, 1.0f)
                .addItemOutput("modern_tech:beryllium_dust", 3, 1.0f)
                .addItemOutput("modern_tech:aluminum_dust", 2, 1.0f)
                .addItemOutput("modern_tech:silicon_dust", 6, 1.0f)
                .addFluidOutput("modern_tech:oxygen", 3000, 1.0f)
                .offerTo(consumer, "materials/electrolyzer/beryllium_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ELECTROLYZER, 16, 400)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:chromium_hydrochloric_solution", 1000, 1.0f)
                .addItemOutput("modern_tech:chromium_tiny_dust", 3, 1.0f)
                .addFluidOutput("modern_tech:hydrogen", 450, 1.0f)
                .addFluidOutput("modern_tech:chlorine", 450, 1.0f)
                .offerTo(consumer, "materials/electrolyzer/chromium_tiny_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ELECTROLYZER, 8, 600)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:heavy_water", 3000, 1.0f)
                .addFluidOutput("modern_tech:deuterium", 2000, 1.0f)
                .addFluidOutput("modern_tech:oxygen", 1000, 1.0f)
                .offerTo(consumer, "materials/electrolyzer/heavy_water");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ELECTROLYZER, 32, 2400)
                .level("aluminum_alloy")
                .addItemInput("#c:dusts/lapis", 18, 1.0f)
                .addItemOutput("modern_tech:aluminum_dust", 3, 1.0f)
                .addItemOutput("modern_tech:sodium_dust", 2, 1.0f)
                .addItemOutput("modern_tech:silicon_dust", 1, 1.0f)
                .addFluidOutput("modern_tech:oxygen", 2500, 0.5f)
                .offerTo(consumer, "materials/electrolyzer/lapis");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ELECTROLYZER, 16, 400)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:manganese_sulfuric_solution", 1000, 1.0f)
                .addItemOutput("modern_tech:manganese_tiny_dust", 3, 1.0f)
                .addFluidOutput("modern_tech:sulfuric_acid", 900, 1.0f)
                .offerTo(consumer, "materials/electrolyzer/manganese_tiny_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ELECTROLYZER, 32, 600)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:purified_platinum_sulfuric_solution", 1000, 1.0f)
                .addItemOutput("modern_tech:platinum_tiny_dust", 3, 1.0f)
                .addFluidOutput("modern_tech:sulfuric_acid", 900, 1.0f)
                .offerTo(consumer, "materials/electrolyzer/platinum_tiny_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ELECTROLYZER, 8, 400)
                .level("aluminum_alloy")
                .addItemInput("#c:dusts/salt", 2, 1.0f)
                .addItemOutput("modern_tech:sodium_dust", 1, 1.0f)
                .addFluidOutput("modern_tech:chlorine", 125, 1.0f)
                .offerTo(consumer, "materials/electrolyzer/salt_electrolysis");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ELECTROLYZER, 32, 300)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:sulfuric_acid", 500, 1.0f)
                .addItemOutput("modern_tech:sulfur_dust", 1, 1.0f)
                .addFluidOutput("modern_tech:oxygen", 200, 1.0f)
                .addFluidOutput(Fluids.WATER, 200, 1.0f)
                .offerTo(consumer, "materials/electrolyzer/sulfuric_acid");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ELECTROLYZER, 40, 2500)
                .level("aluminum_alloy")
                .addItemInput("modern_tech:singularity", 1, 0.0f)
                .addFluidOutput("modern_tech:uu_matter", 1, 1.0f)
                .offerTo(consumer, "materials/electrolyzer/uu_matter");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ELECTROLYZER, 8, 600)
                .level("aluminum_alloy")
                .addFluidInput(Fluids.WATER, 3000, 1.0f)
                .addFluidOutput("modern_tech:hydrogen", 2000, 1.0f)
                .addFluidOutput("modern_tech:oxygen", 1000, 1.0f)
                .offerTo(consumer, "materials/electrolyzer/water");

        shaped(consumer, "materials/fire_clay_bricks",
                new ShapedRecipeJson("modern_tech:fire_clay_bricks", 1, "TT", "TT")
                        .addInput('T', "modern_tech:fire_clay_brick"));

        shaped(consumer, "materials/fire_clay_dust",
                new ShapedRecipeJson("modern_tech:fire_clay_dust", 3, "TC", "TC")
                        .addInput('C', "modern_tech:brick_dust")
                        .addInput('T', "minecraft:clay_ball"));

        forgeHammer(consumer, "materials/forge_hammer/brick_to_dust", "minecraft:brick", 1,
                "modern_tech:brick_dust", 1);

        forgeHammer(consumer, "materials/forge_hammer/brick_dust", "minecraft:brick", 1,
                "modern_tech:brick_tiny_dust", 3);

        forgeHammer(consumer, "materials/forge_hammer/copper_ore_to_dust", "#c:ores/copper", 1,
                "modern_tech:copper_dust", 12);

        forgeHammer(consumer, "materials/forge_hammer/copper_ore_to_raw", "#c:ores/copper", 1,
                "minecraft:raw_copper", 5);

        new MachineRecipeBuilder(MIMachineRecipeTypes.HEAT_EXCHANGER, 2, 400)
                .level("bronze")
                .addFluidInput(Fluids.LAVA, 10, 1.0f)
                .addFluidInput(Fluids.WATER, 500, 1.0f)
                .addItemOutput(Blocks.OBSIDIAN, 1, 0.01f)
                .addFluidOutput("modern_tech:steam", 8000, 1.0f)
                .offerTo(consumer, "materials/heat_exchanger/lava");

        new MachineRecipeBuilder(MIMachineRecipeTypes.IMPLOSION_COMPRESSOR, 1, 10)
                .level("bronze")
                .addItemInput("#c:tiny_dusts/beryllium", 9, 1.0f)
                .addItemInput("#c:nuggets/stainless_steel", 1, 1.0f)
                .addItemInput("modern_tech:industrial_tnt", 1, 1.0f)
                .addItemOutput("modern_tech:beryllium_ingot", 1, 1.0f)
                .offerTo(consumer, "materials/implosion_compressor/beryllium");

        new MachineRecipeBuilder(MIMachineRecipeTypes.IMPLOSION_COMPRESSOR, 1, 10)
                .level("bronze")
                .addItemInput("modern_tech:mixed_ingot_blastproof", 1, 1.0f)
                .addItemInput("modern_tech:industrial_tnt", 1, 1.0f)
                .addItemOutput("modern_tech:blastproof_alloy_ingot", 3, 1.0f)
                .offerTo(consumer, "materials/implosion_compressor/blastproof_alloy_plate");

        new MachineRecipeBuilder(MIMachineRecipeTypes.IMPLOSION_COMPRESSOR, 1, 10)
                .level("bronze")
                .addItemInput("#c:dusts/diamond", 1, 1.0f)
                .addItemInput("modern_tech:industrial_tnt", 1, 1.0f)
                .addItemOutput(Items.DIAMOND, 1, 1.0f)
                .offerTo(consumer, "materials/implosion_compressor/diamond");

        new MachineRecipeBuilder(MIMachineRecipeTypes.IMPLOSION_COMPRESSOR, 1, 10)
                .level("bronze")
                .addItemInput("#c:dusts/emerald", 1, 1.0f)
                .addItemInput("modern_tech:industrial_tnt", 1, 1.0f)
                .addItemOutput(Items.EMERALD, 1, 1.0f)
                .offerTo(consumer, "materials/implosion_compressor/emerald");

        new MachineRecipeBuilder(MIMachineRecipeTypes.IMPLOSION_COMPRESSOR, 1, 10)
                .level("bronze")
                .addItemInput("modern_tech:mixed_ingot_iridium", 1, 1.0f)
                .addItemInput("modern_tech:industrial_tnt", 1, 1.0f)
                .addItemOutput("modern_tech:iridium_plate", 1, 1.0f)
                .offerTo(consumer, "materials/implosion_compressor/iridium_plate");

        new MachineRecipeBuilder(MIMachineRecipeTypes.IMPLOSION_COMPRESSOR, 1, 10)
                .level("bronze")
                .addItemInput("modern_tech:mixed_plate_nuclear", 1, 1.0f)
                .addItemInput("modern_tech:industrial_tnt", 1, 1.0f)
                .addItemOutput("modern_tech:nuclear_alloy_plate", 3, 1.0f)
                .offerTo(consumer, "materials/implosion_compressor/nuclear_alloy_plate");

        new MachineRecipeBuilder(MIMachineRecipeTypes.IMPLOSION_COMPRESSOR, 1, 10)
                .level("bronze")
                .addItemInput("#c:tiny_dusts/tungsten", 9, 1.0f)
                .addItemInput("#c:nuggets/stainless_steel", 1, 1.0f)
                .addItemInput("modern_tech:industrial_tnt", 1, 1.0f)
                .addItemOutput("modern_tech:tungsten_ingot", 1, 1.0f)
                .offerTo(consumer, "materials/implosion_compressor/tungsten");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 200)
                .level("bronze")
                .addItemInput(Blocks.BRICKS, 1, 1.0f)
                .addItemOutput("modern_tech:brick_dust", 4, 1.0f)
                .offerTo(consumer, "materials/macerator/brick_dust_from_block");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput(ItemTags.TERRACOTTA, 1, 1.0f)
                .addItemOutput("modern_tech:brick_dust", 4, 1.0f)
                .offerTo(consumer, "materials/macerator/brick_dust_from_terracotta");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("#c:ores/copper", 1, 1.0f)
                .addItemOutput(Items.RAW_COPPER, 8, 1.0f)
                .offerTo(consumer, "materials/macerator/copper_ore_to_raw");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("#c:ores/lapis", 1, 1.0f)
                .addItemOutput("modern_tech:lapis_crushed_dust", 16, 1.0f)
                .offerTo(consumer, "materials/macerator/lapis_ore_to_crushed");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("#c:raw_materials/lead", 1, 1.0f)
                .addItemOutput("modern_tech:lead_dust", 1, 1.0f)
                .addItemOutput("modern_tech:raw_silver", 1, 0.5f)
                .addItemOutput("modern_tech:lead_dust", 1, 0.5f)
                .offerTo(consumer, "materials/macerator/lead_crushed_to_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("modern_tech:lignite_coal_crushed_dust", 1, 1.0f)
                .addItemOutput("modern_tech:lignite_coal_dust", 1, 1.0f)
                .addItemOutput("modern_tech:lignite_coal_dust", 1, 0.5f)
                .addItemOutput("modern_tech:sulfur_dust", 1, 0.5f)
                .offerTo(consumer, "materials/macerator/lignite_coal_crushed_to_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("#c:ores/quartz", 1, 1.0f)
                .addItemOutput("modern_tech:quartz_crushed_dust", 4, 1.0f)
                .offerTo(consumer, "materials/macerator/quartz_ore_to_crushed");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput("#c:ores/redstone", 1, 1.0f)
                .addItemOutput("modern_tech:redstone_crushed_dust", 6, 1.0f)
                .offerTo(consumer, "materials/macerator/redstone_ore_to_crushed");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MACERATOR, 2, 100)
                .level("bronze")
                .addItemInput(ItemTags.LOGS, 1, 1.0f)
                .addItemOutput("modern_tech:wood_pulp", 12, 1.0f)
                .offerTo(consumer, "materials/macerator/wood_pulp");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 400)
                .level("bronze")
                .addItemInput("modern_tech:carbon_dust", 1, 1.0f)
                .addFluidInput(Fluids.WATER, 100, 1.0f)
                .addFluidOutput("modern_tech:raw_synthetic_oil", 200, 1.0f)
                .offerTo(consumer, "materials/mixer/carbon_dust_to_raw_oil");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 400)
                .level("bronze")
                .addItemInput("modern_tech:coke_dust", 1, 1.0f)
                .addFluidInput(Fluids.WATER, 100, 1.0f)
                .addFluidOutput("modern_tech:raw_synthetic_oil", 200, 1.0f)
                .offerTo(consumer, "materials/mixer/coke_to_raw_oil");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("modern_tech:brick_dust", 2, 1.0f)
                .addItemInput(Items.CLAY_BALL, 2, 1.0f)
                .addItemOutput("modern_tech:fire_clay_dust", 4, 1.0f)
                .offerTo(consumer, "materials/mixer/fire_clay_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("modern_tech:coke_dust", 1, 1.0f)
                .addItemInput("#c:dusts/sulfur", 1, 1.0f)
                .addItemOutput(Items.GUNPOWDER, 2, 1.0f)
                .offerTo(consumer, "materials/mixer/gunpowder");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 16, 200)
                .level("bronze")
                .addItemInput(ItemTags.SAND, 4, 1.0f)
                .addItemInput(Items.FLINT, 3, 1.0f)
                .addFluidInput("modern_tech:toluene", 250, 1.0f)
                .addFluidInput("modern_tech:nitrogen", 1000, 1.0f)
                .addItemOutput("modern_tech:industrial_tnt", 4, 1.0f)
                .offerTo(consumer, "materials/mixer/itnt");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput(Items.PAPER, 1, 1.0f)
                .addFluidInput("modern_tech:synthetic_oil", 100, 1.0f)
                .addItemOutput("modern_tech:rubber_sheet", 2, 1.0f)
                .offerTo(consumer, "materials/mixer/oil_to_rubber_sheet");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 4, 300)
                .level("bronze")
                .addItemInput("#c:dusts/sulfur", 1, 1.0f)
                .addFluidInput("modern_tech:synthetic_oil", 1000, 1.0f)
                .addFluidOutput("modern_tech:synthetic_rubber", 1000, 1.0f)
                .offerTo(consumer, "materials/mixer/oil_to_rubber");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput(Items.PAPER, 1, 1.0f)
                .addFluidInput("modern_tech:synthetic_rubber", 100, 1.0f)
                .addItemOutput("modern_tech:rubber_sheet", 12, 1.0f)
                .offerTo(consumer, "materials/mixer/rubber_sheet_from_synthetic");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 100)
                .level("bronze")
                .addItemInput("#c:dusts/iron", 7, 1.0f)
                .addItemInput("modern_tech:coke_dust", 2, 1.0f)
                .addItemOutput("modern_tech:uncooked_steel_dust", 9, 1.0f)
                .offerTo(consumer, "materials/mixer/uncooked_steel_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 100)
                .level("bronze")
                .addItemInput("modern_tech:fire_clay_brick", 4, 1.0f)
                .addItemOutput("modern_tech:fire_clay_bricks", 1, 1.0f)
                .offerTo(consumer, "materials/packer/fire_bricks");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 200)
                .level("bronze")
                .addItemInput("#c:ingots/tungsten", 1, 1.0f)
                .addItemInput("#c:ingots/titanium", 1, 1.0f)
                .addItemInput("#c:ingots/stainless_steel", 1, 1.0f)
                .addItemOutput("modern_tech:mixed_ingot_blastproof", 1, 1.0f)
                .offerTo(consumer, "materials/packer/mixed_ingot_blastproof");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 200)
                .level("bronze")
                .addItemInput("#c:ingots/blastproof_alloy", 1, 1.0f)
                .addItemInput("#c:ingots/iridium", 1, 1.0f)
                .addItemInput("#c:ingots/blastproof_alloy", 1, 1.0f)
                .addItemOutput("modern_tech:mixed_ingot_iridium", 1, 1.0f)
                .offerTo(consumer, "materials/packer/mixed_ingot_iridium");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PACKER, 2, 200)
                .level("bronze")
                .addItemInput("modern_tech:blastproof_alloy_plate", 1, 1.0f)
                .addItemInput("#c:plates/beryllium", 1, 1.0f)
                .addItemInput("#c:plates/cadmium", 1, 1.0f)
                .addItemOutput("modern_tech:mixed_plate_nuclear", 1, 1.0f)
                .offerTo(consumer, "materials/packer/mixed_plate_nuclear");

        new MachineRecipeBuilder(MIMachineRecipeTypes.PRESSURIZER, 8, 200)
                .level("bronze")
                .addItemInput("modern_tech:air_intake", 1, 0.0f)
                .addFluidOutput("modern_tech:liquid_air", 1000, 1.0f)
                .offerTo(consumer, "materials/pressurizer/liquid_air");

        new ShapelessRecipeBuilder("modern_tech:uncooked_steel_dust", 7)
                .requires("#c:dusts/iron")
                .requires("#c:dusts/iron")
                .requires("#c:dusts/iron")
                .requires("#c:dusts/iron")
                .requires("#c:dusts/iron")
                .requires("#c:dusts/iron")
                .requires("#c:dusts/iron")
                .requires("modern_tech:coke_dust")
                .requires("modern_tech:coke_dust")
                .offerTo(consumer, "materials/uncooked_steel_dust");

        new MachineRecipeBuilder(MIMachineRecipeTypes.VACUUM_FREEZER, 256, 250)
                .level("bronze")
                .addFluidInput("modern_tech:argon", 140, 1.0f)
                .addFluidInput("modern_tech:helium", 60, 1.0f)
                .addFluidOutput("modern_tech:cryofluid", 200, 1.0f)
                .offerTo(consumer, "materials/vacuum_freezer/cryofluid");

        new MachineRecipeBuilder(MIMachineRecipeTypes.VACUUM_FREEZER, 32, 200)
                .level("bronze")
                .addItemInput("modern_tech:air_intake", 1, 0.0f)
                .addFluidOutput("modern_tech:liquid_air", 1000, 1.0f)
                .offerTo(consumer, "materials/vacuum_freezer/liquid_air");

        new MachineRecipeBuilder(MIMachineRecipeTypes.VACUUM_FREEZER, 16, 5)
                .level("bronze")
                .addFluidInput("modern_tech:molten_redstone", 90, 1.0f)
                .addItemOutput(Items.REDSTONE, 1, 1.0f)
                .offerTo(consumer, "materials/vacuum_freezer/redstone");
    }
}
