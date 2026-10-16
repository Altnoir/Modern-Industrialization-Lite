package aztech.milite.datagen.recipe;

import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.recipe.json.ShapedRecipeJson;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;

public class ElectricAgeRecipesProvider extends FixedRecipesProvider {
    public ElectricAgeRecipesProvider(PackOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        shaped(consumer, "electric_age/battery/cadmium_battery_asbl",
                new ShapedRecipeJson("milite:cadmium_battery", 1, "cac", "BrB", "BrB")
                        .addInput('a', "#c:plates/battery_alloy")
                        .addInput('c', "milite:annealed_copper_wire")
                        .addInput('B', "milite:battery_alloy_plate")
                        .addInput('r', "#c:dusts/cadmium"));

        shaped(consumer, "electric_age/battery/lv_battery_asbl",
                new ShapedRecipeJson("milite:redstone_battery", 1, "cac", "BrB", "BrB")
                        .addInput('a', "#c:plates/battery_alloy")
                        .addInput('c', "milite:tin_wire")
                        .addInput('B', "milite:battery_alloy_plate")
                        .addInput('r', "minecraft:redstone"));

        shaped(consumer, "electric_age/battery/plutonium_battery_asbl",
                new ShapedRecipeJson("milite:plutonium_battery", 1, "cac", "BrB", "BrB")
                        .addInput('a', "#c:plates/battery_alloy")
                        .addInput('c', "milite:superconductor_wire")
                        .addInput('B', "milite:battery_alloy_plate")
                        .addInput('r', "#c:dusts/plutonium"));

        shaped(consumer, "electric_age/battery/portable_storage_unit_asbl",
                new ShapedRecipeJson("milite:portable_storage_unit", 1, "aCa", "crc", "aCa")
                        .addInput('a', "#c:plates/battery_alloy")
                        .addInput('c', "milite:battery_alloy_plate")
                        .addInput('C', "milite:tin_wire")
                        .addInput('r', "milite:analog_circuit"));

        shaped(consumer, "electric_age/battery/silicon_battery_asbl",
                new ShapedRecipeJson("milite:silicon_battery", 1, "cac", "BrB", "BrB")
                        .addInput('a', "#c:plates/battery_alloy")
                        .addInput('c', "milite:electrum_wire")
                        .addInput('B', "milite:battery_alloy_plate")
                        .addInput('r', "#c:dusts/silicon"));

        shaped(consumer, "electric_age/battery/sodium_battery_asbl",
                new ShapedRecipeJson("milite:sodium_battery", 1, "cac", "BrB", "BrB")
                        .addInput('a', "#c:plates/battery_alloy")
                        .addInput('c', "milite:aluminum_wire")
                        .addInput('B', "milite:battery_alloy_plate")
                        .addInput('r', "#c:dusts/sodium"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 32, 1000)
                .level("bronze")
                .addItemInput("milite:iridium_machine_hull", 1, 1.0f)
                .addItemInput("milite:singularity", 1, 1.0f)
                .addItemInput("milite:quantum_circuit", 2, 1.0f)
                .addItemInput("#c:plates/iridium", 16, 1.0f)
                .addItemInput("milite:large_advanced_motor", 8, 1.0f)
                .addItemInput("milite:titanium_barrel", 1, 1.0f)
                .addItemOutput("milite:quantum_barrel", 1, 1.0f)
                .offerTo(consumer, "electric_age/blocks/assembler/quantum_barrel");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 32, 1000)
                .level("bronze")
                .addItemInput("milite:iridium_machine_hull", 1, 1.0f)
                .addItemInput("milite:singularity", 1, 1.0f)
                .addItemInput("milite:quantum_circuit", 2, 1.0f)
                .addItemInput("#c:plates/iridium", 16, 1.0f)
                .addItemInput("milite:large_advanced_pump", 8, 1.0f)
                .addItemInput("milite:titanium_tank", 1, 1.0f)
                .addItemOutput("milite:quantum_tank", 1, 1.0f)
                .offerTo(consumer, "electric_age/blocks/assembler/quantum_tank");

        shaped(consumer, "electric_age/blocks/fusion_chamber_asbl",
                new ShapedRecipeJson("milite:fusion_chamber", 1, "ChC", "LHL", "ChC")
                        .addInput('C', "milite:superconductor_coil")
                        .addInput('h', "milite:large_heat_exchanger")
                        .addInput('L', "milite:nuclear_alloy_large_plate")
                        .addInput('H', "milite:plasma_handling_iridium_machine_casing"));

        shaped(consumer, "electric_age/casing/aluminum_alloy_machine_casing_asbl",
                new ShapedRecipeJson("milite:aluminum_alloy_machine_casing", 1, "PPP", "PGP", "PPP")
                        .addInput('P', "#c:plates/aluminum_alloy")
                        .addInput('G', "#c:gears/aluminum_alloy"));

        shaped(consumer, "electric_age/casing/blastproof_casing_asbl",
                new ShapedRecipeJson("milite:blastproof_casing", 1, "AAA", "A A", "AAA")
                        .addInput('A', "#c:plates/blastproof_alloy"));

        shaped(consumer, "electric_age/casing/bronze_plated_bricks_asbl",
                new ShapedRecipeJson("milite:bronze_plated_bricks", 1, "AAA", "AHA", "AAA")
                        .addInput('A', "#c:plates/bronze")
                        .addInput('H', "milite:fire_clay_bricks"));

        shaped(consumer, "electric_age/casing/clean_stainless_steel_machine_casing_asbl",
                new ShapedRecipeJson("milite:clean_stainless_steel_machine_casing", 1, "AAA", "AGA", "AAA")
                        .addInput('A', "#c:plates/stainless_steel")
                        .addInput('G', "#c:gears/stainless_steel"));

        shaped(consumer, "electric_age/casing/frostproof_machine_casing_asbl",
                new ShapedRecipeJson("milite:frostproof_machine_casing", 1, "AAA", "AGA", "AAA")
                        .addInput('A', "#c:plates/aluminum")
                        .addInput('G', "#c:gears/aluminum"));

        shaped(consumer, "electric_age/casing/heatproof_machine_casing_asbl",
                new ShapedRecipeJson("milite:heatproof_machine_casing", 1, "PPP", "PGP", "PPP")
                        .addInput('P', "#c:plates/invar")
                        .addInput('G', "#c:gears/invar"));

        shaped(consumer, "electric_age/casing/iridium_machine_casing_asbl",
                new ShapedRecipeJson("milite:iridium_machine_casing", 1, "AAA", "AHA", "AAA")
                        .addInput('A', "#c:plates/iridium")
                        .addInput('H', "milite:titanium_machine_hull"));

        shaped(consumer, "electric_age/casing/iridium_machine_casing_pipe_asbl",
                new ShapedRecipeJson("milite:iridium_machine_casing_pipe", 2, "pPp", "p#p", "pPp")
                        .addInput('#', "milite:plasma_handling_iridium_machine_casing")
                        .addInput('p', "milite:iridium_plate")
                        .addInput('P', "milite:large_advanced_pump"));

        shaped(consumer, "electric_age/casing/nuclear_casing_asbl",
                new ShapedRecipeJson("milite:nuclear_casing", 1, "AAA", "ACA", "AAA")
                        .addInput('A', "#c:plates/nuclear_alloy")
                        .addInput('C', "milite:stainless_steel_machine_hull"));

        shaped(consumer, "electric_age/casing/nuclear_fluid_hatch_asbl",
                new ShapedRecipeJson("milite:nuclear_fluid_hatch", 1, "AAA", "ACA", "AAA")
                        .addInput('A', "#c:plates/nuclear_alloy")
                        .addInput('C', "milite:titanium_fluid_input_hatch"));

        shaped(consumer, "electric_age/casing/nuclear_item_hatch_asbl",
                new ShapedRecipeJson("milite:nuclear_item_hatch", 1, "AAA", "ACA", "AAA")
                        .addInput('A', "#c:plates/nuclear_alloy")
                        .addInput('C', "milite:titanium_item_input_hatch"));

        shaped(consumer, "electric_age/casing/nuclear_pipe_casing_asbl",
                new ShapedRecipeJson("milite:nuclear_alloy_machine_casing_pipe", 1, "cPc", "cCc", "cPc")
                        .addInput('c', "milite:blastproof_alloy_plate")
                        .addInput('P', "milite:advanced_pump")
                        .addInput('C', "milite:nuclear_casing"));

        shaped(consumer, "electric_age/casing/plasma_handling_iridium_machine_casing_asbl",
                new ShapedRecipeJson("milite:plasma_handling_iridium_machine_casing", 1, "AAA", "ACA", "AAA")
                        .addInput('A', "#c:plates/iridium")
                        .addInput('C', "milite:superconductor_coil"));

        shaped(consumer, "electric_age/casing/solid_titanium_machine_casing_asbl",
                new ShapedRecipeJson("milite:solid_titanium_machine_casing", 1, "AAA", "AGA", "AAA")
                        .addInput('A', "#c:plates/titanium")
                        .addInput('G', "#c:gears/titanium"));

        shaped(consumer, "electric_age/casing/stainless_steel_casing_pipe_asbl",
                new ShapedRecipeJson("milite:stainless_steel_machine_casing_pipe", 2, "p p", "p#p", "p p")
                        .addInput('#', "milite:clean_stainless_steel_machine_casing")
                        .addInput('p', "milite:stainless_steel_plate"));

        shaped(consumer, "electric_age/casing/stainless_steel_machine_casing_asbl",
                new ShapedRecipeJson("milite:stainless_steel_machine_casing", 1, "AAA", "AHA", "AAA")
                        .addInput('A', "#c:plates/stainless_steel")
                        .addInput('H', "milite:aluminum_alloy_machine_hull"));

        shaped(consumer, "electric_age/casing/titanium_casing_pipe_asbl",
                new ShapedRecipeJson("milite:titanium_machine_casing_pipe", 2, "p p", "p#p", "p p")
                        .addInput('#', "milite:solid_titanium_machine_casing")
                        .addInput('p', "milite:titanium_plate"));

        shaped(consumer, "electric_age/casing/titanium_machine_casing_asbl",
                new ShapedRecipeJson("milite:titanium_machine_casing", 1, "ACA", "CHC", "ACA")
                        .addInput('A', "#c:plates/titanium")
                        .addInput('C', "#c:plates/chromium")
                        .addInput('H', "milite:stainless_steel_machine_hull"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 16, 500)
                .level("bronze")
                .addItemInput("milite:aluminum_wire", 6, 1.0f)
                .addItemInput("milite:silicon_battery", 1, 1.0f)
                .addItemInput("milite:electronic_circuit_board", 1, 1.0f)
                .addItemInput("milite:stainless_steel_plate", 4, 1.0f)
                .addFluidInput("milite:polyethylene", 750, 1.0f)
                .addItemOutput("milite:digital_circuit_board", 1, 1.0f)
                .offerTo(consumer, "electric_age/circuit/assembler/digital_circuit_board");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 32, 1200)
                .level("bronze")
                .addItemInput("milite:annealed_copper_wire", 8, 1.0f)
                .addItemInput("milite:cadmium_battery", 2, 1.0f)
                .addItemInput("milite:digital_circuit_board", 1, 1.0f)
                .addItemInput("#c:plates/platinum", 4, 1.0f)
                .addFluidInput("milite:polyvinyl_chloride", 1500, 1.0f)
                .addItemOutput("milite:processing_unit_board", 1, 1.0f)
                .offerTo(consumer, "electric_age/circuit/assembler/processing_unit_board");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 64, 2000)
                .level("bronze")
                .addItemInput("milite:superconductor_wire", 12, 1.0f)
                .addItemInput("milite:plutonium_battery", 2, 1.0f)
                .addItemInput("milite:processing_unit_board", 1, 1.0f)
                .addItemInput("#c:plates/iridium", 6, 1.0f)
                .addFluidInput("milite:helium_3", 50, 1.0f)
                .addItemOutput("milite:quantum_circuit_board", 1, 1.0f)
                .offerTo(consumer, "electric_age/circuit/assembler/quantum_circuit_board");

