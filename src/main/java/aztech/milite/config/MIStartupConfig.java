package aztech.milite.config;

import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class MIStartupConfig {
    public static final MIStartupConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        var builder = new MIConfigBuilder();
        INSTANCE = new MIStartupConfig(builder);
        SPEC = builder.build();
    }

    public final ModConfigSpec.BooleanValue almostUnifiedIntegration;

    public final ModConfigSpec.BooleanValue datagenOnStartup;
    public final ModConfigSpec.BooleanValue loadRuntimeGeneratedResources;

    public final ModConfigSpec.BooleanValue transactionCallerName;
    // These should ideally be moved to the server config one day.
    public final ModConfigSpec.BooleanValue debugCommands;
    public final ModConfigSpec.IntValue maxDistillationTowerHeight;
    public final ModConfigSpec.BooleanValue defaultIndustrialistTrades;
    public final ModConfigSpec.BooleanValue validateRecipeLevels;

    private MIStartupConfig(MIConfigBuilder builder) {
        builder.pushSection("compat", "Mod Compatibility");
        this.almostUnifiedIntegration = builder.start("almostUnifiedIntegration",
                "Almost Unified integration",
                "Enable the Almost Unified integration, if present.")
                .gameRestart()
                .define("almostUnifiedIntegration", true);
        builder.popSection();

        builder.pushSection("datagen", "Runtime Datagen");
        this.datagenOnStartup = builder.start("datagenOnStartup",
                "Datagen on startup",
                "Run MI runtime datagen on startup.")
                .gameRestart()
                .define("datagenOnStartup", false);
        this.loadRuntimeGeneratedResources = builder.start("loadRuntimeGeneratedResources",
                "Load generated resources",
                "Additionally load resources in milite/generated_resources.")
                .gameRestart()
                .define("loadRuntimeGeneratedResources", false);
        builder.popSection();

        this.transactionCallerName = builder.start("transactionCallerName",
                "Include Transaction Caller Name",
                "Enable to have transfer transactions include the name of the class that called it.",
                "Note that this comes at the cost of performance. It is recommended to not use this outside of debugging.")
                .define("transactionCallerName", !FMLEnvironment.production);
        this.debugCommands = builder.start("debugCommands",
                "Debug commands",
                "Enable UNSUPPORTED and DANGEROUS debug commands.")
                .define("debugCommands", !FMLEnvironment.production);
        this.maxDistillationTowerHeight = builder.start("maxDistillationTowerHeight",
                "Max distillation tower height",
                "Maximum height of the distillation tower multiblock.")
                .gameRestart()
                .defineInRange("maxDistillationTowerHeight", 9, 1, 32);
        this.defaultIndustrialistTrades = builder.start("defaultIndustrialistTrades",
                "Default Industrialist trades",
                "Enable the default trades from the Industrialist villager provided by MI.",
                "Disable this to provide your own set of trades.")
                .define("defaultIndustrialistTrades", true);

        builder.pushSection("debug", "Debugging");
        this.validateRecipeLevels = builder.start("validateRecipeLevels",
                "Validate recipe levels",
                "On datapack load, check every machine recipe against the levels the machines of its type can reach, and log the ones nothing can craft.",
                "This only ever logs a warning, it never changes or removes a recipe. Turn it off if the checks get in the way.")
                .gameRestart()
                .define("validateRecipeLevels", !FMLEnvironment.production);
        builder.popSection();
    }
}
