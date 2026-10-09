package aztech.milite.materials.recipe;

import static aztech.milite.materials.part.MIParts.*;
import static aztech.milite.materials.property.MaterialProperty.HARDNESS;
import static aztech.milite.materials.property.MaterialProperty.MAIN_PART;

import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.materials.MaterialBuilder;
import aztech.milite.materials.part.PartKeyProvider;
import aztech.milite.materials.recipe.builder.MIRecipeBuilder;
import aztech.milite.materials.recipe.builder.ShapedRecipeBuilder;
import aztech.milite.materials.recipe.builder.ShapelessRecipeBuilder;
import aztech.milite.materials.recipe.builder.SmeltingRecipeBuilder;
import aztech.milite.MIFluids;
import aztech.milite.MIItem;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

/**
 * Standard conversion recipes for all materials.
 */
public final class StandardRecipes {
    public static void apply(MaterialBuilder.RecipeContext ctx) {
        add3By3Crafting(ctx, TINY_DUST, DUST, true);
        add3By3Crafting(ctx, NUGGET, INGOT, true);
        add3By3Crafting(ctx, ctx.get(MAIN_PART), BLOCK, false); // Not in packer due to conflicts with double ingots.
        add3By3Crafting(ctx, RAW_METAL, RAW_METAL_BLOCK, true);

        new ShapedRecipeBuilder(ctx, BLADE, 4, "blade", "P", "P", "I").addTaggedPart('P', PLATE).addTaggedPart('I', ROD)
                .exportToMachine(MIMachineRecipeTypes.PACKER);

        // A coil used to be built from eight cables. Cables are gone, so it is built from the wire they carried instead,
        // which keeps the same material cost. Without this, a blast furnace could not be built at all.
        new ShapedRecipeBuilder(ctx, COIL, 1, "coil", "xxx", "x x", "xxx").addTaggedPart('x', WIRE).exportToAssembler();

        new ShapelessRecipeBuilder(ctx, ORE, 1, "deepslate_to_ore").addPart(ORE_DEEPSLATE);

        new ShapedRecipeBuilder(ctx, LARGE_PLATE, 1, "large_plate", "xx", "xx").addTaggedPart('x', PLATE)
                .exportToMachine(MIMachineRecipeTypes.PACKER);

        new ShapedRecipeBuilder(ctx, ROTOR, 1, "rotor", "bBb", "BRB", "bBb").addTaggedPart('b', BOLT).addTaggedPart('B', BLADE).addTaggedPart('R',
                RING);

        new ShapedRecipeBuilder(ctx, GEAR, 1, "gear", "PbP", "bRb", "PbP").addTaggedPart('b', BOLT).addTaggedPart('P', PLATE).addTaggedPart('R',
                RING);

        new ShapedRecipeBuilder(ctx, HAMMER, 1, "hammer", "ppp", "psp", " s ").addTaggedPart('p', LARGE_PLATE).addInput('s', Items.STICK);

        new ShapedRecipeBuilder(ctx, RING, 2, "ring", "bRb", "R R", "bRb").addTaggedPart('b', BOLT).addTaggedPart('R', ROD);

        new ShapedRecipeBuilder(ctx, TANK, 1, "tank", "###", "#G#", "###").addTaggedPart('#', PLATE).addInput('G', Tags.Items.GLASS_BLOCKS)
                .exportToAssembler();
        new ShapedRecipeBuilder(ctx, BARREL, 1, "barrel", "###", "#b#", "###")
                .addTaggedPart('#', PLATE)
                .addInput('b', Tags.Items.BARRELS_WOODEN)
                .exportToAssembler();

        // The pattern letter 'c' used to ask for a curved plate. Curved plates are gone, so 'c' and 'p' are both the
        // plain plate now, which leaves the drill head costing the same three plates it did before.
        new ShapedRecipeBuilder(ctx, DRILL_HEAD, 1, "drill_head", "bcp", "GRc", "bGb").addTaggedPart('G', GEAR).addPart('b', BOLT)
                .addTaggedPart('c', PLATE).addTaggedPart('R', ROD).addTaggedPart('p', PLATE);

        addMaceratorRecycling(ctx, DOUBLE_INGOT, 18);
        addMaceratorRecycling(ctx, PLATE, 9);
        addMaceratorRecycling(ctx, NUGGET, 1);
        addMaceratorRecycling(ctx, LARGE_PLATE, 36);
        addMaceratorRecycling(ctx, GEAR, 18);
        addMaceratorRecycling(ctx, RING, 4);
        addMaceratorRecycling(ctx, BOLT, 2);
        addMaceratorRecycling(ctx, ROD, 4);
        addMaceratorRecycling(ctx, ROTOR, 27);
        if (!ctx.get(MAIN_PART).equals(DUST)) {
            addMaceratorRecycling(ctx, ctx.get(MAIN_PART), 9);
        }
        addMaceratorRecycling(ctx, BLADE, 5);
        addMaceratorRecycling(ctx, DRILL_HEAD, 7 * 9 + 4);
        addMaceratorRecycling(ctx, WIRE, 4);

        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.MACERATOR, "ore_to_crushed").addTaggedPartInput(ORE, 1).addPartOutput(CRUSHED_DUST, 3);
        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.MACERATOR, "ore_to_raw").addTaggedPartInput(ORE, 1).addPartOutput(RAW_METAL, 3);

        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.MACERATOR, "crushed_dust", 2, (int) (100 * ctx.get(HARDNESS).timeFactor))
                .addTaggedPartInput(CRUSHED_DUST, 1)
                .addPartOutput(DUST, 1).addPartOutput(DUST, 1, 0.5f);
        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.MACERATOR, "raw_metal", 2, (int) (100 * ctx.get(HARDNESS).timeFactor))
                .addTaggedPartInput(RAW_METAL, 1)
                .addPartOutput(DUST, 1).addPartOutput(DUST, 1, 0.5f);
        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.COMPRESSOR, "main").addTaggedPartInput(ctx.get(MAIN_PART), 1).addPartOutput(PLATE, 1);
        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.COMPRESSOR, "double_ingot").addTaggedPartInput(DOUBLE_INGOT, 1).addPartOutput(PLATE, 2);
        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.COMPRESSOR, "ring").addTaggedPartInput(ROD, 1).addPartOutput(RING, 1);
        addCuttingMachine(ctx, "main", ctx.get(MAIN_PART), ROD, 2);
        addCuttingMachine(ctx, "double_ingot", DOUBLE_INGOT, ROD, 4);
        addCuttingMachine(ctx, "rod", ROD, BOLT, 2);
        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.PACKER, "block").addTaggedPartInput(ctx.get(MAIN_PART), 9)
                .addItemInput(MIItem.PACKER_BLOCK_TEMPLATE, 1, 0.0f).addPartOutput(BLOCK, 1);
        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.PACKER, "double_ingot").addTaggedPartInput(INGOT, 2)
                .addItemInput(MIItem.PACKER_DOUBLE_INGOT_TEMPLATE, 1, 0.0f).addPartOutput(DOUBLE_INGOT, 1);

        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.PACKER, "fuel_rod_double").addPartInput(FUEL_ROD, 2)
                .addItemInput("#c:plates/nuclear_alloy", 1)
                .addPartOutput(FUEL_ROD_DOUBLE, 1);

        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.PACKER, "fuel_rod_quad").addItemInput("#c:plates/nuclear_alloy", 2)
                .addPartInput(FUEL_ROD_DOUBLE, 2)
                .addPartOutput(FUEL_ROD_QUAD, 1);

        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.WIREMILL, "wire").addTaggedPartInput(PLATE, 1).addPartOutput(WIRE, 2);
        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.WIREMILL, "fine_wire").addTaggedPartInput(WIRE, 1).addPartOutput(FINE_WIRE, 4);
        // These use the default energy of two, but the assembler only exists as an electric machine, so they still
        // belong to the electric level and have to say so.
        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.ASSEMBLER, "rotor").level(MachineLevel.ELECTRIC)
                .addTaggedPartInput(BLADE, 4).addTaggedPartInput(RING, 1)
                .addFluidInput(MIFluids.SOLDERING_ALLOY, 100).addPartOutput(ROTOR, 1);
        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.ASSEMBLER, "gear").level(MachineLevel.ELECTRIC)
                .addTaggedPartInput(PLATE, 4).addTaggedPartInput(RING, 1)
                .addFluidInput(MIFluids.SOLDERING_ALLOY, 100).addPartOutput(GEAR, 2);
        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.ASSEMBLER, "drill_head").level(MachineLevel.ELECTRIC)
                .addTaggedPartInput(PLATE, 3)
                .addTaggedPartInput(ROD, 1).addTaggedPartInput(GEAR, 2).addFluidInput(MIFluids.SOLDERING_ALLOY, 75)
                .addPartOutput(DRILL_HEAD, 1);

        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.ASSEMBLER, "fuel_rod", 16, 200)
                .addItemInput("milite:blastproof_alloy_plate", 2)
                .addItemInput(MIItem.LARGE_MOTOR, 1).addItemInput(MIItem.ROBOT_ARM, 2).addTaggedPartInput(ROD, 18)
                .addFluidInput(MIFluids.SOLDERING_ALLOY, 500).addFluidInput(MIFluids.HELIUM, 100)
                .addPartOutput(FUEL_ROD, 1);

        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.HEAT_EXCHANGER, "hot_ingot", 8, 10).addPartInput(HOT_INGOT, 1)
                .addFluidInput(MIFluids.CRYOFLUID, 20)
                .addPartOutput(INGOT, 1).addFluidOutput(MIFluids.ARGON, 13).addFluidOutput(MIFluids.HELIUM, 5);

        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.POLARIZER, "rod_magnetic", 8, 200).addTaggedPartInput(ROD, 1).addPartOutput(ROD_MAGNETIC, 1);
        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.POLARIZER, "wire_magnetic", 8, 200).addTaggedPartInput(WIRE, 1).addPartOutput(WIRE_MAGNETIC, 1);
        new SmeltingRecipeBuilder(ctx, ROD_MAGNETIC, ROD, 0.0f, false);
        new SmeltingRecipeBuilder(ctx, WIRE_MAGNETIC, WIRE, 0.0f, false);

        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.VACUUM_FREEZER, "hot_ingot", 32, 250).level(MachineLevel.ALUMINUM)
                .addTaggedPartInput(HOT_INGOT, 1).addPartOutput(INGOT, 1);
    }

    /**
     * Add a recycling recipe in the macerator.
     */
    private static void addMaceratorRecycling(MaterialBuilder.RecipeContext ctx, PartKeyProvider partInput, int tinyDustOutput) {
        MIRecipeBuilder builder = new MIRecipeBuilder(ctx, MIMachineRecipeTypes.MACERATOR, partInput);
        builder.addTaggedPartInput(partInput, 1);
        if (tinyDustOutput % 9 == 0) {
            builder.addPartOutput(DUST, tinyDustOutput / 9);
        } else {
            while (tinyDustOutput > 64) {
                builder.addPartOutput(TINY_DUST, 64);
                tinyDustOutput -= 64;
            }
            builder.addPartOutput(TINY_DUST, tinyDustOutput);
        }
    }

    /**
     * Add 3x3 -> 1 and 1 -> 9 crafting recipes.
     */
    private static void add3By3Crafting(MaterialBuilder.RecipeContext ctx, PartKeyProvider smallPart, PartKeyProvider bigPart, boolean packer) {
        if (ctx.hasInternalPart(smallPart) || ctx.hasInternalPart(bigPart)) {
            new ShapedRecipeBuilder(ctx, bigPart, 1, bigPart.key() + "_from_" + smallPart.key(), "xxx", "xyx", "xxx")
                    .addPart('y', smallPart)
                    .addTaggedPart('x', smallPart);
            new ShapelessRecipeBuilder(ctx, smallPart, 9, smallPart.key() + "_from_" + bigPart.key())
                    .addPart(bigPart);
        }

        if (packer) {
            new MIRecipeBuilder(ctx, MIMachineRecipeTypes.PACKER, bigPart).addTaggedPartInput(smallPart, 9).addPartOutput(bigPart, 1);
        }
        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.UNPACKER, smallPart).addTaggedPartInput(bigPart, 1).addPartOutput(smallPart, 9);
    }

    private static void addCuttingMachine(MaterialBuilder.RecipeContext ctx, String name, PartKeyProvider inputPart, PartKeyProvider outputPart,
            int amount) {
        new MIRecipeBuilder(ctx, MIMachineRecipeTypes.CUTTING_MACHINE, name).addTaggedPartInput(inputPart, 1).addPartOutput(outputPart, amount);
    }
}
