package aztech.milite.compat.rei.machines;

import aztech.milite.machines.multiblocks.ShapeTemplate;
import aztech.milite.MI;
import aztech.milite.util.Rectangle;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Helper to register stuff for use with the REI plugin. This class will be
 * called even if REI is not loaded, and should not reference the REI API.
 */
public class ReiMachineRecipes {
    public static final Map<ResourceLocation, MachineCategoryParams> categories = new TreeMap<>();
    /**
     * Maps a machine block id to the list of recipe categories.
     */
    public static final Map<ResourceLocation, List<ClickAreaCategory>> machineToClickAreaCategory = new HashMap<>();
    /**
     * Maps a machine block id to the parameters of the click area for the recipe.
     */
    public static final Map<ResourceLocation, Rectangle> machineToClickArea = new HashMap<>();
    /**
     * List of registered multiblock shape "recipes".
     */
    public static final List<MultiblockShape> multiblockShapes = new ArrayList<>();

    public static void registerCategory(ResourceLocation machine, MachineCategoryParams params) {
        if (categories.put(machine, params) != null) {
            throw new IllegalStateException("Machine was already registered: " + machine);
        }
    }

    public static void registerWorkstation(ResourceLocation machine, ResourceLocation item) {
        MachineCategoryParams params = categories.get(machine);
        if (params == null) {
            throw new NullPointerException("Machine params may not be null for machine " + machine);
        }
        params.workstations.add(item);
    }

    public static void registerRecipeCategoryForMachine(ResourceLocation machine, ResourceLocation category) {
        registerRecipeCategoryForMachine(machine, category, MachineScreenPredicate.ANY);
    }

    public static void registerRecipeCategoryForMachine(ResourceLocation machine, ResourceLocation category,
            MachineScreenPredicate screenPredicate) {
        machineToClickAreaCategory.computeIfAbsent(machine, k -> new ArrayList<>())
                .add(new ClickAreaCategory(category, screenPredicate));
    }

    public static void registerMachineClickArea(ResourceLocation machine, Rectangle clickArea) {
        machineToClickArea.put(machine, clickArea);
    }

    public static void registerMultiblockShape(ResourceLocation machine, ShapeTemplate shapeTemplate) {
        registerMultiblockShape(machine, shapeTemplate, null);
    }

    public static void registerMultiblockShape(String machine, ShapeTemplate shapeTemplate) {
        registerMultiblockShape(MI.id(machine), shapeTemplate);
    }

    public static void registerMultiblockShape(ResourceLocation machine, ShapeTemplate shapeTemplate, @Nullable String alternative) {
        multiblockShapes.add(new MultiblockShape(machine, shapeTemplate, alternative));
    }

    public static void registerMultiblockShape(String machine, ShapeTemplate shapeTemplate, @Nullable String alternative) {
        registerMultiblockShape(MI.id(machine), shapeTemplate, alternative);
    }

    public static class ClickAreaCategory {
        public final ResourceLocation category;
        public final MachineScreenPredicate predicate;

        ClickAreaCategory(ResourceLocation category, MachineScreenPredicate predicate) {
            this.category = category;
            this.predicate = predicate;
        }
    }

    public enum MachineScreenPredicate {
        ANY,
        MULTIBLOCK,
    }

    public record MultiblockShape(ResourceLocation machine, ShapeTemplate shapeTemplate, @Nullable String alternative) {}
}
