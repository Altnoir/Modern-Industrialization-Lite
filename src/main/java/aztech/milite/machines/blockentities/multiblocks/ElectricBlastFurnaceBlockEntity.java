package aztech.milite.machines.blockentities.multiblocks;

import static aztech.milite.machines.multiblocks.HatchTypes.*;

import aztech.milite.api.machine.holder.EnergyListComponentHolder;
import aztech.milite.compat.kubejs.KubeJSProxy;
import aztech.milite.compat.rei.machines.ReiMachineRecipes;
import aztech.milite.machines.BEP;
import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.components.OverdriveComponent;
import aztech.milite.machines.guicomponents.SlotPanel;
import aztech.milite.machines.init.MachineLevel;
import aztech.milite.machines.init.MachineTier;
import aztech.milite.machines.init.MIMachineRecipeTypes;
import aztech.milite.machines.models.MachineCasings;
import aztech.milite.machines.multiblocks.HatchFlags;
import aztech.milite.machines.multiblocks.ShapeTemplate;
import aztech.milite.machines.multiblocks.SimpleMember;
import aztech.milite.machines.recipe.MachineRecipe;
import aztech.milite.machines.recipe.MachineRecipeType;
import aztech.milite.MI;
import aztech.milite.MIBlock;
import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;

public class ElectricBlastFurnaceBlockEntity extends AbstractElectricCraftingMultiblockBlockEntity implements EnergyListComponentHolder {
    /**
     * One electric blast furnace. The coil is part of the structure, so the coil, the energy it lets the furnace draw
     * and the level of recipe the furnace runs all belong to the same machine. A furnace also runs the recipes of every
     * furnace below it, so a higher one can take over the work of the ones it replaces.
     *
     * @param id          the block id of this furnace
     * @param englishName its name, which is also the name of its recipe category
     * @param coilBlockId the coil its structure is built with, and the only coil that fits it
     * @param maxBaseFE   the energy per tick the coil lets the furnace draw, shown on the coil tooltip
     * @param level       the level of recipe it runs, and of every furnace below it
     */
    public record Tier(String id, String englishName, ResourceLocation coilBlockId, long maxBaseFE, MachineLevel level) {}

    public static final List<Tier> tiers;
    public static final Map<ResourceLocation, Tier> tiersByCoil;
    private static final Map<String, ShapeTemplate> shapesById;

    static {
        List<Tier> registrationTiers = new ArrayList<>();
        registrationTiers.add(new Tier("electric_blast_furnace", "Electric Blast Furnace", MI.id("cupronickel_coil"), 32,
                MachineLevel.ELECTRIC));
        registrationTiers.add(new Tier("stainless_steel_blast_furnace", "Stainless Steel Blast Furnace", MI.id("kanthal_coil"), 128,
                MachineLevel.STAINLESS_STEEL));
        registrationTiers.add(new Tier("iridium_blast_furnace", "Iridium Blast Furnace", MI.id("superconductor_coil"), 512,
                MachineLevel.IRIDIUM));

        KubeJSProxy.instance.fireAddEbfTiersEvent(tier -> {
            Preconditions.checkArgument(tier.maxBaseFE() > 4, "EBF tier FE/t must be greater than 4.");
            for (var t : registrationTiers) {
                if (t.id().equals(tier.id())) {
                    throw new IllegalArgumentException("An EBF with id " + tier.id() + " is already registered.");
                }
                if (t.coilBlockId().equals(tier.coilBlockId())) {
                    throw new IllegalArgumentException("An EBF with coil " + tier.coilBlockId() + " is already registered.");
                }
                if (t.level() == tier.level()) {
                    throw new IllegalArgumentException("An EBF with level " + tier.level() + " is already registered.");
                }
            }
            registrationTiers.add(tier);
        });
        registrationTiers.sort(Comparator.comparingInt(tier -> tier.level().ordinal()));

        tiers = List.copyOf(registrationTiers);
        tiersByCoil = tiers.stream().collect(Collectors.toMap(Tier::coilBlockId, t -> t));

        Map<String, ShapeTemplate> shapes = new LinkedHashMap<>();
        for (Tier tier : tiers) {
            shapes.put(tier.id(), createShape(tier.coilBlockId()));
        }
        shapesById = Map.copyOf(shapes);
    }

    /**
     * Every furnace is the same four heatproof layers, with the coil it is built around in the middle two.
     */
    private static ShapeTemplate createShape(ResourceLocation coilBlockId) {
        SimpleMember heatproofCasings = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS.get(MI.id("heatproof_machine_casing")));
        SimpleMember coils = SimpleMember.forBlockId(coilBlockId);
        HatchFlags hatches = new HatchFlags.Builder().with(ITEM_INPUT, ITEM_OUTPUT, FLUID_INPUT, FLUID_OUTPUT, ENERGY_INPUT).build();
        return new ShapeTemplate.Builder(MachineCasings.HEATPROOF)
                .add3by3(0, heatproofCasings, false, hatches)
                .add3by3(1, coils, true, null)
                .add3by3(2, coils, true, null)
                .add3by3(3, heatproofCasings, false, hatches)
                .build();
    }

    public ElectricBlastFurnaceBlockEntity(BEP bep, Tier tier) {
        super(bep, tier.id(), new OrientationComponent.Params(false, false, false), new ShapeTemplate[] { shapesById.get(tier.id()) });
        this.tier = tier;
        this.overdrive = new OverdriveComponent();
        this.registerComponents(overdrive);
        registerGuiComponent(new SlotPanel(this)
                .withRedstoneControl(redstoneControl)
                .withOverdrive(overdrive));
    }

    private final Tier tier;
    private final OverdriveComponent overdrive;

    public Tier getTier() {
        return tier;
    }

    @Override
    public MachineRecipeType recipeType() {
        return MIMachineRecipeTypes.BLAST_FURNACE;
    }

    /**
     * Which recipes the furnace runs is decided by the block a player built, not by anything inside it.
     */
    @Override
    public MachineLevel getMachineLevel() {
        return tier.level();
    }

    /**
     * A furnace runs the recipes of its own level and of every level below it.
     */
    @Override
    public boolean banRecipe(MachineRecipe recipe) {
        return !tier.level().canCraft(recipe.level);
    }

    @Override
    public int getSpeedBonus() {
        return 2;
    }

    @Override
    public boolean isOverdriving() {
        return overdrive.shouldOverdrive();
    }

    public static void registerReiShapes() {
        for (Tier tier : tiers) {
            ReiMachineRecipes.registerMultiblockShape(tier.id(), shapesById.get(tier.id()), tier.coilBlockId().getPath());
        }
    }
}
