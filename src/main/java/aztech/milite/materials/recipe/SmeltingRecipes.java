package aztech.milite.materials.recipe;

import static aztech.milite.materials.part.MIParts.*;

import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.recipe.MachineRecipeType;
import aztech.milite.materials.MaterialBuilder;
import aztech.milite.materials.recipe.builder.MIRecipeBuilder;
import aztech.milite.materials.recipe.builder.SmeltingRecipeBuilder;

/**
 * Standard smelting and blasting recipes for materials that can be smelted with
 * regular (blast) furnaces.
 */
public final class SmeltingRecipes {
    public static void apply(MaterialBuilder.RecipeContext ctx) {
        SmeltingRecipeBuilder.smeltAndBlast(ctx, TINY_DUST, NUGGET, 0.08f);
        SmeltingRecipeBuilder.smeltAndBlast(ctx, CRUSHED_DUST, INGOT, 0.7f);
        SmeltingRecipeBuilder.smeltAndBlast(ctx, DUST, INGOT, 0.7f);
        SmeltingRecipeBuilder.smeltAndBlast(ctx, ORE, INGOT, 0.7f);
        SmeltingRecipeBuilder.smeltAndBlast(ctx, ORE_DEEPSLATE, INGOT, 0.7f);
        SmeltingRecipeBuilder.smeltAndBlast(ctx, RAW_METAL, INGOT, 0.7f);
        SmeltingRecipeBuilder.smeltAndBlast(ctx, ORE, GEM, 0.7f);
        SmeltingRecipeBuilder.smeltAndBlast(ctx, ORE_DEEPSLATE, GEM, 0.7f);
    }

    public static void applyBlastFurnace(MaterialBuilder.RecipeContext ctx, boolean hotIngot, int eu, int duration) {
        // The steam blast furnace and the electric ones do not share a recipe type, so a recipe is written for the one
        // that can actually run it.
        MachineRecipeType type = eu <= STEAM_MAX_ENERGY ? MIMachineRecipeTypes.STEAM_BLAST_FURNACE : MIMachineRecipeTypes.BLAST_FURNACE;
        MachineLevel level = blastFurnaceLevel(ctx, eu);
        if (hotIngot) {
            new MIRecipeBuilder(ctx, type, "dust", eu, duration).level(level)
                    .addTaggedPartInput(DUST, 1).addPartOutput(HOT_INGOT, 1);
        } else {
            new MIRecipeBuilder(ctx, type, "dust", eu, duration).level(level)
                    .addTaggedPartInput(DUST, 1).addPartOutput(INGOT, 1);
            new MIRecipeBuilder(ctx, type, "tiny_dust", eu, duration / 10).level(level)
                    .addTaggedPartInput(TINY_DUST, 1).addPartOutput(NUGGET, 1);
        }
    }

    /** The most a recipe may cost and still be run by the steam blast furnace. */
    private static final int STEAM_MAX_ENERGY = 4;

    /**
     * Which blast furnace a recipe needs.
     *
     * <p>
     * The cheapest recipes are the ones the steam blast furnace runs. Above that the tier follows the material and not
     * the cost: a coil must be reachable with the furnace one tier below the one it unlocks, so the three coil
     * materials sit one step below their own furnace and only the endgame materials ask for the iridium one.
     */
    private static MachineLevel blastFurnaceLevel(MaterialBuilder.RecipeContext ctx, int eu) {
        if (eu <= STEAM_MAX_ENERGY) {
            return MachineLevel.BRONZE;
        }
        return switch (ctx.getMaterialName()) {
            // Kanthal unlocks the stainless steel furnace, titanium and superconductor unlock the iridium one.
            case "titanium", "superconductor", "annealed_copper", "silicon" -> MachineLevel.STAINLESS_STEEL;
            case "uranium", "uranium_235", "uranium_238", "plutonium", "he_uranium", "le_uranium", "he_mox", "le_mox", "platinum" -> MachineLevel.IRIDIUM;
            default -> MachineLevel.ELECTRIC;
        };
    }

    public static void applyBlastFurnace(MaterialBuilder.RecipeContext ctx, boolean hotIngot, int eu) {
        applyBlastFurnace(ctx, hotIngot, eu, 200);
    }

    public static void applyBlastFurnace(MaterialBuilder.RecipeContext ctx, int eu) {
        applyBlastFurnace(ctx, false, eu);
    }

    public static void applyBlastFurnace(MaterialBuilder.RecipeContext ctx) {
        applyBlastFurnace(ctx, 32);
    }

    private SmeltingRecipes() {}
}