        shaped(consumer, "electric_age/circuit/craft/digital_circuit_asbl",
                new ShapedRecipeJson("milite:digital_circuit", 1, "cOc", "NbN", "cAc")
                        .addInput('O', "milite:or_gate")
                        .addInput('c', "milite:electronic_circuit")
                        .addInput('N', "milite:not_gate")
                        .addInput('A', "milite:and_gate")
                        .addInput('b', "milite:digital_circuit_board"));

        shaped(consumer, "electric_age/circuit/craft/electronic_circuit_asbl",
                new ShapedRecipeJson("milite:electronic_circuit", 1, "cdc", "tbt", "cdc")
                        .addInput('d', "milite:diode")
                        .addInput('c', "milite:analog_circuit")
                        .addInput('t', "milite:transistor")
                        .addInput('b', "milite:electronic_circuit_board"));

        shaped(consumer, "electric_age/circuit/craft/electronic_circuit_board_asbl",
                new ShapedRecipeJson("milite:electronic_circuit_board", 1, "eAe", "AbA", "eBe")
                        .addInput('A', "#c:plates/aluminum")
                        .addInput('e', "milite:electrum_wire")
                        .addInput('b', "milite:analog_circuit_board")
                        .addInput('B', "milite:redstone_battery"));

        shaped(consumer, "electric_age/circuit/craft/lv_circuit_asbl",
                new ShapedRecipeJson("milite:analog_circuit", 1, "RLR", "CBC", "www")
                        .addInput('L', "milite:inductor")
                        .addInput('w', "milite:copper_wire")
                        .addInput('R', "milite:resistor")
                        .addInput('C', "milite:capacitor")
                        .addInput('B', "milite:analog_circuit_board"));

