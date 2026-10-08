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

    private MIServerConfig(MIConfigBuilder builder) {
        this.spawnWithGuideBook = builder.start("spawnWithGuideBook",
                "Spawn with guidebook",
                "Grant guidebook the first time a player joins the server.")
                .define("spawnWithGuideBook", true);
        this.respawnWithGuideBook = builder.start("respawnWithGuideBook",
                "Respawn with guidebook",
                "Grant guidebook when a player respawns after death.")
                .define("respawnWithGuideBook", true);

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
