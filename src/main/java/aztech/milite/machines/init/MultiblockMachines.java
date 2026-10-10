package aztech.milite.machines.init;

import static aztech.milite.machines.models.MachineCasings.CLEAN_STAINLESS_STEEL;
import static aztech.milite.machines.multiblocks.HatchTypes.*;

import aztech.milite.api.energy.CableTier;
import aztech.milite.compat.kubejs.KubeJSProxy;
import aztech.milite.compat.rei.machines.MachineCategoryParams;
import aztech.milite.compat.rei.machines.ReiMachineRecipes;
import aztech.milite.compat.rei.machines.SteamMode;
import aztech.milite.inventory.SlotPositions;
import aztech.milite.machines.blockentities.multiblocks.*;
import aztech.milite.machines.components.FluidItemConsumerComponent;
import aztech.milite.machines.components.OverclockComponent;
import aztech.milite.machines.guicomponents.CraftingMultiblockGui;
import aztech.milite.machines.guicomponents.ProgressBar;
import aztech.milite.machines.models.MachineCasing;
import aztech.milite.machines.models.MachineCasings;
import aztech.milite.machines.multiblocks.HatchFlags;
import aztech.milite.machines.multiblocks.ShapeTemplate;
import aztech.milite.machines.multiblocks.SimpleMember;
import aztech.milite.machines.recipe.MachineRecipe;
import aztech.milite.machines.recipe.MachineRecipeType;
import aztech.milite.MI;
import aztech.milite.MIBlock;
import aztech.milite.MIFluids;
import aztech.milite.util.Rectangle;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MultiblockMachines {
    private static final Rectangle CRAFTING_GUI = new Rectangle(CraftingMultiblockGui.X, CraftingMultiblockGui.Y,
            CraftingMultiblockGui.W, CraftingMultiblockGui.H);
    // @formatter:off
    public static MachineDefinition<SteamCraftingMultiblockBlockEntity> COKE_OVEN;
    public static MachineDefinition<SteamCraftingMultiblockBlockEntity> STEAM_BLAST_FURNACE;
    public static MachineDefinition<SteamCraftingMultiblockBlockEntity> STEAM_QUARRY;
    /**
     * The electric blast furnaces, one per level. Keying them by level is what lets the rest of the code reach the
     * furnace a recipe needs without knowing about the blocks one by one.
     */
    public static final Map<MachineLevel, MachineDefinition<ElectricBlastFurnaceBlockEntity>> ELECTRIC_BLAST_FURNACES = new EnumMap<>(
            MachineLevel.class);
    public static MachineDefinition<SteamBoilerMultiblockBlockEntity> LARGE_STEAM_BOILER;
    public static MachineDefinition<SteamBoilerMultiblockBlockEntity> ADVANCED_LARGE_STEAM_BOILER;
    public static MachineDefinition<SteamBoilerMultiblockBlockEntity> HIGH_PRESSURE_LARGE_STEAM_BOILER;
    public static MachineDefinition<SteamBoilerMultiblockBlockEntity> HIGH_PRESSURE_ADVANCED_LARGE_STEAM_BOILER;
    public static MachineDefinition<ElectricCraftingMultiblockBlockEntity> ELECTRIC_QUARRY;
    public static MachineDefinition<ElectricCraftingMultiblockBlockEntity> OIL_DRILLING_RIG;
    public static MachineDefinition<ElectricCraftingMultiblockBlockEntity> VACUUM_FREEZER;
    public static MachineDefinition<DistillationTowerBlockEntity> DISTILLATION_TOWER;
    public static MachineDefinition<GeneratorMultiblockBlockEntity> LARGE_DIESEL_GENERATOR;
    public static MachineDefinition<GeneratorMultiblockBlockEntity> LARGE_STEAM_TURBINE;
    public static MachineDefinition<ElectricCraftingMultiblockBlockEntity> HEAT_EXCHANGER;
    public static MachineDefinition<ElectricCraftingMultiblockBlockEntity> PRESSURIZER;
    public static MachineDefinition<ElectricCraftingMultiblockBlockEntity> IMPLOSION_COMPRESSOR;
    public static MachineDefinition<NuclearReactorMultiblockBlockEntity> NUCLEAR_REACTOR;
    public static MachineDefinition<LargeTankMultiblockBlockEntity> LARGE_TANK;
    public static MachineDefinition<FusionReactorBlockEntity> FUSION_REACTOR;
    public static MachineDefinition<GeneratorMultiblockBlockEntity> PLASMA_TURBINE;

    private static SimpleMember invarCasings;

    private static SimpleMember bronzePlatedBricks;
    private static SimpleMember bronzePipe;

    private static SimpleMember frostproofMachineCasing;

    private static SimpleMember stainlessSteelClean;
    private static SimpleMember stainlessSteelPipe;

    private static SimpleMember titaniumCasing;
    private static SimpleMember titaniumPipe;


    private static SimpleMember blastProofCasing;

    private static SimpleMember titaniumHull;
    private static SimpleMember fusionChamber;

    private static SimpleMember plasmaHandlingIridium;
    private static SimpleMember iridiumPipe;

    private static final HatchFlags fluidInputs = new HatchFlags.Builder().with(FLUID_INPUT).build();
    private static final HatchFlags energyOutput = new HatchFlags.Builder().with(ENERGY_OUTPUT).build();
    private static final HatchFlags energyInput = new HatchFlags.Builder().with(ENERGY_INPUT).build();


    private static void cokeOven() {
        SimpleMember bricks = SimpleMember.forBlock(() -> Blocks.BRICKS);
        HatchFlags cokeOvenHatches = new HatchFlags.Builder().with(ITEM_INPUT).with(ITEM_OUTPUT).with(FLUID_INPUT).with(FLUID_OUTPUT).build();
        ShapeTemplate cokeOvenShape = new ShapeTemplate.Builder(MachineCasings.BRICKS).add3by3Levels(-1, 1, bricks, cokeOvenHatches).build();
        COKE_OVEN = MachineRegistrationHelper.registerMachine("Coke Oven", "coke_oven",
                bet -> new SteamCraftingMultiblockBlockEntity(bet, "coke_oven", cokeOvenShape, MIMachineRecipeTypes.COKE_OVEN, OverclockComponent.getDefaultCatalysts()));
        ReiMachineRecipes.registerMultiblockShape("coke_oven", cokeOvenShape);
    }

    private static void steamBlastFurnace() {
        SimpleMember fireclayBricks = SimpleMember.forBlock(MIBlock.BLOCK_FIRE_CLAY_BRICKS);
        HatchFlags sbfHatches = new HatchFlags.Builder().with(ITEM_INPUT, ITEM_OUTPUT, FLUID_INPUT, FLUID_OUTPUT).build();
        ShapeTemplate sbfShape = new ShapeTemplate.Builder(MachineCasings.FIREBRICKS).add3by3Levels(-1, 2, fireclayBricks, sbfHatches).build();
        STEAM_BLAST_FURNACE = MachineRegistrationHelper.registerMachine("Steam Blast Furnace", "steam_blast_furnace",
                bet -> new SteamCraftingMultiblockBlockEntity(bet, "steam_blast_furnace", sbfShape, MIMachineRecipeTypes.STEAM_BLAST_FURNACE, OverclockComponent.getDefaultCatalysts()));
        ReiMachineRecipes.registerMultiblockShape("steam_blast_furnace", sbfShape);
        // It has no levels, but it does reach the steel one, so its recipes are checked against that.
        MachineRegistrationHelper.recordMachineLevel(MIMachineRecipeTypes.STEAM_BLAST_FURNACE, MachineLevel.STEEL);
    }

    private static void electricBlastFurnaces() {
        for (ElectricBlastFurnaceBlockEntity.Tier tier : ElectricBlastFurnaceBlockEntity.tiers) {
            ELECTRIC_BLAST_FURNACES.put(tier.level(), MachineRegistrationHelper.registerMachine(tier.englishName(), tier.id(),
                    bet -> new ElectricBlastFurnaceBlockEntity(bet, tier)));
            // A multiblock only knows its level once it is placed, so the level check has to be told about it here.
            MachineRegistrationHelper.recordMachineLevel(MIMachineRecipeTypes.BLAST_FURNACE, tier.level());
        }
        ElectricBlastFurnaceBlockEntity.registerReiShapes();
    }

    private static void steamBoilers() {

        HatchFlags slbHatchFlags = new HatchFlags.Builder().with(ITEM_INPUT, FLUID_INPUT, FLUID_OUTPUT).build();
        ShapeTemplate largeSteamBoilerShape = new ShapeTemplate.Builder(MachineCasings.HEATPROOF).add3by3(-1, invarCasings, false, slbHatchFlags)
                .add3by3(0, bronzePlatedBricks, true, null).add3by3(1, bronzePlatedBricks, true, null).add3by3(2, bronzePlatedBricks, false, null)
                .add(0, 0, 1, bronzePipe, null).add(0, 1, 1, bronzePipe, null).build();


        LARGE_STEAM_BOILER = MachineRegistrationHelper.registerMachine("Large Steam Boiler", "large_steam_boiler",
                bet -> new SteamBoilerMultiblockBlockEntity(bet, largeSteamBoilerShape, "large_steam_boiler",
                        256, false));
        ReiMachineRecipes.registerMultiblockShape("large_steam_boiler", largeSteamBoilerShape);

        ShapeTemplate advancedLargeSteamBoilerShape = new ShapeTemplate.Builder(MachineCasings.HEATPROOF)
                .add3by3(-2, invarCasings, false, slbHatchFlags)
                .add3by3(-1, bronzePlatedBricks, true, null)
                .add3by3(0, bronzePlatedBricks, true, null)
                .add3by3(1, bronzePlatedBricks, true, null)
                .add3by3(2, bronzePlatedBricks, false, null)
                .add(0, -1, 1, bronzePipe, null)
                .add(0, 0, 1, bronzePipe, null)
                .add(0, 1, 1, bronzePipe, null).build();

        ADVANCED_LARGE_STEAM_BOILER = MachineRegistrationHelper.registerMachine("Advanced Large Steam Boiler", "advanced_large_steam_boiler",
                bet -> new SteamBoilerMultiblockBlockEntity(bet, advancedLargeSteamBoilerShape, "advanced_large_steam_boiler",
                        1024, false));
        ReiMachineRecipes.registerMultiblockShape("advanced_large_steam_boiler", advancedLargeSteamBoilerShape);


        ShapeTemplate highPressureLargeSteamBoilerShape = new ShapeTemplate.Builder(MachineCasings.HEATPROOF)
                .add3by3(-1, invarCasings, false, slbHatchFlags)
                .add3by3(0, stainlessSteelClean, true, null)
                .add3by3(1, stainlessSteelClean, true, null)
                .add3by3(2, stainlessSteelClean, false, null)
                .add(0, 0, 1, stainlessSteelPipe, null)
                .add(0, 1, 1, stainlessSteelPipe, null).build();

        HIGH_PRESSURE_LARGE_STEAM_BOILER = MachineRegistrationHelper.registerMachine(
                "High Pressure Large Steam Boiler",
                "high_pressure_large_steam_boiler",
                bet -> new SteamBoilerMultiblockBlockEntity(bet, highPressureLargeSteamBoilerShape, "high_pressure_large_steam_boiler",
                        2048, true));
        ReiMachineRecipes.registerMultiblockShape("high_pressure_large_steam_boiler", highPressureLargeSteamBoilerShape);

        ShapeTemplate highPressureAdvancedLargeSteamBoilerShape = new ShapeTemplate.Builder(MachineCasings.HEATPROOF)
                .add3by3(-2, invarCasings, false, slbHatchFlags)
                .add3by3(-1, stainlessSteelClean, true, null)
                .add3by3(0, stainlessSteelClean, true, null)
                .add3by3(1, stainlessSteelClean, true, null)
                .add3by3(2, stainlessSteelClean, false, null)
                .add(0, -1, 1, stainlessSteelPipe, null)
                .add(0, 0, 1, stainlessSteelPipe, null)
                .add(0, 1, 1, stainlessSteelPipe, null).build();

        HIGH_PRESSURE_ADVANCED_LARGE_STEAM_BOILER = MachineRegistrationHelper.registerMachine(
                "High Pressure Advanced Large Steam Boiler",
                "high_pressure_advanced_large_steam_boiler",
                bet -> new SteamBoilerMultiblockBlockEntity(bet, highPressureAdvancedLargeSteamBoilerShape, "high_pressure_advanced_large_steam_boiler",
                        8192, true));
        ReiMachineRecipes.registerMultiblockShape("high_pressure_advanced_large_steam_boiler", highPressureAdvancedLargeSteamBoilerShape);
    }

    private static void quarries() {
        SimpleMember steelCasing = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("steel_machine_casing")));
        SimpleMember steelPipe = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("steel_machine_casing_pipe")));
        HatchFlags quarryHatchFlags = new HatchFlags.Builder().with(ITEM_INPUT, FLUID_INPUT, ITEM_OUTPUT).build();
        HatchFlags quarryElectricHatchFlags = new HatchFlags.Builder().with(ITEM_INPUT, ITEM_OUTPUT, ENERGY_INPUT).build();

        ShapeTemplate.Builder quarryShapeBuilder = new ShapeTemplate.Builder(MachineCasings.STEEL).add3by3(0, steelCasing, true, quarryHatchFlags)
                .add3by3(1, steelCasing, true, quarryHatchFlags);

        ShapeTemplate.Builder quarryElectricShapeBuilder = new ShapeTemplate.Builder(MachineCasings.STEEL)
                .add3by3(0, steelCasing, true, quarryElectricHatchFlags).add3by3(1, steelCasing, true, quarryElectricHatchFlags);

        for (int y = 2; y <= 4; y++) {
            quarryShapeBuilder.add(-1, y, 1, steelPipe, null);
            quarryShapeBuilder.add(1, y, 1, steelPipe, null);
            quarryElectricShapeBuilder.add(-1, y, 1, steelPipe, null);
            quarryElectricShapeBuilder.add(1, y, 1, steelPipe, null);
        }
        quarryShapeBuilder.add(0, 4, 1, steelCasing, null);
        quarryElectricShapeBuilder.add(0, 4, 1, steelCasing, null);

        SimpleMember chain = SimpleMember.verticalChain();

        for (int y = 0; y <= 3; y++) {
            quarryShapeBuilder.add(0, y, 1, chain, null);
            quarryElectricShapeBuilder.add(0, y, 1, chain, null);
        }

        ShapeTemplate quarryShape = quarryShapeBuilder.build();
        ShapeTemplate quarryElectricShape = quarryElectricShapeBuilder.build();

        STEAM_QUARRY = MachineRegistrationHelper.registerMachine(
                "Steam Quarry",
                "steam_quarry",
                bet -> new SteamCraftingMultiblockBlockEntity(bet, "steam_quarry", quarryShape, MIMachineRecipeTypes.QUARRY, OverclockComponent.getDefaultCatalysts()));
        ReiMachineRecipes.registerMultiblockShape("steam_quarry", quarryShape);
        ELECTRIC_QUARRY = MachineRegistrationHelper.registerMachine(
                "Electric Quarry",
                "electric_quarry",
                bet -> new ElectricCraftingMultiblockBlockEntity(bet, "electric_quarry", quarryElectricShape, MIMachineRecipeTypes.QUARRY));
        ReiMachineRecipes.registerMultiblockShape("electric_quarry", quarryElectricShape);
    }

    private static void oilDrillingRig() {
        SimpleMember steelCasing = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("steel_machine_casing")));
        SimpleMember steelPipe = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("steel_machine_casing_pipe")));
        SimpleMember chain = SimpleMember.verticalChain();
        HatchFlags hatchFlags = new HatchFlags.Builder().with(ITEM_INPUT).with(FLUID_OUTPUT).with(ENERGY_INPUT).build();

        ShapeTemplate oilDrillingRigShape = new ShapeTemplate.LayeredBuilder(MachineCasings.STEEL, new String[][] {
                { "C   C", "C   C", "C   C", "CCCCC", "     ", "     ", "     ", "     ", "     ", "     " },
                { "     ", "     ", "     ", "C   C", " HHH ", "     ", "     ", "     ", "     ", "     " },
                { " o o ", " o o ", " o o ", "CoPoC", " oPo ", " oPo ", " oPo ", " oPo ", " oPo ", "CCCCC" },
                { "     ", "     ", "     ", "C   C", " H#H ", "     ", "     ", "     ", "     ", "     " },
                { "C   C", "C   C", "C   C", "CCCCC", "     ", "     ", "     " , "     ", "     ", "     "},
        })
                .key('C', steelCasing, null)
                .key('o', chain, null)
                .key('P', steelPipe, null)
                .key('H', steelCasing, hatchFlags)
                .build();

        OIL_DRILLING_RIG = MachineRegistrationHelper.registerMachine(
                "Oil Drilling Rig",
                "oil_drilling_rig", bet -> new ElectricCraftingMultiblockBlockEntity(bet,
                        "oil_drilling_rig", oilDrillingRigShape, MIMachineRecipeTypes.OIL_DRILLING_RIG));
        ReiMachineRecipes.registerMultiblockShape("oil_drilling_rig", oilDrillingRigShape);
    }

    private static void vacuumFreezer() {
        HatchFlags vacuumFreezerHatches = new HatchFlags.Builder().with(ITEM_INPUT).with(ITEM_OUTPUT).with(FLUID_INPUT).with(FLUID_OUTPUT)
                .with(ENERGY_INPUT).build();
        ShapeTemplate vacuumFreezerShape = new ShapeTemplate.Builder(MachineCasings.FROSTPROOF)
                .add3by3LevelsRoofed(-1, 2, frostproofMachineCasing, vacuumFreezerHatches).build();
        VACUUM_FREEZER = MachineRegistrationHelper.registerMachine(
                "Vacuum Freezer",
                "vacuum_freezer",
                bet -> new ElectricCraftingMultiblockBlockEntity(bet, "vacuum_freezer", vacuumFreezerShape, MIMachineRecipeTypes.VACUUM_FREEZER));
        ReiMachineRecipes.registerMultiblockShape("vacuum_freezer", vacuumFreezerShape);
    }

    private static void distillationTower() {
        DISTILLATION_TOWER = MachineRegistrationHelper.registerMachine(
                "Large Distillation Tower",
                "distillation_tower", DistillationTowerBlockEntity::new);
        DistillationTowerBlockEntity.registerReiShapes();
    }

    private static void largeDieselGenerator() {
        ShapeTemplate.Builder largeDieselGeneratorShapeBuilder = new ShapeTemplate.Builder(MachineCasings.SOLID_TITANIUM);
        for (int z = 1; z < 4; z++) {
            largeDieselGeneratorShapeBuilder.add(0, 0, z, z < 3 ? titaniumPipe : titaniumCasing, z == 3 ? energyOutput : null);
            for (int x = -1; x < 2; x++) {
                largeDieselGeneratorShapeBuilder.add(x, 1, z, titaniumCasing, null);
                largeDieselGeneratorShapeBuilder.add(x, -1, z, titaniumCasing, null);
                if (x != 0) {
                    largeDieselGeneratorShapeBuilder.add(x, 0, z, titaniumCasing, z < 3 ? fluidInputs : null);
                }
            }
        }
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                if (x != 0 || y != 0) {
                    largeDieselGeneratorShapeBuilder.add(x, y, 0, titaniumPipe, null);
                }
            }
        }
        ShapeTemplate largeDieselGeneratorShape = largeDieselGeneratorShapeBuilder.build();
        LARGE_DIESEL_GENERATOR = MachineRegistrationHelper.registerMachine(
                "Large Diesel Generator",
                "large_diesel_generator", bet ->
                        new GeneratorMultiblockBlockEntity(bet, "large_diesel_generator",
                                largeDieselGeneratorShape,
                                FluidItemConsumerComponent.ofFluidFuels(16384, 2)));
        ReiMachineRecipes.registerMultiblockShape("large_diesel_generator", largeDieselGeneratorShape);
    }

    private static ShapeTemplate largeTurbineShape(MachineCasing mainCasing, SimpleMember casing, SimpleMember pipe) {
        ShapeTemplate.Builder largeTurbineBuilder = new ShapeTemplate.Builder(mainCasing);
        for (int z = 0; z < 4; z++) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (z == 0) {
                        if (x != 0 || y != 0) {
                            largeTurbineBuilder.add(x, y, z, casing, fluidInputs);
                        }
                    } else if (z == 3) {
                        largeTurbineBuilder.add(x, y, z, casing, (x == 0 && y == 0) ? energyOutput : null);
                    } else {
                        largeTurbineBuilder.add(x, y, z, pipe, null);
                    }

                }
            }
        }
        return largeTurbineBuilder.build();

    }

    private static void largeSteamTurbine() {
        ShapeTemplate largeSteamTurbineShape = largeTurbineShape(MachineCasings.CLEAN_STAINLESS_STEEL,
                stainlessSteelClean, stainlessSteelPipe);

        LARGE_STEAM_TURBINE = MachineRegistrationHelper.registerMachine(
                "Large Steam Turbine",
                "large_steam_turbine", bet ->
                        new GeneratorMultiblockBlockEntity(bet, "large_steam_turbine", largeSteamTurbineShape,
                                FluidItemConsumerComponent.ofFluid(16384,
                                        new FluidItemConsumerComponent.FeProductionMapBuilder<>(BuiltInRegistries.FLUID)
                                                .add(MIFluids.STEAM.getId(), 1)
                                                .add(MIFluids.HIGH_PRESSURE_STEAM.getId(), 8)
                                                .add(MIFluids.HEAVY_WATER_STEAM.getId(), 1)
                                                .add(MIFluids.HIGH_PRESSURE_HEAVY_WATER_STEAM.getId(), 8)
                                                .build()
                                )
                                ));
        ReiMachineRecipes.registerMultiblockShape("large_steam_turbine", largeSteamTurbineShape);
    }

    private static void heatExchanger() {
        ShapeTemplate.Builder heatExchangerShapeBuilder = new ShapeTemplate.Builder(MachineCasings.STAINLESS_STEEL_PIPE);
        for (int z = 0; z < 5; z++) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (z > 0 && z < 4) {

                        heatExchangerShapeBuilder.add(x, y, z, x == 1 ? invarCasings : x == 0 ? stainlessSteelPipe : frostproofMachineCasing,
                                ((y == 1 || y == -1) && x == 0) ? energyInput : null);
                    } else {
                        if (z != 0 || x != 0 || y != 0) {
                            HatchFlags flag;
                            if (x == 0) {
                                flag = new HatchFlags.Builder().with(z == 0 ? ITEM_INPUT : ITEM_OUTPUT).with(ENERGY_INPUT).build();
                            } else {
                                boolean fluidOutput = (x == -1) ^ (z == 0);
                                flag = new HatchFlags.Builder().with(fluidOutput ? FLUID_OUTPUT : FLUID_INPUT).build();
                            }

                            heatExchangerShapeBuilder.add(x, y, z, stainlessSteelPipe, flag);
                        }
                    }
                }
            }
        }
        ShapeTemplate heatExchangerShape = heatExchangerShapeBuilder.build();
        HEAT_EXCHANGER = MachineRegistrationHelper.registerMachine(
                "Heat Exchanger",
                "heat_exchanger",
                bet -> new ElectricCraftingMultiblockBlockEntity(bet, "heat_exchanger", heatExchangerShape, MIMachineRecipeTypes.HEAT_EXCHANGER));
        ReiMachineRecipes.registerMultiblockShape("heat_exchanger", heatExchangerShape);

    }

    private static void pressurizer() {
        ShapeTemplate.Builder pressurizeShapeBuilder = new ShapeTemplate.Builder(MachineCasings.SOLID_TITANIUM);
        for (int y = -1; y < 3; y++) {
            SimpleMember member = (y == -1 || y == 2) ? titaniumCasing : titaniumPipe;
            HatchFlags flag = null;
            if (y == -1) {
                flag = new HatchFlags.Builder().with(ENERGY_INPUT, FLUID_OUTPUT).build();
            } else if (y == 2) {
                flag = new HatchFlags.Builder().with(FLUID_INPUT, ITEM_INPUT).build();
            }
            pressurizeShapeBuilder.add(-1, y, 1, member, flag);
            pressurizeShapeBuilder.add(0, y, 1, member, flag);
            pressurizeShapeBuilder.add(1, y, 1, member, flag);
            pressurizeShapeBuilder.add(0, y, 2, member, flag);
            if (y != 0) {
                pressurizeShapeBuilder.add(0, y, 0, member, flag);
            }
        }
        ShapeTemplate pressurizerShape = pressurizeShapeBuilder.build();
        PRESSURIZER = MachineRegistrationHelper.registerMachine(
                "Pressurizer",
                "pressurizer",
                bet -> new ElectricCraftingMultiblockBlockEntity(bet, "pressurizer", pressurizerShape, MIMachineRecipeTypes.PRESSURIZER));
        ReiMachineRecipes.registerMultiblockShape("pressurizer", pressurizerShape);
    }

    private static void implosionCompressor() {
        ShapeTemplate.Builder implosionCompressorShapeBuilder = new ShapeTemplate.Builder(MachineCasings.SOLID_TITANIUM);
        HatchFlags hatchs = new HatchFlags.Builder().with(ITEM_OUTPUT, ITEM_INPUT, ENERGY_INPUT).build();
        implosionCompressorShapeBuilder.add3by3(0, titaniumCasing, false, hatchs);
        implosionCompressorShapeBuilder.add3by3(1, blastProofCasing, true, null);
        implosionCompressorShapeBuilder.add3by3(2, blastProofCasing, true, null);
        implosionCompressorShapeBuilder.add3by3(3, titaniumCasing, false, null);

        ShapeTemplate implosionCompressorShape = implosionCompressorShapeBuilder.build();
        IMPLOSION_COMPRESSOR = MachineRegistrationHelper.registerMachine(
                "Implosion Compressor",
                "implosion_compressor",
                bet -> new ElectricCraftingMultiblockBlockEntity(bet, "implosion_compressor", implosionCompressorShape, MIMachineRecipeTypes.IMPLOSION_COMPRESSOR));
        ReiMachineRecipes.registerMultiblockShape("implosion_compressor", implosionCompressorShape);
    }

    private static void nuclearReactor() {
        NUCLEAR_REACTOR = MachineRegistrationHelper.registerMachine(
                "Nuclear Reactor",
                "nuclear_reactor", NuclearReactorMultiblockBlockEntity::new);
        NuclearReactorMultiblockBlockEntity.registerReiShapes();
    }

    private static void largeTank() {
        LARGE_TANK = MachineRegistrationHelper.registerMachine(
                "Large Tank",
                "large_tank", LargeTankMultiblockBlockEntity::new, LargeTankMultiblockBlockEntity::registerFluidAPI);
    }

    private static void fusionReactor() {
        ShapeTemplate.Builder fusionReactorShapeBuilder = new ShapeTemplate.Builder(CableTier.TITANIUM.casing);
        HatchFlags flags = new HatchFlags.Builder().with(FLUID_INPUT, FLUID_OUTPUT, ENERGY_INPUT).build();
        for (int y = -1; y <= 1; y++) {
            for (int x = -5; x <= 5; x++) {
                for (int z = 0; z <= 10; z++) {
                    int ring = Math.max(Math.abs(x), Math.abs(z - 5));
                    boolean hatchable = (x == 0 && (z == 0 || z == 10)) || (z == 5 && (x == -5 || x == 5));

                    if (ring == 5) {
                        fusionReactorShapeBuilder.add(x, y, z, titaniumHull, hatchable ? flags : null);
                    } else if (ring == 4) {
                        fusionReactorShapeBuilder.add(x, y, z, y == 0 ? fusionChamber : titaniumHull);
                    } else if (ring == 3 && y == 0) {
                        fusionReactorShapeBuilder.add(x, y, z, titaniumHull);
                    }
                }
            }
        }

        ShapeTemplate fusionReactorShape = fusionReactorShapeBuilder.build();
        FUSION_REACTOR = MachineRegistrationHelper.registerMachine(
                "Fusion Reactor",
                "fusion_reactor",
                bet -> new FusionReactorBlockEntity(bet, "fusion_reactor",
                        fusionReactorShape));
        ReiMachineRecipes.registerMultiblockShape("fusion_reactor", fusionReactorShape);

    }

    private static void plasmaTurbine() {
        ShapeTemplate plasmaTurbineShape = largeTurbineShape(MachineCasings.PLASMA_HANDLING_IRIDIUM,
                plasmaHandlingIridium, iridiumPipe);

        PLASMA_TURBINE = MachineRegistrationHelper.registerMachine(
                "Plasma Turbine",
                "plasma_turbine", bet ->
                        new GeneratorMultiblockBlockEntity(bet, "plasma_turbine", plasmaTurbineShape,
                                FluidItemConsumerComponent.ofSingleFluid(
                                        1 << 20,
                                        MIFluids.HELIUM_PLASMA,
                                        100000
                                )));
        ReiMachineRecipes.registerMultiblockShape("plasma_turbine", plasmaTurbineShape);
    }

    public static void init() {

        invarCasings = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("heatproof_machine_casing")));

        bronzePlatedBricks = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("bronze_plated_bricks")));
        bronzePipe = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("bronze_machine_casing_pipe")));

        frostproofMachineCasing = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("frostproof_machine_casing")));

        stainlessSteelClean = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("clean_stainless_steel_machine_casing")));
        stainlessSteelPipe = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("stainless_steel_machine_casing_pipe")));

        titaniumCasing = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("solid_titanium_machine_casing")));
        titaniumPipe = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("titanium_machine_casing_pipe")));

        blastProofCasing = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("blastproof_casing")));


        titaniumHull = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("titanium_machine_hull")));
        fusionChamber = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("fusion_chamber")));

        plasmaHandlingIridium = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("plasma_handling_iridium_machine_casing")));
        iridiumPipe = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("iridium_machine_casing_pipe")));

        cokeOven();
        steamBlastFurnace();
        electricBlastFurnaces();
        steamBoilers();
        quarries();
        oilDrillingRig();
        vacuumFreezer();
        distillationTower();
        largeDieselGenerator();
        largeSteamTurbine();
        heatExchanger();
        pressurizer();
        implosionCompressor();
        nuclearReactor();
        largeTank();
        fusionReactor();
        plasmaTurbine();

        clientInit();
    }

    private static void clientInit() {
        MachineRegistrationHelper.addMachineModel("coke_oven", "coke_oven", MachineCasings.BRICKS, true, false, false);
        new Rei("Coke Oven", "coke_oven", MIMachineRecipeTypes.COKE_OVEN, new ProgressBar.Params(77, 33, "arrow"))
                .items(inputs -> inputs.addSlot(56, 35), outputs -> outputs.addSlot(102, 35))
                .fluids(inputs -> {
                }, outputs -> outputs.addSlot(102, 53))
                .steam(true)
                .register();

        MachineRegistrationHelper.addMachineModel("steam_blast_furnace", "steam_blast_furnace", MachineCasings.FIREBRICKS, true, false, false);

        // Each furnace has its own front panel, so a player can tell which one they are looking at. The panel keeps the
        // green running lamps of every machine, only the furnace's own colour changes.
        for (ElectricBlastFurnaceBlockEntity.Tier tier : ElectricBlastFurnaceBlockEntity.tiers) {
            MachineRegistrationHelper.addMachineModel(tier.id(), tier.id(), MachineCasings.HEATPROOF, true, false, false);
        }

        MachineRegistrationHelper.addMachineModel("large_steam_boiler", "large_boiler", MachineCasings.BRONZE_PLATED_BRICKS, true, false, false);

        MachineRegistrationHelper.addMachineModel("advanced_large_steam_boiler", "large_boiler", MachineCasings.BRONZE_PLATED_BRICKS, true, false,
                false);

        MachineRegistrationHelper.addMachineModel("high_pressure_large_steam_boiler", "large_boiler", CLEAN_STAINLESS_STEEL, true, false, false);

        MachineRegistrationHelper.addMachineModel("high_pressure_advanced_large_steam_boiler", "large_boiler", CLEAN_STAINLESS_STEEL, true, false,
                false);

        MachineRegistrationHelper.addMachineModel("steam_quarry", "steam_quarry", MachineCasings.STEEL, true, false, false);
        new Rei("Steam Quarry", "steam_quarry", MIMachineRecipeTypes.QUARRY, new ProgressBar.Params(77, 33, "arrow"))
                .items(inputs -> inputs.addSlot(56, 35), outputs -> outputs.addSlots(102, 35, 4, 4))
                .workstations("steam_quarry", "electric_quarry").extraTest(recipe -> recipe.energy <= 4)
                .steam(false)
                .register();
        new Rei("Electric Quarry", "electric_quarry", MIMachineRecipeTypes.QUARRY, new ProgressBar.Params(77, 33, "arrow"))
                .items(inputs -> inputs.addSlot(56, 35), outputs -> outputs.addSlots(102, 35, 4, 4))
                .workstations("electric_quarry").extraTest(recipe -> recipe.energy > 4)
                .register();

        MachineRegistrationHelper.addMachineModel("electric_quarry", "electric_quarry", MachineCasings.STEEL, true, false, false);

        MachineRegistrationHelper.addMachineModel("vacuum_freezer", "vacuum_freezer", MachineCasings.FROSTPROOF, true, false, false);
        new Rei("Vacuum Freezer", "vacuum_freezer", MIMachineRecipeTypes.VACUUM_FREEZER, new ProgressBar.Params(77, 33, "arrow"))
                .items(inputs -> inputs.addSlots(56, 35, 1, 2), outputs -> outputs.addSlot(102, 35))
                .fluids(inputs -> inputs.addSlots(36, 35, 1, 2), outputs -> outputs.addSlot(122, 35))
                .register();

        MachineRegistrationHelper.addMachineModel("oil_drilling_rig", "oil_drilling_rig", MachineCasings.STEEL, true, false, false);
        new Rei("Oil Drilling Rig", "oil_drilling_rig", MIMachineRecipeTypes.OIL_DRILLING_RIG, new ProgressBar.Params(77, 33, "arrow"))
                .items(inputs -> inputs.addSlot(36, 35), outputs -> {
                })
                .fluids(inputs -> {
                }, outputs -> outputs.addSlot(122, 35))
                .register();

        MachineRegistrationHelper.addMachineModel("distillation_tower", "distillation_tower", CLEAN_STAINLESS_STEEL, true, false, false);
        new Rei("Large Distillation Tower", "distillation_tower", MIMachineRecipeTypes.DISTILLATION_TOWER, new ProgressBar.Params(77, 33, "arrow"))
                .fluids(inputs -> inputs.addSlot(56, 35), outputs -> outputs.addSlots(102, 35, 8, 1))
                .register();

        MachineRegistrationHelper.addMachineModel("large_diesel_generator", "diesel_generator", MachineCasings.SOLID_TITANIUM, true, false, false);

        MachineRegistrationHelper.addMachineModel("large_steam_turbine", "steam_turbine", CLEAN_STAINLESS_STEEL, true, false, false);

        MachineRegistrationHelper.addMachineModel("heat_exchanger", "heat_exchanger", MachineCasings.STAINLESS_STEEL_PIPE, true, false, false);
        new Rei("Heat Exchanger", "heat_exchanger", MIMachineRecipeTypes.HEAT_EXCHANGER, new ProgressBar.Params(77, 42, "arrow"))
                .items(inputs -> inputs.addSlot(36, 35), outputs -> outputs.addSlot(122, 35))
                .fluids(inputs -> inputs.addSlots(56, 35, 1, 2), outputs -> outputs.addSlots(102, 35, 1, 2))
                .register();

        MachineRegistrationHelper.addMachineModel("pressurizer", "pressurizer", MachineCasings.TITANIUM_PIPE, true, false, false);
        new Rei("Pressurizer", "pressurizer", MIMachineRecipeTypes.PRESSURIZER, new ProgressBar.Params(77, 33, "arrow"))
                .items(inputs -> inputs.addSlot(38, 35), outputs -> {
                })
                .fluids(inputs -> inputs.addSlot(56, 35), outputs -> outputs.addSlot(102, 35))
                .register();

        MachineRegistrationHelper.addMachineModel("implosion_compressor", "compressor", MachineCasings.SOLID_TITANIUM, true, false, false);
        new Rei("Implosion Compressor", "implosion_compressor", MIMachineRecipeTypes.IMPLOSION_COMPRESSOR,
                new ProgressBar.Params(77, 42, "compress"))
                        .items(inputs -> inputs.addSlots(36, 35, 2, 2), outputs -> outputs.addSlot(102, 42))
                        .register();

        MachineRegistrationHelper.addMachineModel("nuclear_reactor", "nuclear_reactor", MachineCasings.NUCLEAR, true, false, false, true);

        MachineRegistrationHelper.addMachineModel("large_tank",
                "large_tank", MachineCasings.STEEL, true, false, false, false);

        MachineRegistrationHelper.addMachineModel("fusion_reactor",
                "fusion_reactor", CableTier.TITANIUM.casing, true, false, false, true);
        new Rei("Fusion Reactor", "fusion_reactor", MIMachineRecipeTypes.FUSION_REACTOR, new ProgressBar.Params(66, 33, "arrow"))
                .fluids(inputs -> inputs.addSlots(26, 35, 2, 1), outputs -> outputs.addSlots(92, 35, 3, 1))
                .register();

        MachineRegistrationHelper.addMachineModel("plasma_turbine", "steam_turbine",
                MachineCasings.PLASMA_HANDLING_IRIDIUM, true, false, false);

        registerEbfReiCategories();
    }

    private static void registerEbfReiCategories() {
        // The steam blast furnace keeps its own category: it is the small, steam driven one, it runs its own recipes,
        // and it is built from fire bricks rather than from heatproof casings. The electric furnaces are workstations of
        // it as well, because they run these recipes too.
        new Rei("Steam Blast Furnace", "steam_blast_furnace", MIMachineRecipeTypes.STEAM_BLAST_FURNACE,
                new ProgressBar.Params(77, 33, "arrow"))
                        .items(inputs -> inputs.addSlots(56, 35, 1, 2), outputs -> outputs.addSlots(102, 35, 1, 1))
                        .fluids(fluids -> fluids.addSlots(36, 35, 1, 1), outputs -> outputs.addSlots(122, 35, 1, 1))
                        .workstations(steamBlastFurnaceWorkstations())
                        .steam(false)
                        .register();

        // The three electric furnaces share one category, and all three are workstations of it. What separates them is
        // the level of recipe they reach, which is a property of the recipe, so a category per furnace would only be
        // the same list three times over. Which furnace can run a recipe is decided when it is built, not shown here.
        new Rei("Electric Blast Furnace", "electric_blast_furnace", MIMachineRecipeTypes.BLAST_FURNACE,
                new ProgressBar.Params(77, 33, "arrow"))
                        .items(inputs -> inputs.addSlots(56, 35, 1, 2), outputs -> outputs.addSlots(102, 35, 1, 1))
                        .fluids(fluids -> fluids.addSlots(36, 35, 1, 1), outputs -> outputs.addSlots(122, 35, 1, 1))
                        .workstations(ElectricBlastFurnaceBlockEntity.tiers.stream()
                                .map(ElectricBlastFurnaceBlockEntity.Tier::id)
                                .toArray(String[]::new))
                        .steam(false)
                        .register();
    }

    /**
     * The steam blast furnace and the three electric ones. An electric furnace runs the steam recipes too, so the viewer
     * lists a steam recipe under all four machines.
     */
    private static String[] steamBlastFurnaceWorkstations() {
        List<String> workstations = new ArrayList<>();
        workstations.add("steam_blast_furnace");
        for (var tier : ElectricBlastFurnaceBlockEntity.tiers) {
            workstations.add(tier.id());
        }
        return workstations.toArray(String[]::new);
    }

    public static class Rei {
        private final String englishName;
        private final ResourceLocation category;
        private final MachineRecipeType recipeType;
        private final ProgressBar.Params progressBarParams;
        private final List<ResourceLocation> workstations;
        // extra workstations to be displayed in viewers, can be any item id
            private Predicate<MachineRecipe> extraTest = recipe -> true;
        private final SlotPositions.Builder itemInputs = new SlotPositions.Builder();
        private final SlotPositions.Builder itemOutputs = new SlotPositions.Builder();
        private final SlotPositions.Builder fluidInputs = new SlotPositions.Builder();
        private final SlotPositions.Builder fluidOutputs = new SlotPositions.Builder();
        private SteamMode steamMode = SteamMode.ELECTRIC_ONLY;

        public Rei(String englishName, ResourceLocation category, MachineRecipeType recipeType, ProgressBar.Params progressBarParams) {
            this.englishName = englishName;
            this.category = category;
            this.recipeType = recipeType;
            this.progressBarParams = progressBarParams;
            this.workstations = new ArrayList<>();
                workstations.add(category);
        }

        public Rei(String englishName, String category, MachineRecipeType recipeType, ProgressBar.Params progressBarParams) {
            this(englishName, MI.id(category), recipeType, progressBarParams);
        }

        public Rei items(Consumer<SlotPositions.Builder> inputs, Consumer<SlotPositions.Builder> outputs) {
            inputs.accept(itemInputs);
            outputs.accept(itemOutputs);
            return this;
        }

        public Rei fluids(Consumer<SlotPositions.Builder> inputs, Consumer<SlotPositions.Builder> outputs) {
            inputs.accept(fluidInputs);
            outputs.accept(fluidOutputs);
            return this;
        }

        public Rei extraTest(Predicate<MachineRecipe> extraTest) {
            this.extraTest = extraTest;
            return this;
        }

        public Rei workstations(ResourceLocation... workstations) {
            this.workstations.clear();
            this.workstations.addAll(Arrays.asList(workstations));
            return this;
        }

        public Rei workstations(String... workstations) {
            return workstations(Arrays.stream(workstations).map(MI::id).toList().toArray(new ResourceLocation[0]));
        }

        public Rei steam(boolean steamOnly) {
            this.steamMode = steamOnly ? SteamMode.STEAM_ONLY : SteamMode.BOTH;
            return this;
        }

        public final void register() {
            // Allow KJS scripts to add slots
            KubeJSProxy.instance.fireAddMultiblockSlotsEvent(category.getPath(), itemInputs, itemOutputs, fluidInputs, fluidOutputs);

            ReiMachineRecipes.registerCategory(category, new MachineCategoryParams(englishName, category,
                    itemInputs.build(), itemOutputs.build(), fluidInputs.build(), fluidOutputs.build(),
                    progressBarParams, recipeType, extraTest, true, steamMode));
            for (ResourceLocation workstation : workstations) {
                ReiMachineRecipes.registerWorkstation(category, workstation);
                ReiMachineRecipes.registerRecipeCategoryForMachine(workstation, category, ReiMachineRecipes.MachineScreenPredicate.MULTIBLOCK);
                ReiMachineRecipes.registerMachineClickArea(workstation, CRAFTING_GUI);
            }
        }
    }
}