        shaped(consumer, "electric_age/circuit/craft/lv_circuit_board_asbl",
                new ShapedRecipeJson("milite:analog_circuit_board", 1, "r", "c", "r")
                        .addInput('r', "milite:rubber_sheet")
                        .addInput('c', "#c:plates/copper"));

        shaped(consumer, "electric_age/circuit/craft/processing_unit_asbl",
                new ShapedRecipeJson("milite:processing_unit", 1, "DUD", "MbM", "DAD")
                        .addInput('D', "milite:digital_circuit")
                        .addInput('M', "milite:random_access_memory")
                        .addInput('U', "milite:memory_management_unit")
                        .addInput('A', "milite:arithmetic_logic_unit")
                        .addInput('b', "milite:processing_unit_board"));

        shaped(consumer, "electric_age/circuit/craft/quantum_circuit_asbl",
                new ShapedRecipeJson("milite:quantum_circuit", 1, "PCP", "QbQ", "PCP")
                        .addInput('P', "milite:processing_unit")
                        .addInput('C', "milite:cooling_cell")
                        .addInput('Q', "milite:qubit")
                        .addInput('b', "milite:quantum_circuit_board"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput("milite:bronze_plate", 3, 1.0f)
                .addItemInput("milite:stainless_steel_rotor", 3, 1.0f)
                .addItemInput("milite:advanced_motor", 1, 1.0f)
                .addFluidInput("milite:soldering_alloy", 200, 1.0f)
                .addItemOutput("milite:advanced_pump", 1, 1.0f)
                .offerTo(consumer, "electric_age/component/assembler/advanced_pump");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput("#c:plates/gold", 2, 1.0f)
                .addItemInput("milite:copper_wire", 2, 1.0f)
                .addItemInput("milite:rubber_sheet", 1, 1.0f)
                .addItemOutput("milite:capacitor", 4, 1.0f)
                .offerTo(consumer, "electric_age/component/assembler/capacitor");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 16, 400)
                .level("bronze")
                .addItemInput("milite:bronze_plate", 3, 1.0f)
                .addItemInput("milite:small_heat_exchanger", 2, 1.0f)
                .addItemInput("#c:plates/tin", 6, 1.0f)
                .addFluidInput("milite:cryofluid", 1000, 1.0f)
                .addItemOutput("milite:cooling_cell", 1, 1.0f)
                .offerTo(consumer, "electric_age/component/assembler/cooling_cell");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput("milite:copper_wire", 8, 1.0f)
                .addItemInput("#c:rods/steel", 1, 1.0f)
                .addItemOutput("milite:inductor", 4, 1.0f)
                .offerTo(consumer, "electric_age/component/assembler/inductor");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput("milite:bronze_plate", 3, 1.0f)
                .addItemInput("milite:tin_rotor", 3, 1.0f)
                .addItemInput("milite:motor", 1, 1.0f)
                .addFluidInput("milite:soldering_alloy", 100, 1.0f)
                .addItemOutput("milite:pump", 1, 1.0f)
                .offerTo(consumer, "electric_age/component/assembler/pump");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 20, 400)
                .level("bronze")
                .addItemInput("milite:arithmetic_logic_unit", 2, 1.0f)
                .addItemInput("#c:plates/carbon", 2, 1.0f)
                .addItemInput("#c:glass_panes", 2, 1.0f)
                .addItemInput("milite:superconductor_wire", 6, 1.0f)
                .addFluidInput("milite:cryofluid", 250, 1.0f)
                .addFluidInput("milite:tritium", 50, 1.0f)
                .addItemOutput("milite:qubit", 1, 1.0f)
                .offerTo(consumer, "electric_age/component/assembler/qubit");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput("milite:copper_fine_wire", 2, 1.0f)
                .addItemInput("#c:dusts/coal", 1, 1.0f)
                .addItemOutput("milite:resistor", 12, 1.0f)
                .offerTo(consumer, "electric_age/component/assembler/resistor");

