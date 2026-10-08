package aztech.milite.machines.init;

import aztech.milite.MI;
import aztech.milite.MIFluids;
import aztech.milite.api.energy.CableTier;
import aztech.milite.datagen.model.MachineModelProperties;
import aztech.milite.datagen.model.MachineModelsToGenerate;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.blockentities.*;
import aztech.milite.machines.components.FluidItemConsumerComponent;
import aztech.milite.machines.models.MachineCasing;
import aztech.milite.machines.models.MachineCasings;

public class SingleBlockSpecialMachines {
    public static void init() {
        MachineRegistrationHelper.registerMachine("Bronze Boiler", "bronze_boiler", bet -> new BoilerMachineBlockEntity(bet, true),
                MachineBlockEntity::registerFluidApi, MachineBlockEntity::registerItemApi);
        MachineRegistrationHelper.registerMachine("Steel Boiler", "steel_boiler", bet -> new BoilerMachineBlockEntity(bet, false),
                MachineBlockEntity::registerFluidApi, MachineBlockEntity::registerItemApi);

        // TODO: register water pumps in REI?
        MachineRegistrationHelper.registerMachine("Bronze Water Pump", "bronze_water_pump", bet -> new SteamWaterPumpBlockEntity(bet, true),
                MachineBlockEntity::registerFluidApi);
        MachineRegistrationHelper.registerMachine("Steel Water Pump", "steel_water_pump", bet -> new SteamWaterPumpBlockEntity(bet, false),
                MachineBlockEntity::registerFluidApi);
        MachineRegistrationHelper.registerMachine("Electric Water Pump", "electric_water_pump", ElectricWaterPumpBlockEntity::new,
                MachineBlockEntity::registerFluidApi,
                ElectricWaterPumpBlockEntity::registerEnergyApi);

        registerEUStorage();

        registerSteamTurbine(CableTier.LV, 32, 16000);
        registerSteamTurbine(CableTier.MV, 128, 32000);
        registerSteamTurbine(CableTier.HV, 512, 64000);

        MachineRegistrationHelper.registerMachine("LV Diesel Generator", "lv_diesel_generator",
                bet -> new GeneratorMachineBlockEntity(bet, "lv_diesel_generator", false,
                        CableTier.LV, 4000, 16000,
                        FluidItemConsumerComponent.ofFluidFuels(64, 2)),
                MachineBlockEntity::registerFluidApi, GeneratorMachineBlockEntity::registerEnergyApi);

        MachineRegistrationHelper.registerMachine("MV Diesel Generator", "mv_diesel_generator",
                bet -> new GeneratorMachineBlockEntity(bet, "mv_diesel_generator", false,
                        CableTier.MV, 12000, 32000,
                        FluidItemConsumerComponent.ofFluidFuels(256, 2)),
                MachineBlockEntity::registerFluidApi, GeneratorMachineBlockEntity::registerEnergyApi);

        MachineRegistrationHelper.registerMachine("HV Diesel Generator", "hv_diesel_generator",
                bet -> new GeneratorMachineBlockEntity(bet, "hv_diesel_generator", false,
                        CableTier.HV, 60000, 64000,
                        FluidItemConsumerComponent.ofFluidFuels(1024, 2)),
                MachineBlockEntity::registerFluidApi, GeneratorMachineBlockEntity::registerEnergyApi);

        MachineRegistrationHelper.registerMachine("Configurable Chest", "configurable_chest", ConfigurableChestMachineBlockEntity::new,
                MachineBlockEntity::registerItemApi);

        MachineRegistrationHelper.registerMachine("Configurable Tank", "configurable_tank", ConfigurableTankMachineBlockEntity::new,
                MachineBlockEntity::registerFluidApi);

        MachineRegistrationHelper.registerMachine("Replicator", "replicator", ReplicatorMachineBlockEntity::new, MachineBlockEntity::registerFluidApi,
                MachineBlockEntity::registerItemApi);

        MachineRegistrationHelper.addModelsForTiers("water_pump", true, true, true, "bronze", "steel", "electric");
        MachineRegistrationHelper.addMachineModel("bronze_boiler", "boiler", MachineCasings.BRICKED_BRONZE, true, false, false);
        MachineRegistrationHelper.addMachineModel("steel_boiler", "boiler", MachineCasings.BRICKED_STEEL, true, false, false);
        addDieselGeneratorModel("lv_diesel_generator", CableTier.LV.casing);
        addDieselGeneratorModel("mv_diesel_generator", CableTier.MV.casing);
        addDieselGeneratorModel("hv_diesel_generator", CableTier.HV.casing);
        MachineRegistrationHelper.addMachineModel("configurable_chest", "", MachineCasings.STEEL_CRATE, false, false, false, false);
        MachineRegistrationHelper.addMachineModel("configurable_tank", "", MachineCasings.CONFIGURABLE_TANK, false, false, false, false);
        MachineRegistrationHelper.addMachineModel("replicator", "replicator", CableTier.SUPERCONDUCTOR.casing, true, false, true, true);
    }

    private static void addDieselGeneratorModel(String id, MachineCasing casing) {
        MachineModelsToGenerate.register(id, new MachineModelProperties.Builder(casing)
                .addOverlay("top", MI.id("block/machines/diesel_generator/overlay_top"))
                .addOverlay("top_active", MI.id("block/machines/diesel_generator/overlay_top_active"))
                .addOverlay("front", MI.id("block/machines/diesel_generator/overlay_front"))
                .addOverlay("front_active", MI.id("block/machines/diesel_generator/overlay_front_active"))
                .addOverlay("output", MI.id("block/overlays/output_energy"))
                .build());
    }

    private static void registerSteamTurbine(CableTier tier, int rf, int fluidCapacity) {
        String id = tier.name + "_steam_turbine";
        String englishName = tier.shortEnglishName + " Steam Turbine";
        MachineRegistrationHelper.registerMachine(englishName, id,
                bet -> new GeneratorMachineBlockEntity(bet, id, true,
                        tier, rf * 100L, fluidCapacity, rf,
                        MIFluids.STEAM, 1),
                MachineBlockEntity::registerFluidApi, GeneratorMachineBlockEntity::registerEnergyApi);

        MachineModelsToGenerate.register(id, new MachineModelProperties.Builder(tier.casing)
                .addOverlay("front", MI.id("block/machines/steam_turbine/overlay_front"))
                .addOverlay("front_active", MI.id("block/machines/steam_turbine/overlay_front_active"))
                .addOverlay("output", MI.id("block/overlays/output_energy"))
                .build());
    }

    private static void registerEUStorage() {
        for (CableTier tier : CableTier.allTiers()) {
            String id = tier.name + "_storage_unit";
            String englishName = tier.shortEnglishName + " Storage Unit";
            MachineRegistrationHelper.registerMachine(englishName, id, bet -> new StorageMachineBlockEntity(bet, tier, id, Math.min(100000L * tier.rf, Integer.MAX_VALUE)),
                    AbstractStorageMachineBlockEntity::registerEnergyApi);

            MachineModelsToGenerate.register(id, new MachineModelProperties.Builder(tier.casing)
                    .addOverlay("side", MI.id("block/machines/electric_storage/overlay_side"))
                    .addOverlay("output", MI.id("block/overlays/output_energy"))
                    .build());
        }
    }
}
