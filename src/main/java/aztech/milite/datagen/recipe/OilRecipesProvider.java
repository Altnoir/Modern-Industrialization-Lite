package aztech.milite.datagen.recipe;

import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

public class OilRecipesProvider extends FixedRecipesProvider {
    public OilRecipesProvider(PackOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        new MachineRecipeBuilder(MIMachineRecipeTypes.BLAST_FURNACE, 32, 50)
                .level("electric")
                .addFluidInput("modern_tech:methane", 200, 1.0f)
                .addFluidOutput("modern_tech:acetylene", 200, 1.0f)
                .offerTo(consumer, "oil/blast_furnace/methane_to_acetylene");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 10, 80)
                .level("aluminum_alloy")
                .addItemInput(Items.SLIME_BALL, 1, 0.0f)
                .addFluidInput("modern_tech:acrylic_glue", 25, 1.0f)
                .addItemOutput(Items.SLIME_BALL, 1, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/acrylic_glue_to_slime");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 12, 200)
                .level("aluminum_alloy")
                .addItemInput("#c:dusts/coal", 1, 1.0f)
                .addFluidInput("modern_tech:acetylene", 1000, 1.0f)
                .addFluidOutput("modern_tech:hydrogen", 2000, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/actylene_to_hydrogen");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 12, 300)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:benzene", 750, 1.0f)
                .addFluidInput("modern_tech:ethylene", 750, 1.0f)
                .addFluidInput("modern_tech:hydrochloric_acid", 100, 1.0f)
                .addFluidOutput("modern_tech:ethylbenzene", 750, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/benzene_ethylene_to_ethylbenzene");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 16, 300)
                .level("aluminum_alloy")
                .addItemInput("#c:tiny_dusts/nickel", 4, 1.0f)
                .addFluidInput("modern_tech:benzene", 500, 1.0f)
                .addFluidInput("modern_tech:hydrogen", 750, 1.0f)
                .addFluidOutput("modern_tech:caprolactam", 600, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/caprolactam");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 16, 300)
                .level("aluminum_alloy")
                .addItemInput("#c:tiny_dusts/aluminum", 1, 1.0f)
                .addFluidInput("modern_tech:ethanol", 750, 1.0f)
                .addFluidOutput("modern_tech:butadiene", 375, 1.0f)
                .addFluidOutput(Fluids.WATER, 750, 1.0f)
                .addFluidOutput("modern_tech:hydrogen", 250, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/ethanol_to_butadiene");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 20, 300)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:ethanol", 500, 1.0f)
                .addFluidInput("modern_tech:acrylic_acid", 25, 1.0f)
                .addFluidOutput("modern_tech:diethyl_ether", 250, 1.0f)
                .addFluidOutput(Fluids.WATER, 250, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/ethanol_to_diethyl_ether");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 10, 280)
                .level("aluminum_alloy")
                .addItemInput("#c:dusts/sulfur", 1, 1.0f)
                .addFluidInput("modern_tech:ethanol", 1000, 1.0f)
                .addFluidOutput("modern_tech:ethylene", 500, 1.0f)
                .addFluidOutput("modern_tech:sulfuric_acid", 400, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/ethanol_to_ethylene");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 20, 240)
                .level("aluminum_alloy")
                .addItemInput("#c:dusts/iron", 1, 1.0f)
                .addFluidInput("modern_tech:ethylbenzene", 1000, 1.0f)
                .addFluidInput("modern_tech:steam", 2000, 1.0f)
                .addFluidOutput("modern_tech:styrene", 1000, 1.0f)
                .addFluidOutput("modern_tech:hydrogen", 500, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/ethylbenzene_to_styrene");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 10, 140)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:ethylene", 200, 1.0f)
                .addFluidInput(Fluids.WATER, 200, 1.0f)
                .addFluidInput("modern_tech:sulfuric_acid", 35, 1.0f)
                .addFluidOutput("modern_tech:ethanol", 200, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/ethylene_to_ethanol");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 8, 100)
                .level("aluminum_alloy")
                .addItemInput(Items.STRING, 1, 0.0f)
                .addFluidInput("modern_tech:nylon", 20, 1.0f)
                .addItemOutput(Items.STRING, 1, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/nylon_to_string");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 12, 300)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:plant_oil", 6000, 1.0f)
                .addFluidInput("modern_tech:ethanol", 400, 1.0f)
                .addFluidInput("modern_tech:sodium_hydroxide", 100, 1.0f)
                .addFluidOutput("modern_tech:raw_biodiesel", 1500, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/plant_oil_to_raw_biodiesel");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 10, 240)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:propene", 400, 1.0f)
                .addFluidInput("modern_tech:oxygen", 600, 1.0f)
                .addFluidOutput("modern_tech:acrylic_acid", 400, 1.0f)
                .addFluidOutput(Fluids.WATER, 400, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/propene_to_acrylic_acid");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 16, 200)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:raw_biodiesel", 1000, 1.0f)
                .addFluidInput("modern_tech:steam", 1000, 1.0f)
                .addFluidOutput("modern_tech:biodiesel", 700, 1.0f)
                .addFluidOutput("modern_tech:ethanol", 250, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/raw_biodiesel_to_biodiesel");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 10, 150)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:styrene", 500, 1.0f)
                .addFluidInput("modern_tech:butadiene", 500, 1.0f)
                .addFluidOutput("modern_tech:styrene_butadiene", 1000, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/styrene_butadiene");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 16, 500)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:acetylene", 1000, 1.0f)
                .addFluidInput("modern_tech:hydrochloric_acid", 1000, 1.0f)
                .addFluidOutput("modern_tech:vinyl_chloride", 1000, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/vinyl_chloride");

        new MachineRecipeBuilder(MIMachineRecipeTypes.OIL_DRILLING_RIG, 8, 200)
                .level("bronze")
                .addItemInput("modern_tech:aluminum_drill", 1, 0.04f)
                .addFluidOutput("modern_tech:crude_oil", 500, 1.0f)
                .offerTo(consumer, "oil/crude_oil");

        new MachineRecipeBuilder(MIMachineRecipeTypes.DISTILLERY, 8, 800)
                .level("aluminum_alloy")
                .addFluidInput("modern_tech:sugar_solution", 1000, 1.0f)
                .addFluidOutput("modern_tech:ethanol", 10, 1.0f)
                .offerTo(consumer, "oil/distillation/ethanol_from_sugar");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 8, 600)
                .level("bronze")
                .addFluidInput("modern_tech:heavy_fuel", 1000, 1.0f)
                .addFluidInput("modern_tech:light_fuel", 5000, 1.0f)
                .addFluidOutput("modern_tech:diesel", 6000, 1.0f)
                .offerTo(consumer, "oil/mixer/diesel");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 12, 450)
                .level("bronze")
                .addFluidInput("modern_tech:biodiesel", 2000, 1.0f)
                .addFluidInput("modern_tech:diethyl_ether", 250, 1.0f)
                .addFluidOutput("modern_tech:boosted_diesel", 1500, 1.0f)
                .offerTo(consumer, "oil/mixer/diethyl_ether_biodiesel_boost");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 12, 300)
                .level("bronze")
                .addFluidInput("modern_tech:diesel", 1000, 1.0f)
                .addFluidInput("modern_tech:diethyl_ether", 250, 1.0f)
                .addFluidOutput("modern_tech:boosted_diesel", 1200, 1.0f)
                .offerTo(consumer, "oil/mixer/diethyl_ether_diesel_boost");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 200)
                .level("bronze")
                .addItemInput(Items.SUGAR, 8, 1.0f)
                .addFluidInput(Fluids.WATER, 1000, 1.0f)
                .addFluidOutput("modern_tech:sugar_solution", 1000, 1.0f)
                .offerTo(consumer, "oil/mixer/sugar_solution");

        new MachineRecipeBuilder(MIMachineRecipeTypes.OIL_DRILLING_RIG, 32, 200)
                .level("bronze")
                .addItemInput("modern_tech:stainless_steel_drill", 1, 0.02f)
                .addFluidOutput("modern_tech:shale_oil", 4000, 1.0f)
                .offerTo(consumer, "oil/shale_oil");

        new MachineRecipeBuilder(MIMachineRecipeTypes.VACUUM_FREEZER, 128, 300)
                .level("bronze")
                .addFluidInput("modern_tech:styrene_butadiene_rubber", 200, 1.0f)
                .addItemOutput("modern_tech:rubber_sheet", 64, 1.0f)
                .offerTo(consumer, "oil/vacuum_freezer/styrene_butadiene_rubber");

        new MachineRecipeBuilder(MIMachineRecipeTypes.OIL_DRILLING_RIG, 2, 100)
                .level("bronze")
                .addItemInput("modern_tech:copper_drill", 1, 0.02f)
                .addFluidOutput(Fluids.WATER, 16000, 1.0f)
                .offerTo(consumer, "oil/water");
    }
}
