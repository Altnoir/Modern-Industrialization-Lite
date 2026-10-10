package aztech.milite.machines.init;

import aztech.milite.api.energy.CableTier;
import aztech.milite.datagen.model.MachineModelsToGenerate;
import aztech.milite.definition.BlockDefinition;
import aztech.milite.items.SortOrder;
import aztech.milite.machines.BEP;
import aztech.milite.machines.MachineBlock;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.models.MachineCasing;
import aztech.milite.machines.models.MachineCasings;
import aztech.milite.machines.recipe.MachineRecipeType;
import aztech.milite.MIBlock;
import aztech.milite.MIRegistries;
import aztech.milite.util.MobSpawning;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineRegistrationHelper {
    /**
     * Register a machine's block, block entity type and wrenchable tag.
     *
     * @param id                Machine block id, for example "electric_macerator"
     * @param factory           The block entity constructor, with a BET parameter.
     * @param extraRegistrators A list of BET consumer used for API registration.
     */
    @SafeVarargs
    public static <T extends MachineBlockEntity> MachineDefinition<T> registerMachine(String englishName, String id,
                                                                                      Function<BEP, ? extends T> factory,
                                                                                      Consumer<? super BlockEntityType<T>>... extraRegistrators) {
        BlockEntityType<T>[] bet = new BlockEntityType[1];
        BiFunction<BlockPos, BlockState, T> ctor = (pos, state) -> factory.apply(new BEP(bet[0], pos, state));

        BlockDefinition<MachineBlock> blockDefinition = MIBlock.block(
                englishName,
                id,
                MIBlock.BlockDefinitionParams.defaultStone()
                        .sortOrder(machineSortOrder(id))
                        .withBlockConstructor((s) -> new MachineBlock(ctor, s))
                        .sound(SoundType.NETHERITE_BLOCK)
                        .withModel((block, gen) -> {
                            // Model generation is handled in the model provider already.
                        })
                        .isValidSpawn(MobSpawning.NO_SPAWN)
                        .isRedstoneConductor(Blocks::never));

        var blockEntityType = MIRegistries.BLOCK_ENTITIES.register(id, () -> {
            Block block = blockDefinition.asBlock();

            bet[0] = BlockEntityType.Builder.of(ctor::apply, block).build(null);

            for (Consumer<? super BlockEntityType<T>> extraRegistrator : extraRegistrators) {
                extraRegistrator.accept(bet[0]);
            }

            return bet[0];
        });

        return new MachineDefinition<>(blockEntityType, blockDefinition);
    }

    /**
     * The tier prefixes machine ids are built from, in the order a player meets them. A machine named
     * {@code <tier>_<machine>} is one of the machines that exists at several tiers.
     *
     * <p>
     * The material tiers come from {@link MachineLevel}, which is the ladder this mod already keeps, so the two cannot
     * drift apart. The tiers only the multiblocks use follow after it; their numbers only ever get compared against
     * machines that share their name, so the two ladders never have to agree on anything.
     */
    private static final List<String> MACHINE_TIER_PREFIXES = machineTierPrefixes();

    private static final List<String> MACHINE_HEAD = List.of("coke_oven");
    private static final List<String> MACHINE_TAIL = List.of("configurable_chest", "configurable_tank");
    private static final int MACHINE_HEAD_RANK = -1;
    private static final int MACHINE_TAIL_RANK = MACHINE_TIER_PREFIXES.size() + 1;

    private static final Map<String, String> MACHINE_FOLLOWS = machineFollows();

    private static Map<String, String> machineFollows() {
        var follows = new HashMap<String, String>();
        follows.put("large_steam_boiler", "steam_blast_furnace");
        follows.put("advanced_large_steam_boiler", "large_steam_boiler");
        follows.put("steam_quarry", "steel_boiler");
        return Map.copyOf(follows);
    }

    /**
     * The kinds of hatch, in the order the tab lists them: what moves items, then what moves fluids, then what moves
     * energy, each with its inputs ahead of its outputs. A hatch of any other kind sorts after these.
     */
    private static final List<String> HATCH_KINDS = List.of("item_input", "item_output", "fluid_input", "fluid_output",
            "energy_input", "energy_output");

    private static List<String> machineTierPrefixes() {
        var prefixes = new ArrayList<String>();
        prefixes.add("steam");
        for (var level : MachineLevel.values()) {
            prefixes.add(level.getSerializedName());
        }
        prefixes.addAll(List.of("advanced", "turbo", "highly_advanced", "nuclear"));
        return List.copyOf(prefixes);
    }

    /**
     * Where a machine sits in the creative tab: the tier it is a version of first, the machine it is second, so the tab
     * reads as the upgrade path, every bronze machine together and ahead of the steel ones.
     *
     * @param id - the machine block id, for example {@code electric_macerator}.
     * @returns the order to register the machine with.
     */
    private static SortOrder machineSortOrder(String id) {
        // A hatch is a part a multiblock is built from rather than a machine of its own, so it goes with the casings.
        if (id.endsWith("_hatch")) {
            return hatchSortOrder(id);
        }
        int head = MACHINE_HEAD.indexOf(id);
        if (head >= 0) {
            return SortOrder.MACHINES.and(MACHINE_HEAD_RANK).and(head);
        }
        int tail = MACHINE_TAIL.indexOf(id);
        if (tail >= 0) {
            return SortOrder.MACHINES.and(MACHINE_TAIL_RANK).and(tail);
        }
        var follows = MACHINE_FOLLOWS.get(id);
        if (follows != null) {
            return machineSortOrder(follows).and(0);
        }
        for (int tier = 0; tier < MACHINE_TIER_PREFIXES.size(); tier++) {
            var prefix = MACHINE_TIER_PREFIXES.get(tier);
            if (id.startsWith(prefix + "_")) {
                var machine = id.substring(prefix.length() + 1);
                return SortOrder.MACHINES.and(tier).and(machineKindOrder(machine)).and(machine);
            }
        }
        return SortOrder.MACHINES.and(MACHINE_TIER_PREFIXES.size()).and(machineKindOrder(id)).and(id);
    }

    /**
     * The order machine kinds are listed in inside one tier. The blast furnace comes first: it is the machine that turns
     * a metal into the plates the rest of the tier is built from, so it reads as the head of its row.
     */
    private static int machineKindOrder(String machine) {
        return machine.equals("blast_furnace") ? 0 : 1;
    }

    /**
     * The level a structure of the given name belongs to.
     */
    private static int structureLevel(String prefix) {
        var level = MachineLevel.byNameOrNull(prefix);
        return level == null ? MachineLevel.values().length : level.ordinal();
    }

    /**
     * Where a hatch sits in the creative tab: the level it belongs to first, its kind second, so the item hatches come
     * before the fluid and energy ones of the same level.
     *
     * @param id - the hatch block id, for example {@code bronze_item_input_hatch}.
     * @returns the order to register the hatch with.
     */
    private static SortOrder hatchSortOrder(String id) {
        var kind = id.substring(0, id.length() - "_hatch".length());
        var level = MachineLevel.values().length;
        for (var prefix : MACHINE_TIER_PREFIXES) {
            if (id.startsWith(prefix + "_")) {
                kind = id.substring(prefix.length() + 1, id.length() - "_hatch".length());
                level = structureLevel(prefix);
                break;
            }
        }
        var kindOrder = HATCH_KINDS.indexOf(kind);
        return SortOrder.HATCHES.withLevel(level).and(kindOrder < 0 ? HATCH_KINDS.size() : kindOrder);
    }

    /**
     * The highest level any machine of a given recipe type can reach.
     *
     * <p>
     * A recipe whose level is above this can never be crafted by anything, which is a mistake that is otherwise only
     * found by noticing the recipe never works. {@code MachineLevelValidator} uses this to report such recipes.
     */
    private static final Map<MachineRecipeType, MachineLevel> highestLevelByRecipeType = new HashMap<>();

    /**
     * Records that a machine of the given recipe type exists at the given level.
     */
    public static void recordMachineLevel(MachineRecipeType recipeType, MachineLevel level) {
        highestLevelByRecipeType.merge(recipeType, level,
                (current, added) -> added.ordinal() > current.ordinal() ? added : current);
    }

    public static @Nullable MachineLevel highestLevel(MachineRecipeType recipeType) {
        return highestLevelByRecipeType.get(recipeType);
    }

    /**
     * The casing a machine level is built with. This is what decides the appearance of the machine, see
     * {@code MachineBakedModel}.
     */
    public static MachineCasing casingOf(MachineLevel level) {
        return switch (level) {
            case BRONZE -> MachineCasings.BRONZE;
            case STEEL -> MachineCasings.STEEL;
            case ELECTRIC -> CableTier.ELECTRIC.casing;
            case ALUMINUM -> CableTier.ALUMINUM.casing;
            case STAINLESS_STEEL -> CableTier.STAINLESS_STEEL.casing;
            case TITANIUM -> CableTier.TITANIUM.casing;
            case IRIDIUM -> CableTier.IRIDIUM.casing;
        };
    }

    /** The built-in energy casing tier for electric machines at a given machine level. */
    public static CableTier casingTierOf(MachineLevel level) {
        return switch (level) {
            case BRONZE, STEEL, ELECTRIC -> CableTier.ELECTRIC;
            case ALUMINUM -> CableTier.ALUMINUM;
            case STAINLESS_STEEL -> CableTier.STAINLESS_STEEL;
            case TITANIUM -> CableTier.TITANIUM;
            case IRIDIUM -> CableTier.IRIDIUM;
        };
    }

    /**
     * Registers the model of a machine variant, taking its casing from the level it belongs to.
     */
    public static void addMachineModel(MachineLevel level, String id, String machineType, boolean frontOverlay, boolean topOverlay,
                                       boolean sideOverlay) {
        addMachineModel(id, machineType, casingOf(level), frontOverlay, topOverlay, sideOverlay);
    }

    /**
     * @deprecated prefer {@link #addMachineModel(MachineLevel, String, String, boolean, boolean, boolean)}, which covers
     * every level instead of only the three oldest ones.
     */
    @Deprecated(forRemoval = false)
    @SuppressWarnings("IfCanBeSwitch")
    public static void addMachineModel(String tier, String id, String machineType, boolean frontOverlay, boolean topOverlay, boolean sideOverlay) {
        MachineCasing defaultCasing;
        if (tier.equals("bronze")) {
            defaultCasing = MachineCasings.BRONZE;
        } else if (tier.equals("steel")) {
            defaultCasing = MachineCasings.STEEL;
        } else if (tier.equals("electric")) {
            defaultCasing = CableTier.ELECTRIC.casing;
        } else {
            throw new RuntimeException("Invalid tier: " + tier);
        }
        addMachineModel(id, machineType, defaultCasing, frontOverlay, topOverlay, sideOverlay);
    }

    public static void addMachineModel(String id, String overlayFolder, MachineCasing defaultCasing, boolean frontOverlay, boolean topOverlay,
                                       boolean sideOverlay) {
        addMachineModel(id, overlayFolder, defaultCasing, frontOverlay, topOverlay, sideOverlay, true);
    }

    public static void addMachineModel(String id, String overlayFolder, MachineCasing defaultCasing, boolean frontOverlay, boolean topOverlay,
                                       boolean sideOverlay, boolean hasActive) {
        MachineModelsToGenerate.register(id, defaultCasing, overlayFolder, frontOverlay, topOverlay, sideOverlay, hasActive);
    }

    public static void addModelsForTiers(String name, boolean frontOverlay, boolean topOverlay, boolean sideOverlay, String... tiers) {
        for (String tier : tiers) {
            addMachineModel(tier, tier + "_" + name, name, frontOverlay, topOverlay, sideOverlay);
        }
    }
}
