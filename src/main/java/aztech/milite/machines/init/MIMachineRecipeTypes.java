package aztech.milite.machines.init;

import aztech.milite.compat.kubejs.KubeJSProxy;
import aztech.milite.machines.recipe.CentrifugeMachineRecipeType;
import aztech.milite.machines.recipe.CuttingMachineRecipeType;
import aztech.milite.machines.recipe.FurnaceMachineRecipeType;
import aztech.milite.machines.recipe.MachineRecipeType;
import aztech.milite.MI;
import aztech.milite.MIRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;
import java.util.List;

public class MIMachineRecipeTypes {
    private static final List<MachineRecipeType> recipeTypes = new ArrayList<>();
    // @formatter:off
    // Single block
    public static final MachineRecipeType ASSEMBLER = create("assembler").withItemInputs().withFluidInputs().withItemOutputs();

    public static final MachineRecipeType CENTRIFUGE = create("centrifuge", CentrifugeMachineRecipeType::new).withItemInputs().withFluidInputs().withItemOutputs().withFluidOutputs();
    public static final MachineRecipeType CHEMICAL_REACTOR = create("chemical_reactor").withItemInputs().withFluidInputs().withItemOutputs().withFluidOutputs();
    public static final MachineRecipeType COMPRESSOR = create("compressor").withItemInputs().withItemOutputs();
    public static final MachineRecipeType CUTTING_MACHINE = create("cutting_machine", CuttingMachineRecipeType::new).withItemInputs().withItemOutputs();
    public static final MachineRecipeType DISTILLERY = create("distillery").withFluidInputs().withFluidOutputs();
    public static final MachineRecipeType ELECTROLYZER = create("electrolyzer").withItemInputs().withFluidInputs().withItemOutputs().withFluidOutputs();
    public static final MachineRecipeType FURNACE = create("furnace", FurnaceMachineRecipeType::new).withItemInputs().withItemOutputs();
    public static final MachineRecipeType MACERATOR = create("macerator").withItemInputs().withItemOutputs();
    public static final MachineRecipeType MIXER = create("mixer").withItemInputs().withFluidInputs().withItemOutputs().withFluidOutputs();
    public static final MachineRecipeType PACKER = create("packer").withItemInputs().withItemOutputs();
    public static final MachineRecipeType POLARIZER = create("polarizer").withItemInputs().withItemOutputs();
    public static final MachineRecipeType UNPACKER = create("unpacker").withItemInputs().withItemOutputs();
    public static final MachineRecipeType WIREMILL = create("wiremill").withItemInputs().withItemOutputs();
    // Multi block
    /**
     * What the three electric blast furnaces run. All of them share this one type: they only differ by the level of
     * recipe they can reach, and the level is part of the recipe, so there is nothing a per furnace type would say that
     * this does not.
     */
    public static final MachineRecipeType BLAST_FURNACE = create("blast_furnace").withItemInputs().withItemOutputs().withFluidInputs().withFluidOutputs();
    // 蒸汽高炉独立配方
    public static final MachineRecipeType STEAM_BLAST_FURNACE = create("steam_blast_furnace").withItemInputs().withItemOutputs()
            .withFluidInputs().withFluidOutputs();
    public static final MachineRecipeType COKE_OVEN = create("coke_oven").withItemInputs().withItemOutputs().withFluidOutputs();
    public static final MachineRecipeType DISTILLATION_TOWER = create("distillation_tower").withFluidInputs().withFluidOutputs();
    public static final MachineRecipeType FUSION_REACTOR = create("fusion_reactor").withFluidInputs().withFluidOutputs();
    public static final MachineRecipeType HEAT_EXCHANGER = create("heat_exchanger").withFluidInputs().withFluidOutputs().withItemOutputs().withItemInputs();
    public static final MachineRecipeType IMPLOSION_COMPRESSOR = create("implosion_compressor").withItemInputs().withItemOutputs();
    public static final MachineRecipeType OIL_DRILLING_RIG = create("oil_drilling_rig").withItemInputs().withFluidOutputs();
    public static final MachineRecipeType PRESSURIZER = create("pressurizer").withItemInputs().withFluidInputs().withFluidOutputs();
    public static final MachineRecipeType QUARRY = create("quarry").withItemInputs().withItemOutputs();
    public static final MachineRecipeType VACUUM_FREEZER = create("vacuum_freezer").withItemInputs().withItemOutputs().withFluidInputs().withFluidOutputs();
    // @formatter:on

    static {
        KubeJSProxy.instance.fireRegisterRecipeTypesEvent();
    }

    public static void init() {
        // init static
    }

    public static List<MachineRecipeType> getRecipeTypes() {
        return Collections.unmodifiableList(recipeTypes);
    }

    public static MachineRecipeType create(String name) {
        return create(name, MachineRecipeType::new);
    }

    private static MachineRecipeType create(String name, Function<ResourceLocation, MachineRecipeType> ctor) {
        return create(MI.id(name), ctor);
    }

    private static MachineRecipeType create(ResourceLocation id, Function<ResourceLocation, MachineRecipeType> ctor) {
        MachineRecipeType type = ctor.apply(id);
        MIRegistries.RECIPE_SERIALIZERS.register(id.getPath(), () -> type);
        MIRegistries.RECIPE_TYPES.register(id.getPath(), () -> type);
        recipeTypes.add(type);
        return type;
    }
}
