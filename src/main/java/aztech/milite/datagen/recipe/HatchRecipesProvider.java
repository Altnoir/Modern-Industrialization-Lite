package aztech.milite.datagen.recipe;

import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.recipe.json.ShapedRecipeJson;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;

public class HatchRecipesProvider extends MIRecipesProvider {
    private static final String pathPrefix = "hatches/";

    public HatchRecipesProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        String[] casings = { "bronze", "steel", "electric", "aluminum_alloy", "stainless_steel", "titanium", "iridium" };
        String[] tanks = { "bronze", "steel", "", "aluminum", "stainless_steel", "titanium", "" };
        // ModernTech has no cables, so energy hatches are crafted with the wire of the same material.
        String[] wires = { "", "", "tin", "electrum", "aluminum", "annealed_copper", "superconductor" };

        for (int i = 0; i < casings.length; i++) {

            String casing = String.format("modern_tech:%s_machine_hull", casings[i]);
            if (i < 2) {
                casing = String.format("modern_tech:%s_machine_casing", casings[i]);
            }

            String tank = String.format("modern_tech:%s_tank", tanks[i]);
            String fluidInput = String.format("modern_tech:%s_fluid_input_hatch", casings[i]);
            String fluidOutput = String.format("modern_tech:%s_fluid_output_hatch", casings[i]);
            String itemInput = String.format("modern_tech:%s_item_input_hatch", casings[i]);
            String itemOutput = String.format("modern_tech:%s_item_output_hatch", casings[i]);
            String energyInput = String.format("modern_tech:%s_energy_input_hatch", casings[i]);
            String energyOutput = String.format("modern_tech:%s_energy_output_hatch", casings[i]);
            String wire = String.format("modern_tech:%s_wire", wires[i]);
            String hopper = "minecraft:hopper";

            String[][] ABs = { { fluidInput, fluidOutput }, { itemInput, itemOutput }, { energyInput, energyOutput } };
            String[][] prefixes = { { "fluid_input", "fluid_output" }, { "item_input", "item_output" }, { "energy_input", "energy_output" } };
            String[] others = { tank, hopper, wire };

            for (int j = 0; j < ABs.length; j++) {

                if (((j != 2) && !casings[i].equals("electric") && !casings[i].equals("iridium")) || (j == 2 && !wires[i].equals(""))) {

                    String[] AB = ABs[j];
                    String other = others[j];

                    for (int k = 0; k < 2; k++) {
                        ShapedRecipeJson craft = new ShapedRecipeJson(AB[k], 1, "U", "V").addInput('U', k == 0 ? other : casing).addInput('V',
                                k == 1 ? other : casing);

                        MachineRecipeBuilder craftAsbl = craft.exportToMachine(MIMachineRecipeTypes.ASSEMBLER, 8, 200, 1,
                                MachineLevel.ELECTRIC);

                        MachineRecipeBuilder unpacker = new MachineRecipeBuilder(MIMachineRecipeTypes.UNPACKER, 2, 200).addItemOutput(casing, 1)
                                .addItemOutput(other, 1).addItemInput(AB[k], 1);

                        ShapedRecipeJson craftFromOther = new ShapedRecipeJson(AB[k], 1, "U").addInput('U', AB[(k + 1) % 2]);

                        craft.offerTo(consumer, pathPrefix + casings[i] + "/" + prefixes[j][k] + "_hatch");
                        craftFromOther.offerTo(consumer, pathPrefix + casings[i] + "/" + prefixes[j][k] + "_from_" + (k == 0 ? "output" : "input"));
                        craftAsbl.offerTo(consumer, pathPrefix + casings[i] + "/assembler/" + prefixes[j][k] + "_hatch");
                        unpacker.offerTo(consumer, pathPrefix + casings[i] + "/unpacker/" + prefixes[j][k] + "_hatch");

                    }

                }
            }

        }
    }
}
