package aztech.milite.machines.init;

import aztech.milite.api.energy.CableTier;
import aztech.milite.compat.kubejs.KubeJSProxy;
import aztech.milite.datagen.model.MachineModelProperties;
import aztech.milite.datagen.model.MachineModelsToGenerate;
import aztech.milite.inventory.ConfigurableFluidStack;
import aztech.milite.inventory.ConfigurableItemStack;
import aztech.milite.inventory.MIInventory;
import aztech.milite.inventory.SlotPositions;
import aztech.milite.machines.blockentities.hatches.EnergyHatch;
import aztech.milite.machines.blockentities.hatches.FluidHatch;
import aztech.milite.machines.blockentities.hatches.ItemHatch;
import aztech.milite.machines.blockentities.hatches.LargeTankHatch;
import aztech.milite.machines.blockentities.hatches.NuclearHatch;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.models.MachineCasing;
import aztech.milite.machines.models.MachineCasings;
import aztech.milite.machines.multiblocks.HatchBlockEntity;
import aztech.milite.MI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiblockHatches {
    public record HatchPair<T extends HatchBlockEntity>(MachineDefinition<T> input, MachineDefinition<T> output) {}

    public static final HatchPair<ItemHatch> BRONZE_ITEM = registerItemHatches("Bronze", "bronze", MachineCasings.BRONZE, 1, 1, 80, 40);
    public static final HatchPair<ItemHatch> STEEL_ITEM = registerItemHatches("Steel", "steel", MachineCasings.STEEL, 2, 1, 80, 30);
    public static final HatchPair<ItemHatch> ADVANCED_ITEM = registerItemHatches("Advanced", "advanced", CableTier.MV.casing, 2, 2, 71, 30);
    public static final HatchPair<ItemHatch> TURBO_ITEM = registerItemHatches("Turbo", "turbo", CableTier.HV.casing, 3, 3, 62, 21);
    public static final HatchPair<ItemHatch> HIGHLY_ADVANCED_ITEM = registerItemHatches("Highly Advanced", "highly_advanced", CableTier.EV.casing, 3, 5, 44, 18);

    public static final HatchPair<FluidHatch> BRONZE_FLUID = registerFluidHatches("Bronze", "bronze", MachineCasings.BRONZE, 4);
    public static final HatchPair<FluidHatch> STEEL_FLUID = registerFluidHatches("Steel", "steel", MachineCasings.STEEL, 16);
    public static final HatchPair<FluidHatch> ADVANCED_FLUID = registerFluidHatches("Advanced", "advanced", CableTier.MV.casing, 64);
    public static final HatchPair<FluidHatch> TURBO_FLUID = registerFluidHatches("Turbo", "turbo", CableTier.HV.casing, 256);
    public static final HatchPair<FluidHatch> HIGHLY_ADVANCED_FLUID = registerFluidHatches("Highly Advanced", "highly_advanced", CableTier.EV.casing, 1024);

    public static final HatchPair<EnergyHatch> LV_ENERGY = registerEnergyHatches(CableTier.LV);
    public static final HatchPair<EnergyHatch> MV_ENERGY = registerEnergyHatches(CableTier.MV);
    public static final HatchPair<EnergyHatch> HV_ENERGY = registerEnergyHatches(CableTier.HV);
    public static final HatchPair<EnergyHatch> EV_ENERGY = registerEnergyHatches(CableTier.EV);
    public static final HatchPair<EnergyHatch> SUPERCONDUCTOR_ENERGY = registerEnergyHatches(CableTier.SUPERCONDUCTOR);

    public static final MachineDefinition<NuclearHatch> NUCLEAR_ITEM = MachineRegistrationHelper.registerMachine(
            "Nuclear Item Hatch", "nuclear_item_hatch",
            bet -> new NuclearHatch(bet, false), NuclearHatch::registerItemApi);
    public static final MachineDefinition<NuclearHatch> NUCLEAR_FLUID = MachineRegistrationHelper.registerMachine(
            "Nuclear Fluid Hatch", "nuclear_fluid_hatch",
            bet -> new NuclearHatch(bet, true), NuclearHatch::registerFluidApi);

    public static final MachineDefinition<LargeTankHatch> LARGE_TANK = MachineRegistrationHelper.registerMachine(
            "Large Tank Hatch", "large_tank_hatch",
            LargeTankHatch::new, LargeTankHatch::registerFluidApi);

    public static void init() {
        // Triggers static init

        MachineModelsToGenerate.register(
                "nuclear_item_hatch", new MachineModelProperties.Builder(MachineCasings.NUCLEAR)
                        .addOverlay("top", MI.id("block/overlays/output_item"))
                        .addOverlay("item_auto", MI.id("block/overlays/item_auto"))
                        .build());
        MachineModelsToGenerate.register(
                "nuclear_fluid_hatch", new MachineModelProperties.Builder(MachineCasings.NUCLEAR)
                        .addOverlay("top", MI.id("block/overlays/output_fluid"))
                        .addOverlay("fluid_auto", MI.id("block/overlays/fluid_auto"))
                        .build());

        MachineRegistrationHelper.addMachineModel("large_tank_hatch", "hatch_fluid", MachineCasings.STEEL, false, false, true, false);

        KubeJSProxy.instance.fireRegisterHatchesEvent();
    }

    public static HatchPair<ItemHatch> registerItemHatches(
            String englishPrefix, String prefix, MachineCasing casing, int rows, int columns, int xStart, int yStart) {
        List<MachineDefinition<ItemHatch>> definitions = new ArrayList<>(2);

        for (int iter = 0; iter < 2; ++iter) {
            boolean input = iter == 0;
            String machine = prefix + "_item_" + (input ? "input" : "output") + "_hatch";
            String englishName = englishPrefix + " Item" + (input ? " Input" : " Output") + " Hatch";
            var def = MachineRegistrationHelper.registerMachine(englishName, machine, bet -> {
                List<ConfigurableItemStack> itemStacks = new ArrayList<>();
                for (int i = 0; i < rows * columns; ++i) {
                    if (input) {
                        itemStacks.add(ConfigurableItemStack.standardInputSlot());
                    } else {
                        itemStacks.add(ConfigurableItemStack.standardOutputSlot());
                    }
                }
                MIInventory inventory = new MIInventory(itemStacks, Collections.emptyList(),
                        new SlotPositions.Builder().addSlots(xStart, yStart, columns, rows).build(), SlotPositions.empty());
                return new ItemHatch(bet, new MachineGuiParameters.Builder(machine, true).build(), input, !prefix.equals("bronze"), inventory);
            }, MachineBlockEntity::registerItemApi);
            definitions.add(def);

            var model = new MachineModelProperties.Builder(casing);
            model.addOverlay("side", MI.id("block/machines/hatch_item/overlay_side"));
            model.addOverlay("output", MI.id("block/overlays/output_item"));
            model.addOverlay("item_auto", MI.id("block/overlays/item_auto"));
            MachineModelsToGenerate.register(machine, model.build());
        }

        return new HatchPair<>(definitions.get(0), definitions.get(1));
    }

    private static final int FLUID_HATCH_SLOT_X = 80;
    private static final int FLUID_HATCH_SLOT_Y = 40;

    public static HatchPair<FluidHatch> registerFluidHatches(String englishPrefix, String prefix, MachineCasing casing, int bucketCapacity) {
        List<MachineDefinition<FluidHatch>> definitions = new ArrayList<>(2);

        for (int iter = 0; iter < 2; ++iter) {
            boolean input = iter == 0;
            String machine = prefix + "_fluid_" + (input ? "input" : "output") + "_hatch";
            String englishName = englishPrefix + " Fluid" + (input ? " Input" : " Output") + " Hatch";
            var def = MachineRegistrationHelper.registerMachine(englishName, machine, bet -> {
                List<ConfigurableFluidStack> fluidStacks = Collections
                        .singletonList(input ? ConfigurableFluidStack.standardInputSlot(bucketCapacity * 1000L)
                                : ConfigurableFluidStack.standardOutputSlot(bucketCapacity * 1000L));
                MIInventory inventory = new MIInventory(Collections.emptyList(), fluidStacks, SlotPositions.empty(),
                        new SlotPositions.Builder().addSlot(FLUID_HATCH_SLOT_X, FLUID_HATCH_SLOT_Y).build());
                return new FluidHatch(bet, new MachineGuiParameters.Builder(machine, true).build(), input, !prefix.equals("bronze"), inventory);
            }, MachineBlockEntity::registerFluidApi);
            definitions.add(def);

            var model = new MachineModelProperties.Builder(casing);
            model.addOverlay("side", MI.id("block/machines/hatch_fluid/overlay_side"));
            model.addOverlay("output", MI.id("block/overlays/output_fluid"));
            model.addOverlay("fluid_auto", MI.id("block/overlays/fluid_auto"));
            MachineModelsToGenerate.register(machine, model.build());
        }

        return new HatchPair<>(definitions.get(0), definitions.get(1));
    }

    public static HatchPair<EnergyHatch> registerEnergyHatches(CableTier tier) {
        List<MachineDefinition<EnergyHatch>> definitions = new ArrayList<>(2);

        for (int iter = 0; iter < 2; ++iter) {
            boolean input = iter == 0;
            String machine = tier.name + "_energy_" + (input ? "input" : "output") + "_hatch";
            String englishName = tier.shortEnglishName + " Energy" + (input ? " Input" : " Output") + " Hatch";
            var def = MachineRegistrationHelper.registerMachine(englishName, machine,
                    bet -> new EnergyHatch(bet, new MachineGuiParameters.Builder(machine, false).build(), input, tier),
                    EnergyHatch::registerEnergyApi);
            definitions.add(def);

            var model = new MachineModelProperties.Builder(tier.casing);
            model.addOverlay("side", MI.id("block/machines/hatch_energy/overlay_side"));
            if (!input) {
                model.addOverlay("output", MI.id("block/overlays/output_energy"));
            }
            MachineModelsToGenerate.register(machine, model.build());
        }

        return new HatchPair<>(definitions.get(0), definitions.get(1));
    }
}
