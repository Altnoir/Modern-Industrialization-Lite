package aztech.milite.datagen.recipe;

import aztech.milite.MIItem;

import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeBuilder;
import aztech.milite.recipe.json.ShapedRecipeJson;
import aztech.milite.recipe.json.ShapelessRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;

public class ElectricAgeRecipesProvider extends FixedRecipesProvider {
    public ElectricAgeRecipesProvider(PackOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        new ShapelessRecipeBuilder("milite:cupronickel_wire_magnetic", 1)
                .requires("milite:cupronickel_wire").requires("minecraft:redstone")
                .offerTo(consumer, "electric_age/component/craft/cupronickel_wire_magnetic");
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
                .addItemInput(MIItem.QUANTUM_PROCESSOR, 2, 1.0f)
                .addItemInput("#c:plates/iridium", 16, 1.0f)
                .addItemInput("milite:large_motor", 8, 1.0f)
                .addItemInput("milite:titanium_barrel", 1, 1.0f)
                .addItemOutput("milite:quantum_barrel", 1, 1.0f)
                .offerTo(consumer, "electric_age/blocks/assembler/quantum_barrel");

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 32, 1000)
                .level("bronze")
                .addItemInput("milite:iridium_machine_hull", 1, 1.0f)
                .addItemInput("milite:singularity", 1, 1.0f)
                .addItemInput(MIItem.QUANTUM_PROCESSOR, 2, 1.0f)
                .addItemInput("#c:plates/iridium", 16, 1.0f)
                .addItemInput("milite:large_pump", 8, 1.0f)
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

        shaped(consumer, "electric_age/casing/nuclear_casing_asbl",
                new ShapedRecipeJson("milite:nuclear_casing", 1, "AAA", "ACA", "AAA")
                        .addInput('A', "#c:plates/nuclear_alloy")
                        .addInput('C', "milite:stainless_steel_machine_hull"));

        shaped(consumer, "electric_age/casing/tungsten_casing_asbl",
                new ShapedRecipeJson("milite:tungsten_casing", 1, "AAA", "ACA", "AAA")
                        .addInput('A', "#c:plates/tungsten")
                        .addInput('C', "milite:titanium_machine_hull"));

        shaped(consumer, "electric_age/casing/nuclear_fluid_hatch_asbl",
                new ShapedRecipeJson("milite:nuclear_fluid_hatch", 1, "AAA", "ACA", "AAA")
                        .addInput('A', "#c:plates/nuclear_alloy")
                        .addInput('C', "milite:titanium_fluid_input_hatch"));

        shaped(consumer, "electric_age/casing/nuclear_item_hatch_asbl",
                new ShapedRecipeJson("milite:nuclear_item_hatch", 1, "AAA", "ACA", "AAA")
                        .addInput('A', "#c:plates/nuclear_alloy")
                        .addInput('C', "milite:titanium_item_input_hatch"));

        shaped(consumer, "electric_age/casing/control_rod_asbl",
                new ShapedRecipeJson("milite:control_rod", 4, "ARA", "RCR", "ARA")
                        .addInput('A', "#c:plates/nuclear_alloy")
                        .addInput('R', "milite:cadmium_control_rod")
                        .addInput('C', "milite:nuclear_casing"));

        shaped(consumer, "electric_age/casing/plasma_handling_iridium_machine_casing_asbl",
                new ShapedRecipeJson("milite:plasma_handling_iridium_machine_casing", 1, "AAA", "ACA", "AAA")
                        .addInput('A', "#c:plates/iridium")
                        .addInput('C', "milite:superconductor_coil"));

        shaped(consumer, "electric_age/casing/solid_titanium_machine_casing_asbl",
                new ShapedRecipeJson("milite:solid_titanium_machine_casing", 1, "AAA", "AGA", "AAA")
                        .addInput('A', "#c:plates/titanium")
                        .addInput('G', "#c:gears/titanium"));

