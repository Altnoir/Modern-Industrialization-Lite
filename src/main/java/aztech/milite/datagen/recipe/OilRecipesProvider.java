package aztech.milite.datagen.recipe;

import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;

public class OilRecipesProvider extends FixedRecipesProvider {
    public OilRecipesProvider(PackOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        new MachineRecipeBuilder(MIMachineRecipeTypes.BLAST_FURNACE, 32, 50)
                .level("electric")
                .addFluidInput("milite:methane", 200, 1.0f)
                .addFluidOutput("milite:acetylene", 200, 1.0f)
                .offerTo(consumer, "oil/blast_furnace/methane_to_acetylene");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 10, 80)
                .level("bronze")
                .addItemInput("minecraft:slime_ball", 1, 0.0f)
                .addFluidInput("milite:acrylic_glue", 25, 1.0f)
                .addItemOutput("minecraft:slime_ball", 1, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/acrylic_glue_to_slime");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 12, 200)
                .level("bronze")
                .addItemInput("#c:dusts/coal", 1, 1.0f)
                .addFluidInput("milite:acetylene", 1000, 1.0f)
                .addFluidOutput("milite:hydrogen", 2000, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/actylene_to_hydrogen");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 12, 300)
                .level("bronze")
                .addFluidInput("milite:benzene", 750, 1.0f)
                .addFluidInput("milite:ethylene", 750, 1.0f)
                .addFluidInput("milite:hydrochloric_acid", 100, 1.0f)
                .addFluidOutput("milite:ethylbenzene", 750, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/benzene_ethylene_to_ethylbenzene");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 16, 300)
                .level("bronze")
                .addItemInput("#c:tiny_dusts/nickel", 4, 1.0f)
                .addFluidInput("milite:benzene", 500, 1.0f)
                .addFluidInput("milite:hydrogen", 750, 1.0f)
                .addFluidOutput("milite:caprolactam", 600, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/caprolactam");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 16, 300)
                .level("bronze")
                .addItemInput("#c:tiny_dusts/aluminum", 1, 1.0f)
                .addFluidInput("milite:ethanol", 750, 1.0f)
                .addFluidOutput("milite:butadiene", 375, 1.0f)
                .addFluidOutput("minecraft:water", 750, 1.0f)
                .addFluidOutput("milite:hydrogen", 250, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/ethanol_to_butadiene");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 20, 300)
                .level("bronze")
                .addFluidInput("milite:ethanol", 500, 1.0f)
                .addFluidInput("milite:acrylic_acid", 25, 1.0f)
                .addFluidOutput("milite:diethyl_ether", 250, 1.0f)
                .addFluidOutput("minecraft:water", 250, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/ethanol_to_diethyl_ether");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 10, 280)
                .level("bronze")
                .addItemInput("#c:dusts/sulfur", 1, 1.0f)
                .addFluidInput("milite:ethanol", 1000, 1.0f)
                .addFluidOutput("milite:ethylene", 500, 1.0f)
                .addFluidOutput("milite:sulfuric_acid", 400, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/ethanol_to_ethylene");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 20, 240)
                .level("bronze")
                .addItemInput("#c:dusts/iron", 1, 1.0f)
                .addFluidInput("milite:ethylbenzene", 1000, 1.0f)
                .addFluidInput("milite:steam", 2000, 1.0f)
                .addFluidOutput("milite:styrene", 1000, 1.0f)
                .addFluidOutput("milite:hydrogen", 500, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/ethylbenzene_to_styrene");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 10, 140)
                .level("bronze")
                .addFluidInput("milite:ethylene", 200, 1.0f)
                .addFluidInput("minecraft:water", 200, 1.0f)
                .addFluidInput("milite:sulfuric_acid", 35, 1.0f)
                .addFluidOutput("milite:ethanol", 200, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/ethylene_to_ethanol");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 8, 100)
                .level("bronze")
                .addItemInput("minecraft:string", 1, 0.0f)
                .addFluidInput("milite:nylon", 20, 1.0f)
                .addItemOutput("minecraft:string", 1, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/nylon_to_string");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 12, 300)
                .level("bronze")
                .addFluidInput("milite:plant_oil", 6000, 1.0f)
                .addFluidInput("milite:ethanol", 400, 1.0f)
                .addFluidInput("milite:sodium_hydroxide", 100, 1.0f)
                .addFluidOutput("milite:raw_biodiesel", 1500, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/plant_oil_to_raw_biodiesel");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 10, 240)
                .level("bronze")
                .addFluidInput("milite:propene", 400, 1.0f)
                .addFluidInput("milite:oxygen", 600, 1.0f)
                .addFluidOutput("milite:acrylic_acid", 400, 1.0f)
                .addFluidOutput("minecraft:water", 400, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/propene_to_acrylic_acid");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 16, 200)
                .level("bronze")
                .addFluidInput("milite:raw_biodiesel", 1000, 1.0f)
                .addFluidInput("milite:steam", 1000, 1.0f)
                .addFluidOutput("milite:biodiesel", 700, 1.0f)
                .addFluidOutput("milite:ethanol", 250, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/raw_biodiesel_to_biodiesel");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 10, 150)
                .level("bronze")
                .addFluidInput("milite:styrene", 500, 1.0f)
                .addFluidInput("milite:butadiene", 500, 1.0f)
                .addFluidOutput("milite:styrene_butadiene", 1000, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/styrene_butadiene");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 16, 500)
                .level("bronze")
                .addFluidInput("milite:acetylene", 1000, 1.0f)
                .addFluidInput("milite:hydrochloric_acid", 1000, 1.0f)
                .addFluidOutput("milite:vinyl_chloride", 1000, 1.0f)
                .offerTo(consumer, "oil/chemical_reactor/vinyl_chloride");

        new MachineRecipeBuilder(MIMachineRecipeTypes.OIL_DRILLING_RIG, 8, 200)
                .level("bronze")
                .addItemInput("milite:aluminum_drill", 1, 0.04f)
                .addFluidOutput("milite:crude_oil", 500, 1.0f)
                .offerTo(consumer, "oil/crude_oil");

        new MachineRecipeBuilder(MIMachineRecipeTypes.DISTILLERY, 8, 800)
                .level("bronze")
                .addFluidInput("milite:sugar_solution", 1000, 1.0f)
                .addFluidOutput("milite:ethanol", 10, 1.0f)
                .offerTo(consumer, "oil/distillation/ethanol_from_sugar");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 8, 600)
                .level("bronze")
                .addFluidInput("milite:heavy_fuel", 1000, 1.0f)
                .addFluidInput("milite:light_fuel", 5000, 1.0f)
                .addFluidOutput("milite:diesel", 6000, 1.0f)
                .offerTo(consumer, "oil/mixer/diesel");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 12, 450)
                .level("bronze")
                .addFluidInput("milite:biodiesel", 2000, 1.0f)
                .addFluidInput("milite:diethyl_ether", 250, 1.0f)
                .addFluidOutput("milite:boosted_diesel", 1500, 1.0f)
                .offerTo(consumer, "oil/mixer/diethyl_ether_biodiesel_boost");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 12, 300)
                .level("bronze")
                .addFluidInput("milite:diesel", 1000, 1.0f)
                .addFluidInput("milite:diethyl_ether", 250, 1.0f)
                .addFluidOutput("milite:boosted_diesel", 1200, 1.0f)
                .offerTo(consumer, "oil/mixer/diethyl_ether_diesel_boost");

        new MachineRecipeBuilder(MIMachineRecipeTypes.MIXER, 2, 200)
                .level("bronze")
                .addItemInput("minecraft:sugar", 8, 1.0f)
                .addFluidInput("minecraft:water", 1000, 1.0f)
                .addFluidOutput("milite:sugar_solution", 1000, 1.0f)
                .offerTo(consumer, "oil/mixer/sugar_solution");

        new MachineRecipeBuilder(MIMachineRecipeTypes.OIL_DRILLING_RIG, 32, 200)
                .level("bronze")
                .addItemInput("milite:stainless_steel_drill", 1, 0.02f)
                .addFluidOutput("milite:shale_oil", 4000, 1.0f)
                .offerTo(consumer, "oil/shale_oil");

        new MachineRecipeBuilder(MIMachineRecipeTypes.VACUUM_FREEZER, 128, 300)
                .level("bronze")
                .addFluidInput("milite:styrene_butadiene_rubber", 200, 1.0f)
                .addItemOutput("milite:rubber_sheet", 64, 1.0f)
                .offerTo(consumer, "oil/vacuum_freezer/styrene_butadiene_rubber");

        new MachineRecipeBuilder(MIMachineRecipeTypes.OIL_DRILLING_RIG, 2, 100)
                .level("bronze")
                .addItemInput("milite:copper_drill", 1, 0.02f)
                .addFluidOutput("minecraft:water", 16000, 1.0f)
                .offerTo(consumer, "oil/water");
    }
}
