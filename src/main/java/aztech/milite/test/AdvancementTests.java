package aztech.milite.test;

import aztech.milite.MI;
import aztech.milite.advancement.BuiltMultiblockTrigger;
import aztech.milite.test.framework.MIGameTest;
import aztech.milite.test.framework.MIGameTestHelper;

import java.util.List;
import java.util.Optional;

public class AdvancementTests {
    private static final List<String> MULTIBLOCKS = List.of(
            "distillation_tower", "electric_blast_furnace", "electric_quarry", "fusion_reactor", "heat_exchanger",
            "implosion_compressor", "kanthal_coil", "large_diesel_generator", "large_steam_boiler", "large_steam_turbine",
            "nuclear_reactor", "oil_drilling_rig", "plasma_turbine", "pressurizer", "steam_quarry", "vacuum_freezer");

    @MIGameTest
    public void multiblocksUseTheBuiltMultiblockTrigger(MIGameTestHelper helper) {
        for (var id : MULTIBLOCKS) {
            var predicate = predicate(helper, id);
            if (predicate.isEmpty()) {
                return;
            }
            if (!predicate.get().id().equals(MI.id(machineId(id)))) {
                helper.fail("advancement " + id + " is triggered by " + predicate.get().id());
                return;
            }
        }
        helper.succeed();
    }

    private static String machineId(String advancementId) {
        return advancementId.equals("kanthal_coil") ? "electric_blast_furnace" : advancementId;
    }

    @MIGameTest
    public void onlyTheKanthalAdvancementRequiresAShape(MIGameTestHelper helper) {
        var kanthal = predicate(helper, "kanthal_coil");
        if (kanthal.isEmpty()) {
            return;
        }
        if (!kanthal.get().shape().equals(Optional.of("kanthal_coil"))) {
            helper.fail("the kanthal coil advancement requires shape " + kanthal.get().shape());
            return;
        }
        if (!kanthal.get().matches(MI.id("electric_blast_furnace"), "kanthal_coil")) {
            helper.fail("the kanthal coil advancement does not match the kanthal coil shape");
            return;
        }
        if (kanthal.get().matches(MI.id("electric_blast_furnace"), null) || kanthal.get().matches(MI.id("electric_blast_furnace"), "cupronickel_coil")) {
            helper.fail("the kanthal coil advancement matches other shapes");
            return;
        }

        for (var id : MULTIBLOCKS) {
            if (id.equals("kanthal_coil")) {
                continue;
            }
            var predicate = predicate(helper, id);
            if (predicate.isEmpty()) {
                return;
            }
            if (predicate.get().shape().isPresent()) {
                helper.fail("advancement " + id + " requires shape " + predicate.get().shape());
                return;
            }
            if (!predicate.get().matches(MI.id(id), null) || predicate.get().matches(MI.id("steel_packer"), null)) {
                helper.fail("advancement " + id + " has a broken multiblock condition");
                return;
            }
        }
        helper.succeed();
    }

    private static Optional<BuiltMultiblockTrigger.MultiblockPredicate> predicate(MIGameTestHelper helper, String id) {
        var holder = helper.getLevel().getServer().getAdvancements().get(MI.id(id));
        if (holder == null) {
            helper.fail("missing advancement " + id);
            return Optional.empty();
        }
        var criterion = holder.value().criteria().get("buildMultiblock");
        if (criterion == null) {
            helper.fail("advancement " + id + " does not use the built multiblock trigger");
            return Optional.empty();
        }
        if (!(criterion.triggerInstance() instanceof BuiltMultiblockTrigger.TriggerInstance instance)) {
            helper.fail("advancement " + id + " has trigger " + criterion.triggerInstance());
            return Optional.empty();
        }
        if (instance.multiblock().isEmpty()) {
            helper.fail("advancement " + id + " has no multiblock condition");
            return Optional.empty();
        }
        return instance.multiblock();
    }
}