        new MachineRecipeBuilder(MIMachineRecipeTypes.BLAST_FURNACE, 64, 1500)
                .level("stainless_steel")
                .addItemInput("#c:dusts/silicon", 32, 1.0f)
                .addItemInput("#c:tiny_dusts/iridium", 1, 1.0f)
                .addFluidInput("milite:argon", 1250, 1.0f)
                .addItemOutput("milite:monocrystalline_silicon", 1, 1.0f)
                .offerTo(consumer, "electric_age/component/blast_furnace/monocrystalline_silicon");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 8, 400)
                .level("bronze")
                .addItemInput("#c:plates/silicon", 1, 1.0f)
                .addItemInput("#c:tiny_dusts/antimony", 1, 1.0f)
                .addItemOutput("milite:silicon_n_doped_plate", 1, 1.0f)
                .offerTo(consumer, "electric_age/component/chemical_reactor/n_doped_silicon");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 8, 400)
                .level("bronze")
                .addItemInput("#c:plates/silicon", 1, 1.0f)
                .addItemInput("#c:tiny_dusts/aluminum", 1, 1.0f)
                .addItemOutput("milite:silicon_p_doped_plate", 1, 1.0f)
                .offerTo(consumer, "electric_age/component/chemical_reactor/p_doped_silicon");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 12, 400)
                .level("bronze")
                .addItemInput("milite:silicon_wafer", 1, 1.0f)
                .addItemInput("#c:dusts/antimony", 1, 1.0f)
                .addItemInput("#c:dusts/aluminum", 1, 1.0f)
                .addFluidInput("milite:argon", 250, 1.0f)
                .addFluidInput("milite:styrene_butadiene_rubber", 500, 1.0f)
                .addItemOutput("milite:random_access_memory", 2, 1.0f)
                .offerTo(consumer, "electric_age/component/chemical_reactor/random_acces_memory");

        shaped(consumer, "electric_age/component/craft/advanced_motor_asbl",
                new ShapedRecipeJson("milite:advanced_motor", 1, "cwr", "wmw", "rwc")
                        .addInput('m', "milite:stainless_steel_rod_magnetic")
                        .addInput('r', "#c:rods/stainless_steel")
                        .addInput('c', "milite:aluminum_wire")
                        .addInput('w', "milite:annealed_copper_wire"));

        shaped(consumer, "electric_age/component/craft/advanced_pump",
                new ShapedRecipeJson("milite:advanced_pump", 1, "brb", "rmr", "brb")
                        .addInput('m', "milite:advanced_motor")
                        .addInput('r', "milite:stainless_steel_rotor")
                        .addInput('b', "milite:stainless_steel_bolt"));

        shaped(consumer, "electric_age/component/craft/air_intake_asbl",
                new ShapedRecipeJson("milite:air_intake", 1, "ara", "rRr", "ara")
                        .addInput('r', "#c:plates/stainless_steel")
                        .addInput('R', "milite:stainless_steel_rotor")
                        .addInput('a', "#c:plates/aluminum"));

        shaped(consumer, "electric_age/component/craft/and_gate_asbl",
                new ShapedRecipeJson("milite:and_gate", 1, "wr ", "wAw", "wr ")
                        .addInput('r', "milite:resistor")
                        .addInput('A', "milite:op_amp")
                        .addInput('w', "milite:aluminum_wire"));

        shaped(consumer, "electric_age/component/craft/arithmetic_logic_unit_asbl",
                new ShapedRecipeJson("milite:arithmetic_logic_unit", 1, "NaN", "oPo", "NaN")
                        .addInput('P', "#c:plates/diamond")
                        .addInput('a', "milite:and_gate")
                        .addInput('o', "milite:or_gate")
                        .addInput('N', "milite:not_gate"));

        shaped(consumer, "electric_age/component/craft/capacitor",
                new ShapedRecipeJson("milite:capacitor", 1, " P ", "wSw", " P ")
                        .addInput('P', "#c:plates/gold")
                        .addInput('w', "milite:copper_wire")
                        .addInput('S', "milite:rubber_sheet"));

        shaped(consumer, "electric_age/component/craft/conveyor_asbl",
                new ShapedRecipeJson("milite:conveyor", 1, "rrr", "mcm", "rrr")
                        .addInput('m', "milite:motor")
                        .addInput('r', "milite:rubber_sheet")
                        .addInput('c', "milite:tin_wire"));

        shaped(consumer, "electric_age/component/craft/diode_asbl",
                new ShapedRecipeJson("milite:diode", 1, "ePg", "eSg", "ePg")
                        .addInput('S', "#c:plates/silicon")
                        .addInput('g', "#c:glass_blocks")
                        .addInput('e', "milite:electrum_fine_wire")
                        .addInput('P', "#c:plates/steel"));

        shaped(consumer, "electric_age/component/craft/diode_doped_asbl",
                new ShapedRecipeJson("milite:diode", 2, "ePg", "eSg", "ePg")
                        .addInput('S', "milite:silicon_n_doped_plate")
                        .addInput('g', "#c:glass_blocks")
                        .addInput('e', "milite:electrum_fine_wire")
                        .addInput('P', "#c:plates/steel"));

        shaped(consumer, "electric_age/component/craft/inductor",
                new ShapedRecipeJson("milite:inductor", 1, "www", "wrw", "www")
                        .addInput('r', "#c:rods/steel")
                        .addInput('w', "milite:copper_wire"));

        shaped(consumer, "electric_age/component/craft/invar_rotary_blade_asbl",
                new ShapedRecipeJson("milite:invar_rotary_blade", 1, " d ", "dGd", " d ")
                        .addInput('d', "#c:dusts/diamond")
                        .addInput('G', "#c:gears/invar"));

        shaped(consumer, "electric_age/component/craft/large_advanced_motor_asbl",
                new ShapedRecipeJson("milite:large_advanced_motor", 1, "mmm", "Crr", "mmm")
                        .addInput('C', "milite:processing_unit")
                        .addInput('r', "#c:rods/titanium")
                        .addInput('m', "milite:advanced_motor"));

        shaped(consumer, "electric_age/component/craft/large_advanced_pump_asbl",
                new ShapedRecipeJson("milite:large_advanced_pump", 1, "ppp", "rMr", "CrC")
                        .addInput('C', "milite:processing_unit")
                        .addInput('r', "milite:titanium_rotor")
                        .addInput('M', "milite:large_advanced_motor")
                        .addInput('p', "milite:advanced_pump"));

        shaped(consumer, "electric_age/component/craft/large_heat_exchanger_asbl",
                new ShapedRecipeJson("milite:large_heat_exchanger", 1, "ghg", "RLR", "ghg")
                        .addInput('h', "milite:small_heat_exchanger")
                        .addInput('L', "milite:large_advanced_pump")
                        .addInput('R', "milite:aluminum_rotor")
                        .addInput('g', "#c:gears/invar"));

        shaped(consumer, "electric_age/component/craft/large_motor_asbl",
                new ShapedRecipeJson("milite:large_motor", 1, "mmm", "Crr", "mmm")
                        .addInput('C', "milite:electronic_circuit")
                        .addInput('r', "#c:rods/aluminum")
                        .addInput('m', "milite:motor"));

        shaped(consumer, "electric_age/component/craft/large_pump_asbl",
                new ShapedRecipeJson("milite:large_pump", 1, "ppp", "rMr", "CrC")
                        .addInput('C', "milite:electronic_circuit")
                        .addInput('r', "milite:aluminum_rotor")
                        .addInput('M', "milite:large_motor")
                        .addInput('p', "milite:pump"));

        shaped(consumer, "electric_age/component/craft/memory_management_unit_asbl",
                new ShapedRecipeJson("milite:memory_management_unit", 1, "WWW", "wPw", "WWW")
                        .addInput('P', "#c:plates/emerald")
                        .addInput('w', "milite:silicon_wafer")
                        .addInput('W', "milite:platinum_fine_wire"));

        shaped(consumer, "electric_age/component/craft/motor_asbl",
                new ShapedRecipeJson("milite:motor", 1, "cwr", "wmw", "rwc")
                        .addInput('m', "milite:steel_rod_magnetic")
                        .addInput('r', "#c:rods/steel")
                        .addInput('c', "milite:tin_wire")
                        .addInput('w', "milite:copper_wire"));

        shaped(consumer, "electric_age/component/craft/not_gate_asbl",
                new ShapedRecipeJson("milite:not_gate", 1, "   ", "wAr", "   ")
                        .addInput('r', "milite:resistor")
                        .addInput('A', "milite:op_amp")
                        .addInput('w', "milite:aluminum_wire"));

        shaped(consumer, "electric_age/component/craft/op_amp_asbl",
                new ShapedRecipeJson("milite:op_amp", 1, "rTT", "rdr", "rTT")
                        .addInput('r', "milite:resistor")
                        .addInput('T', "milite:transistor")
                        .addInput('d', "milite:diode"));

        shaped(consumer, "electric_age/component/craft/or_gate_asbl",
                new ShapedRecipeJson("milite:or_gate", 1, " A ", "wdw", " A ")
                        .addInput('A', "milite:op_amp")
                        .addInput('w', "milite:aluminum_wire")
                        .addInput('d', "milite:diode"));

        shaped(consumer, "electric_age/component/craft/overdrive_module_asbl",
                new ShapedRecipeJson("milite:overdrive_module", 1, "wdw", "tbt", "wdw")
                        .addInput('d', "milite:diode")
                        .addInput('t', "milite:transistor")
                        .addInput('w', "milite:electrum_fine_wire")
                        .addInput('b', "milite:electronic_circuit_board"));

        shaped(consumer, "electric_age/component/craft/piston_asbl",
                new ShapedRecipeJson("milite:piston", 1, "ppp", "crr", "cmg")
                        .addInput('m', "milite:motor")
                        .addInput('r', "#c:rods/steel")
                        .addInput('c', "milite:tin_wire")
                        .addInput('p', "#c:plates/steel")
                        .addInput('g', "#c:gears/steel"));

        shaped(consumer, "electric_age/component/craft/pump",
                new ShapedRecipeJson("milite:pump", 1, "brb", "rmr", "brb")
                        .addInput('m', "milite:motor")
                        .addInput('r', "milite:tin_rotor")
                        .addInput('b', "milite:tin_bolt"));

        shaped(consumer, "electric_age/component/craft/redstone_control_module_asbl",
                new ShapedRecipeJson("milite:redstone_control_module", 1, "dtd", "rcr", "ibi")
                        .addInput('d', "minecraft:redstone")
                        .addInput('t', "minecraft:redstone_torch")
                        .addInput('c', "milite:capacitor")
                        .addInput('r', "milite:resistor")
                        .addInput('b', "milite:analog_circuit_board")
                        .addInput('i', "milite:inductor"));

        shaped(consumer, "electric_age/component/craft/resistor",
                new ShapedRecipeJson("milite:resistor", 3, " P ", "FCF", " P ")
                        .addInput('P', "minecraft:paper")
                        .addInput('F', "milite:copper_fine_wire")
                        .addInput('C', "#c:dusts/coal"));

        shaped(consumer, "electric_age/component/craft/robot_arm_asbl",
                new ShapedRecipeJson("milite:robot_arm", 1, "ccc", "mrm", "pCr")
                        .addInput('m', "milite:motor")
                        .addInput('r', "#c:rods/steel")
                        .addInput('c', "milite:tin_wire")
                        .addInput('p', "milite:piston")
                        .addInput('C', "milite:analog_circuit"));

        shaped(consumer, "electric_age/component/craft/small_heat_exchanger_asbl",
                new ShapedRecipeJson("milite:small_heat_exchanger", 1, "pPp", "PLP", "pPp")
                        .addInput('P', "#c:plates/invar")
                        .addInput('L', "milite:stainless_steel_large_plate")
                        .addInput('p', "milite:advanced_pump"));

        shaped(consumer, "electric_age/component/craft/steel_rod_magnetic",
                new ShapedRecipeJson("milite:steel_rod_magnetic", 1, " RR", "RrR", "RR ")
                        .addInput('R', "minecraft:redstone")
                        .addInput('r', "#c:rods/steel"));

        shaped(consumer, "electric_age/component/craft/transistor_asbl",
                new ShapedRecipeJson("milite:transistor", 2, " e ", "PPP", "SSS")
                        .addInput('S', "#c:plates/silicon")
                        .addInput('e', "milite:electrum_fine_wire")
                        .addInput('P', "#c:plates/steel"));

        shaped(consumer, "electric_age/component/craft/transistor_doped_asbl",
                new ShapedRecipeJson("milite:transistor", 4, " e ", "PPP", "SsS")
                        .addInput('S', "milite:silicon_p_doped_plate")
                        .addInput('s', "milite:silicon_n_doped_plate")
                        .addInput('e', "milite:electrum_fine_wire")
                        .addInput('P', "#c:plates/steel"));

        shaped(consumer, "electric_age/component/craft/ultradense_metal_ball_asbl",
                new ShapedRecipeJson("milite:ultradense_metal_ball", 1, "TlT", "lIl", "TlT")
                        .addInput('T', "milite:tungsten_large_plate")
                        .addInput('l', "#c:plates/lead")
                        .addInput('I', "#c:plates/iridium"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.CUTTING_MACHINE, 16, 600)
                .level("bronze")
                .addItemInput("milite:monocrystalline_silicon", 1, 1.0f)
                .addItemOutput("milite:silicon_wafer", 32, 1.0f)
                .offerTo(consumer, "electric_age/component/cutting_machine/silicon_waffer");

        new MachineRecipeBuilder(MIMachineRecipeTypes.IMPLOSION_COMPRESSOR, 1, 10)
                .level("bronze")
                .addItemInput("milite:ultradense_metal_ball", 1, 1.0f)
                .addItemInput("milite:nuke", 64, 1.0f)
                .addItemOutput("milite:singularity", 1, 1.0f)
                .offerTo(consumer, "electric_age/component/implosion_compressor/singularity");

        new MachineRecipeBuilder(MIMachineRecipeTypes.POLARIZER, 16, 400)
                .level("bronze")
                .addItemInput("#c:rods/stainless_steel", 1, 1.0f)
                .addItemInput("milite:neodymium_dust", 1, 1.0f)
                .addItemOutput("milite:stainless_steel_rod_magnetic", 1, 1.0f)
                .offerTo(consumer, "electric_age/component/polarizer/stainless_steel_rod_magnetic");

        shaped(consumer, "electric_age/hull/aluminum_alloy_machine_hull_asbl",
                new ShapedRecipeJson("milite:aluminum_alloy_machine_hull", 1, " c ", "BCB", "ppp")
                        .addInput('B', "milite:silicon_battery")
                        .addInput('c', "milite:electronic_circuit")
                        .addInput('C', "milite:aluminum_alloy_machine_casing")
                        .addInput('p', "milite:electrum_wire"));

        shaped(consumer, "electric_age/hull/electric_machine_hull_asbl",
                new ShapedRecipeJson("milite:electric_machine_hull", 1, " A ", "BCB", "ccc")
                        .addInput('A', "milite:analog_circuit")
                        .addInput('c', "milite:tin_wire")
                        .addInput('B', "milite:redstone_battery")
                        .addInput('C', "milite:steel_machine_casing"));

        shaped(consumer, "electric_age/hull/iridium_machine_hull_asbl",
                new ShapedRecipeJson("milite:iridium_machine_hull", 1, " c ", "BCB", "ppp")
                        .addInput('B', "milite:plutonium_battery")
                        .addInput('c', "milite:quantum_circuit")
                        .addInput('C', "milite:iridium_machine_casing")
                        .addInput('p', "milite:superconductor_wire"));

        shaped(consumer, "electric_age/hull/stainless_steel_machine_hull_asbl",
                new ShapedRecipeJson("milite:stainless_steel_machine_hull", 1, " c ", "BCB", "ppp")
                        .addInput('B', "milite:sodium_battery")
                        .addInput('c', "milite:digital_circuit")
                        .addInput('C', "milite:stainless_steel_machine_casing")
                        .addInput('p', "milite:aluminum_wire"));

        shaped(consumer, "electric_age/hull/titanium_machine_hull_asbl",
                new ShapedRecipeJson("milite:titanium_machine_hull", 1, " c ", "BCB", "ppp")
                        .addInput('B', "milite:cadmium_battery")
                        .addInput('c', "milite:processing_unit")
                        .addInput('C', "milite:titanium_machine_casing")
                        .addInput('p', "milite:annealed_copper_wire"));

        shaped(consumer, "electric_age/machine/advanced_large_steam_boiler_asbl",
                new ShapedRecipeJson("milite:advanced_large_steam_boiler", 1, "PcP", "LBL", "PcP")
                        .addInput('P', "milite:pump")
                        .addInput('c', "milite:electronic_circuit")
                        .addInput('B', "milite:large_steam_boiler")
                        .addInput('L', "milite:large_pump"));

        shaped(consumer, "electric_age/machine/aluminum_alloy_diesel_generator_asbl",
                new ShapedRecipeJson("milite:aluminum_alloy_diesel_generator", 1, "PCP", "GHG", "ccc")
                        .addInput('H', "milite:aluminum_alloy_machine_hull")
                        .addInput('P', "milite:large_pump")
                        .addInput('G', "#c:gears/aluminum")
                        .addInput('c', "milite:electrum_wire")
                        .addInput('C', "milite:electronic_circuit"));

        shaped(consumer, "electric_age/machine/aluminum_alloy_steam_turbine_asbl",
                new ShapedRecipeJson("milite:aluminum_alloy_steam_turbine", 1, "mcm", "rHr", "mem")
                        .addInput('H', "milite:aluminum_alloy_machine_hull")
                        .addInput('c', "milite:electronic_circuit")
                        .addInput('r', "milite:aluminum_rotor")
                        .addInput('m', "milite:large_motor")
                        .addInput('e', "milite:electrum_wire"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 32, 8000)
                .level("bronze")
                .addItemInput("milite:iridium_machine_hull", 1, 1.0f)
                .addItemInput("milite:quantum_upgrade", 2, 1.0f)
                .addItemInput("milite:quantum_circuit", 4, 1.0f)
                .addFluidInput("milite:uu_matter", 500, 1.0f)
                .addItemOutput("milite:replicator", 1, 1.0f)
                .offerTo(consumer, "electric_age/machine/assembler/replicator");

        shaped(consumer, "electric_age/machine/configurable_chest_asbl",
                new ShapedRecipeJson("milite:configurable_chest", 4, "chc", "mCm", "chc")
                        .addInput('C', "milite:steel_machine_casing")
                        .addInput('m', "milite:motor")
                        .addInput('c', "milite:analog_circuit")
                        .addInput('h', "minecraft:hopper"));

        shaped(consumer, "electric_age/machine/configurable_tank_asbl",
                new ShapedRecipeJson("milite:configurable_tank", 4, "ctc", "pCp", "ctc")
                        .addInput('C', "milite:steel_machine_casing")
                        .addInput('p', "milite:pump")
                        .addInput('c', "milite:analog_circuit")
                        .addInput('t', "milite:aluminum_tank"));

        shaped(consumer, "electric_age/machine/electric_blast_furnace_asbl",
                new ShapedRecipeJson("milite:electric_blast_furnace", 1, "CCC", "CHC", "CFC")
                        .addInput('C', "milite:cupronickel_wire_magnetic")
                        .addInput('H', "milite:electric_machine_hull")
                        .addInput('F', "minecraft:blast_furnace"));

        shaped(consumer, "electric_age/machine/electric_diesel_generator_asbl",
                new ShapedRecipeJson("milite:electric_diesel_generator", 1, "PCP", "GHG", "ccc")
                        .addInput('H', "milite:electric_machine_hull")
                        .addInput('P', "milite:pump")
                        .addInput('G', "#c:gears/tin")
                        .addInput('c', "milite:tin_wire")
                        .addInput('C', "milite:analog_circuit"));

        shaped(consumer, "electric_age/machine/electric_quarry_asbl",
                new ShapedRecipeJson("milite:electric_quarry", 1, "LAL", "GCG", "LPL")
                        .addInput('L', "milite:large_motor")
                        .addInput('A', "milite:robot_arm")
                        .addInput('G', "milite:electronic_circuit")
                        .addInput('P', "milite:steam_quarry")
                        .addInput('C', "milite:aluminum_alloy_machine_hull"));

        shaped(consumer, "electric_age/machine/electric_steam_turbine_asbl",
                new ShapedRecipeJson("milite:electric_steam_turbine", 1, "mCm", "rHr", "mcm")
                        .addInput('C', "milite:analog_circuit")
                        .addInput('m', "milite:motor")
                        .addInput('H', "milite:electric_machine_hull")
                        .addInput('r', "milite:tin_rotor")
                        .addInput('c', "milite:tin_wire"));

        shaped(consumer, "electric_age/machine/fusion_reactor_asbl",
                new ShapedRecipeJson("milite:fusion_reactor", 1, "CCC", "QHQ", "CCC")
                        .addInput('C', "milite:fusion_chamber")
                        .addInput('H', "milite:iridium_machine_hull")
                        .addInput('Q', "milite:quantum_circuit"));

        shaped(consumer, "electric_age/machine/heat_exchanger_asbl",
                new ShapedRecipeJson("milite:heat_exchanger", 1, "FpH", "PhP", "FpH")
                        .addInput('p', "milite:small_heat_exchanger")
                        .addInput('h', "milite:stainless_steel_machine_hull")
                        .addInput('P', "milite:stainless_steel_machine_casing_pipe")
                        .addInput('F', "milite:frostproof_machine_casing")
                        .addInput('H', "milite:heatproof_machine_casing"));

        shaped(consumer, "electric_age/machine/high_pressure_advanced_large_steam_boiler_asbl",
                new ShapedRecipeJson("milite:high_pressure_advanced_large_steam_boiler", 1, "LcL", "fBf", "LcL")
                        .addInput('L', "milite:large_pump")
                        .addInput('c', "milite:processing_unit")
                        .addInput('B', "milite:high_pressure_large_steam_boiler")
                        .addInput('f', "milite:large_advanced_pump"));

        shaped(consumer, "electric_age/machine/high_pressure_large_steam_boiler_asbl",
                new ShapedRecipeJson("milite:high_pressure_large_steam_boiler", 1, "fcf", "PBP", "fcf")
                        .addInput('P', "milite:clean_stainless_steel_machine_casing")
                        .addInput('c', "milite:digital_circuit")
                        .addInput('B', "milite:advanced_large_steam_boiler")
                        .addInput('f', "milite:advanced_pump"));

        shaped(consumer, "electric_age/machine/implosion_compressor_asbl",
                new ShapedRecipeJson("milite:implosion_compressor", 1, "BmB", "THT", "BmB")
                        .addInput('m', "milite:advanced_motor")
                        .addInput('T', "milite:titanium_large_plate")
                        .addInput('H', "milite:stainless_steel_machine_hull")
                        .addInput('B', "milite:blastproof_alloy_large_plate"));

        shaped(consumer, "electric_age/machine/large_diesel_generator_asbl",
                new ShapedRecipeJson("milite:large_diesel_generator", 1, "pcp", "GHG", "mgm")
                        .addInput('g', "milite:stainless_steel_diesel_generator")
                        .addInput('p', "milite:advanced_pump")
                        .addInput('c', "milite:processing_unit")
                        .addInput('m', "milite:advanced_motor")
                        .addInput('H', "milite:titanium_machine_hull")
                        .addInput('G', "milite:titanium_gear"));

        shaped(consumer, "electric_age/machine/large_steam_boiler_asbl",
                new ShapedRecipeJson("milite:large_steam_boiler", 1, "PcP", "fBf", "PcP")
                        .addInput('P', "milite:bronze_plated_bricks")
                        .addInput('c', "milite:analog_circuit")
                        .addInput('B', "milite:bronze_boiler")
                        .addInput('f', "milite:bronze_plate"));

        shaped(consumer, "electric_age/machine/large_steam_turbine_asbl",
                new ShapedRecipeJson("milite:large_steam_turbine", 1, "pcp", "RHR", "mtm")
                        .addInput('t', "milite:stainless_steel_steam_turbine")
                        .addInput('p', "milite:advanced_pump")
                        .addInput('c', "milite:processing_unit")
                        .addInput('m', "milite:advanced_motor")
                        .addInput('H', "milite:stainless_steel_machine_hull")
                        .addInput('R', "milite:stainless_steel_rotor"));

        shaped(consumer, "electric_age/machine/large_tank_asbl",
                new ShapedRecipeJson("milite:large_tank", 1, "tpt", "RSR", "tpt")
                        .addInput('t', "milite:steel_tank")
                        .addInput('p', "milite:pump")
                        .addInput('S', "milite:steel_machine_casing")
                        .addInput('R', "milite:tin_rotor"));

        shaped(consumer, "electric_age/machine/large_tank_hatch_asbl",
                new ShapedRecipeJson("milite:large_tank_hatch", 1, "t", "p", "S")
                        .addInput('t', "milite:steel_tank")
                        .addInput('p', "milite:pump")
                        .addInput('S', "milite:steel_machine_casing"));

        shaped(consumer, "electric_age/machine/nuclear_reactor_asbl",
                new ShapedRecipeJson("milite:nuclear_reactor", 1, "AcA", "RCR", "AcA")
                        .addInput('A', "milite:nuclear_alloy_large_plate")
                        .addInput('C', "milite:titanium_machine_hull")
                        .addInput('c', "milite:processing_unit")
                        .addInput('R', "milite:cadmium_control_rod"));

        shaped(consumer, "electric_age/machine/plasma_turbine_asbl",
                new ShapedRecipeJson("milite:plasma_turbine", 1, "tpt", "RHR", "tpt")
                        .addInput('t', "milite:superconductor_coil")
                        .addInput('p', "milite:quantum_circuit")
                        .addInput('H', "milite:iridium_machine_hull")
                        .addInput('R', "milite:titanium_rotor"));

        shaped(consumer, "electric_age/machine/pressurizer_asbl",
                new ShapedRecipeJson("milite:pressurizer", 1, "pcp", "PHP", "pCp")
                        .addInput('p', "milite:advanced_pump")
                        .addInput('H', "milite:stainless_steel_machine_hull")
                        .addInput('c', "milite:digital_circuit")
                        .addInput('P', "milite:titanium_machine_casing_pipe")
                        .addInput('C', "milite:aluminum_wire"));

        shaped(consumer, "electric_age/machine/stainless_steel_diesel_generator_asbl",
                new ShapedRecipeJson("milite:stainless_steel_diesel_generator", 1, "PCP", "GHG", "ccc")
                        .addInput('H', "milite:stainless_steel_machine_hull")
                        .addInput('P', "milite:advanced_pump")
                        .addInput('G', "#c:gears/stainless_steel")
                        .addInput('c', "milite:aluminum_wire")
                        .addInput('C', "milite:digital_circuit"));

        shaped(consumer, "electric_age/machine/stainless_steel_steam_turbine_asbl",
                new ShapedRecipeJson("milite:stainless_steel_steam_turbine", 1, "pCp", "rHr", "mcm")
                        .addInput('C', "milite:digital_circuit")
                        .addInput('m', "milite:advanced_motor")
                        .addInput('H', "milite:stainless_steel_machine_hull")
                        .addInput('p', "milite:bronze_plate")
                        .addInput('r', "milite:stainless_steel_rotor")
                        .addInput('c', "milite:aluminum_wire"));

        shaped(consumer, "electric_age/machine/vacuum_freezer_asbl",
                new ShapedRecipeJson("milite:vacuum_freezer", 1, "PPP", "CHC", "pCp")
                        .addInput('C', "milite:electronic_circuit")
                        .addInput('H', "milite:aluminum_alloy_machine_hull")
                        .addInput('P', "milite:large_pump")
                        .addInput('p', "milite:aluminum_large_plate"));

        shaped(consumer, "electric_age/storage_unit/aluminum_alloy_storage_unit_asbl",
                new ShapedRecipeJson("milite:aluminum_alloy_storage_unit", 1, "bbb", "LHL", "bbb")
                        .addInput('b', "milite:silicon_battery")
                        .addInput('H', "milite:aluminum_alloy_machine_hull")
                        .addInput('L', "#c:plates/battery_alloy"));

        shaped(consumer, "electric_age/storage_unit/electric_storage_unit_asbl",
                new ShapedRecipeJson("milite:electric_storage_unit", 1, "bbb", "LHL", "bbb")
                        .addInput('b', "milite:redstone_battery")
                        .addInput('H', "milite:electric_machine_hull")
                        .addInput('L', "#c:plates/battery_alloy"));

        shaped(consumer, "electric_age/storage_unit/iridium_storage_unit_asbl",
                new ShapedRecipeJson("milite:iridium_storage_unit", 1, "bbb", "LHL", "bbb")
                        .addInput('b', "milite:plutonium_battery")
                        .addInput('H', "milite:iridium_machine_hull")
                        .addInput('L', "#c:plates/battery_alloy"));

        shaped(consumer, "electric_age/storage_unit/stainless_steel_storage_unit_asbl",
                new ShapedRecipeJson("milite:stainless_steel_storage_unit", 1, "bbb", "LHL", "bbb")
                        .addInput('b', "milite:sodium_battery")
                        .addInput('H', "milite:stainless_steel_machine_hull")
                        .addInput('L', "#c:plates/battery_alloy"));

        shaped(consumer, "electric_age/storage_unit/titanium_storage_unit_asbl",
                new ShapedRecipeJson("milite:titanium_storage_unit", 1, "bbb", "LHL", "bbb")
                        .addInput('b', "milite:cadmium_battery")
                        .addInput('H', "milite:titanium_machine_hull")
                        .addInput('L', "#c:plates/battery_alloy"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 32, 1000)
                .level("bronze")
                .addItemInput("milite:quantum_circuit", 8, 1.0f)
                .addItemInput("milite:singularity", 1, 1.0f)
                .addItemInput("milite:quantum_circuit_board", 1, 1.0f)
                .addFluidInput("milite:uu_matter", 50, 1.0f)
                .addItemOutput("milite:quantum_upgrade", 1, 1.0f)
                .offerTo(consumer, "electric_age/upgrades/quantum");
    }
}
