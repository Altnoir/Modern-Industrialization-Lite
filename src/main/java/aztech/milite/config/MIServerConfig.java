package aztech.milite.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class MIServerConfig {
    public static final MIServerConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        var builder = new MIConfigBuilder();
        INSTANCE = new MIServerConfig(builder);
        SPEC = builder.build();
    }

    public final ModConfigSpec.BooleanValue spawnWithGuideBook;
    public final ModConfigSpec.BooleanValue respawnWithGuideBook;

    public final ModConfigSpec.BooleanValue compostableToPlantOil;
    public final ModConfigSpec.BooleanValue stonecutterToCuttingMachine;
    public final ModConfigSpec.IntValue steamPerWater;
    public final ModConfigSpec.BooleanValue ftbQuestsIntegration;

    public final ModConfigSpec.BooleanValue machineSounds;
    public final ModConfigSpec.DoubleValue machineSoundVolume;

    private MIServerConfig(MIConfigBuilder builder) {
        builder.pushSection("compat", "Mod Compatibility");
        this.ftbQuestsIntegration = builder.start("ftbQuestsIntegration",
                "FTB Quests integration",
                "Enable machine production and built multiblock progress for FTB Quests, if present.")
                .define("ftbQuestsIntegration", ConfigMigration.ftbQuestsDefault());
        builder.popSection();

        this.spawnWithGuideBook = builder.start("spawnWithGuideBook",
                "Spawn with guidebook",
                "Grant guidebook the first time a player joins the server.")
                .define("spawnWithGuideBook", true);
        this.respawnWithGuideBook = builder.start("respawnWithGuideBook",
                "Respawn with guidebook",
                "Grant guidebook when a player respawns after death.")
                .define("respawnWithGuideBook", true);

        this.steamPerWater = builder.start("steamPerWater",
                "Steam per Water",
                "How much steam should be produced per mb of water that is boiled.")
                .defineInRange("steamPerWater", 16, 1, 1000);

        builder.pushSection("sounds", "Sounds");
        this.machineSounds = builder.start("machineSounds",
                "Machine sounds",
                "Play a working sound while a machine is running.")
                .define("machineSounds", true);
        this.machineSoundVolume = builder.start("machineSoundVolume",
                "Machine sound volume",
                "Volume of the machine working sounds.")
                .defineInRange("machineSoundVolume", 1.0, 0.0, 5.0);
        builder.popSection();

        builder.pushSection("recipes", "Recipes");
        this.compostableToPlantOil = builder.start("compostableToPlantOil",
                "Compostable plant oil recipes",
                "Generate plant oil recipes in the centrifuge for all compostable items.")
                .define("compostableToPlantOil", true);
        this.stonecutterToCuttingMachine = builder.start("stonecutterToCuttingMachine",
                "Stonecutter cutting machine recipes",
                "Generate cutting machine recipes for all stonecutter recipes.")
                .define("stonecutterToCuttingMachine", true);
        builder.popSection();
    }
}