        shaped(consumer, "electric_age/casing/stainless_steel_machine_casing_asbl",
                new ShapedRecipeJson("milite:stainless_steel_machine_casing", 1, "AAA", "AHA", "AAA")
                        .addInput('A', "#c:plates/stainless_steel")
                        .addInput('H', "milite:aluminum_alloy_machine_hull"));

        shaped(consumer, "electric_age/casing/titanium_machine_casing_asbl",
                new ShapedRecipeJson("milite:titanium_machine_casing", 1, "ACA", "CHC", "ACA")
                        .addInput('A', "#c:plates/titanium")
                        .addInput('C', "#c:plates/chromium")
                        .addInput('H', "milite:stainless_steel_machine_hull"));





        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 16, 500)
                .level("bronze")
                .addItemInput("milite:aluminum_wire", 6, 1.0f)
                .addItemInput("milite:silicon_battery", 1, 1.0f)
                .addItemInput("milite:stainless_steel_plate", 4, 1.0f)
                .addFluidInput("milite:polyethylene", 750, 1.0f)
                .addItemOutput("milite:digital_circuit_board", 1, 1.0f)
                .offerTo(consumer, "electric_age/circuit/assembler/digital_circuit_board");

        shaped(consumer, "electric_age/circuit/craft/digital_circuit_asbl",
                new ShapedRecipeJson("milite:digital_circuit", 1, "cAc", "wbw", "cAc")
                        .addInput('A', "milite:op_amp")
                        .addInput('c', "milite:electronic_circuit")
                        .addInput('w', "milite:aluminum_wire")
                        .addInput('b', "milite:digital_circuit_board"));

        shaped(consumer, "electric_age/circuit/craft/electronic_circuit_asbl",
                new ShapedRecipeJson("milite:electronic_circuit", 1, "cdc", "tbt", "cdc")
                        .addInput('d', "milite:diode")
                        .addInput('c', "milite:analog_circuit")
                        .addInput('t', "milite:transistor")
                        .addInput('b', "milite:electronic_circuit_board"));

        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("electric")
                .addItemInput("milite:electrum_wire", 4, 1.0f)
                .addItemInput("milite:redstone_battery", 1, 1.0f)
                .addItemInput("#c:plates/aluminum", 2, 1.0f)
                .addItemOutput("milite:electronic_circuit_board", 1, 1.0f)
                .offerTo(consumer, "assembler_generated/electric_age/circuit/craft/electronic_circuit_board");

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


        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 8, 200)
                .level("bronze")
                .addItemInput("milite:copper_fine_wire", 2, 1.0f)
                .addItemInput("#c:dusts/coal", 1, 1.0f)
                .addItemOutput("milite:resistor", 12, 1.0f)
                .offerTo(consumer, "electric_age/component/assembler/resistor");

        new MachineRecipeBuilder(MIMachineRecipeTypes.BLAST_FURNACE, 32, 1500)
                .level("electric")
                .addItemInput("#c:dusts/silicon", 32, 1.0f)
                .addItemOutput("milite:monocrystalline_silicon", 1, 1.0f)
                .offerTo(consumer, "electric_age/component/blast_furnace/monocrystalline_silicon");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 8, 400)
                .level("aluminum_alloy")
                .addItemInput("#c:plates/silicon", 1, 1.0f)
                .addItemInput("#c:tiny_dusts/antimony", 1, 1.0f)
                .addItemOutput("milite:silicon_n_doped_plate", 1, 1.0f)
                .offerTo(consumer, "electric_age/component/chemical_reactor/n_doped_silicon");

        new MachineRecipeBuilder(MIMachineRecipeTypes.CHEMICAL_REACTOR, 8, 400)
                .level("aluminum_alloy")
                .addItemInput("#c:plates/silicon", 1, 1.0f)
                .addItemInput("#c:tiny_dusts/aluminum", 1, 1.0f)
                .addItemOutput("milite:silicon_p_doped_plate", 1, 1.0f)
                .offerTo(consumer, "electric_age/component/chemical_reactor/p_doped_silicon");


        shaped(consumer, "electric_age/component/craft/air_intake_asbl",
                new ShapedRecipeJson("milite:air_intake", 1, "ara", "rRr", "ara")
                        .addInput('r', "#c:plates/stainless_steel")
                        .addInput('R', "milite:stainless_steel_rotor")
                        .addInput('a', "#c:plates/aluminum"));



        shaped(consumer, "electric_age/component/craft/op_amp_asbl",
                new ShapedRecipeJson("milite:op_amp", 1, "rTT", "rdr", "rTT")
                        .addInput('r', "milite:resistor")
                        .addInput('T', "milite:transistor")
                        .addInput('d', "milite:diode"));

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

        shaped(consumer, "electric_age/component/craft/large_heat_exchanger_asbl",
                new ShapedRecipeJson("milite:large_heat_exchanger", 1, "ghg", "RLR", "ghg")
                        .addInput('h', "milite:small_heat_exchanger")
                        .addInput('L', "milite:large_pump")
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


        shaped(consumer, "electric_age/component/craft/motor_asbl",
                new ShapedRecipeJson("milite:motor", 1, "cwr", "wmw", "rwc")
                        .addInput('m', "milite:steel_rod_magnetic")
                        .addInput('r', "#c:rods/steel")
                        .addInput('c', "milite:tin_wire")
                        .addInput('w', "milite:copper_wire"));




        shaped(consumer, "electric_age/component/craft/overclocker_unit_asbl",
                new ShapedRecipeJson("milite:overclocker_unit", 1, "wdw", "tbt", "wdw")
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
                        .addInput('p', "milite:pump"));

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
                .level("aluminum_alloy")
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
                .level("aluminum_alloy")
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
                        .addInput('c', MIItem.QUANTUM_PROCESSOR.id())
                        .addInput('C', "milite:iridium_machine_casing")
                        .addInput('p', "milite:superconductor_wire"));

        shaped(consumer, "electric_age/hull/stainless_steel_machine_hull_asbl",
                new ShapedRecipeJson("milite:stainless_steel_machine_hull", 1, " c ", "BCB", "ppp")
                        .addInput('B', "milite:sodium_battery")
                        .addInput('c', MIItem.DIGITAL_CIRCUIT.id())
                        .addInput('C', "milite:stainless_steel_machine_casing")
                        .addInput('p', "milite:aluminum_wire"));

        shaped(consumer, "electric_age/hull/titanium_machine_hull_asbl",
                new ShapedRecipeJson("milite:titanium_machine_hull", 1, " c ", "BCB", "ppp")
                        .addInput('B', "milite:cadmium_battery")
                        .addInput('c', MIItem.PROCESSOR.id())
                        .addInput('C', "milite:titanium_machine_casing")
                        .addInput('p', "milite:annealed_copper_wire"));







        new MachineRecipeBuilder(MIMachineRecipeTypes.ASSEMBLER, 32, 8000)
                .level("bronze")
                .addItemInput("milite:iridium_machine_hull", 1, 1.0f)
                .addItemInput("milite:quantum_upgrade", 2, 1.0f)
                .addItemInput(MIItem.QUANTUM_PROCESSOR, 4, 1.0f)
                .addFluidInput("milite:uu_matter", 500, 1.0f)
                .addItemOutput("milite:replicator", 1, 1.0f)
                .offerTo(consumer, "electric_age/machine/assembler/replicator");









































        new ShapelessRecipeBuilder("milite:large_tank_hatch", 1)
                .requires("milite:steel_machine_hull")
                .requires("minecraft:bucket")
                .offerTo(consumer, "electric_age/machine/large_tank_hatch");

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
                .addItemInput(MIItem.QUANTUM_PROCESSOR, 8, 1.0f)
                .addItemInput("milite:singularity", 1, 1.0f)
                .addFluidInput("milite:uu_matter", 50, 1.0f)
                .addItemOutput("milite:quantum_upgrade", 1, 1.0f)
                .offerTo(consumer, "electric_age/upgrades/quantum");
    }
}
