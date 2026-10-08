package aztech.milite.config;

import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class MIClientConfig {
    public static final MIClientConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        var builder = new MIConfigBuilder();
        INSTANCE = new MIClientConfig(builder);
        SPEC = builder.build();
    }

    public final ModConfigSpec.BooleanValue missingRecipeViewerMessage;

    public final ModConfigSpec.IntValue armorHudYPosition;
    public final ModConfigSpec.BooleanValue barrelContentRendering;
    public final ModConfigSpec.BooleanValue hatchPlacementOverlay;
    public final ModConfigSpec.IntValue maxMultiblockHighlightOverlayRange;
    public final ModConfigSpec.BooleanValue interMachineConnectedTextures;

    public final ModConfigSpec.BooleanValue fuelTooltips;
    public final ModConfigSpec.BooleanValue itemTagTooltips;
    public final ModConfigSpec.BooleanValue defaultOreGenTooltips;

    private MIClientConfig(MIConfigBuilder builder) {
        builder.pushSection("messages", "Messages");
        this.missingRecipeViewerMessage = builder.start("missingRecipeViewerMessage",
                "Missing recipe viewer message",
                "Enable login message when EMI, JEI and REI are all missing.")
                .define("missingRecipeViewerMessage", true);
        builder.popSection();

        builder.pushSection("rendering", "Rendering");
        this.armorHudYPosition = builder.start("armorHudYPosition",
                "Armor HUD vertical position",
                "Space between the top of the screen and the jetpack/GraviChestPlate overlay text.")
                .defineInRange("armorHudYPosition", 4, 0, 10000);
        this.barrelContentRendering = builder.start("barrelContentRendering",
                "Barrel content rendering",
                "Enable rendering of barrel content: item icon, item amount, and item name.")
                .define("barrelContentRendering", true);
        this.hatchPlacementOverlay = builder.start("hatchPlacementOverlay",
                "Hatch placement overlay",
                "Show valid hatch positions in multiblocks when holding a hatch.")
                .define("hatchPlacementOverlay", true);
        this.maxMultiblockHighlightOverlayRange = builder.start("maxMultiblockHighlightOverlayRange",
                "Multiblock placement overlay range",
                "Show valid positions in multiblocks when holding a hatch or wrench while within this many blocks. Set to 0 to disable.")
                .defineInRange("maxMultiblockHighlightOverlayRange", 32, 0, Integer.MAX_VALUE);
        this.interMachineConnectedTextures = builder.start("interMachineConnectedTextures",
                "Inter-machine connected textures",
                "Enable connected textures between machines that have the same casing. (Requires a suitable resource pack)")
                .define("interMachineConnectedTextures", false);
        builder.popSection();

        builder.pushSection("tooltips", "Tooltips");
        this.fuelTooltips = builder.start("fuelTooltips",
                "Fuel tooltips",
                "Add the total EU value of fuel items to their tooltips.")
                .define("fuelTooltips", true);
        this.itemTagTooltips = builder.start("itemTagTooltips",
                "Item tag tooltips",
                "Add item tags to item tooltips.")
                .define("itemTagTooltips", !FMLEnvironment.production);
        this.defaultOreGenTooltips = builder.start("defaultOreGenTooltips",
                "Ore generation tooltips",
                "Enable the default ore generation tooltips.",
                "These tooltips show how each MI ore generates, based on the default ore generation settings in MI.",
                "Set this to false if you change the ore features in a datapack.")
                .define("defaultOreGenTooltips", true);
        builder.popSection();
    }
}
