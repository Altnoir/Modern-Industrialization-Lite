package aztech.milite.machines.guicomponents;

import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.MI;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

/**
 * Supports both auto-extract and auto-insert. Auto-insert is just a GUI change,
 * but the logic stays the same.
 */
public class AutoExtract implements GuiComponentServer<AutoExtract.Params, AutoExtract.Data> {
    public static final Type<Params, Data> TYPE = new Type<>(MI.id("auto_extract"), Params.STREAM_CODEC, Data.STREAM_CODEC);

    private final OrientationComponent orientation;
    private final Display display;

    public AutoExtract(OrientationComponent orientation, Display display) {
        this.orientation = orientation;
        this.display = display;
    }

    public AutoExtract(OrientationComponent orientation, boolean displayAsInsert) {
        this(orientation, displayAsInsert ? Display.INSERT : Display.EXTRACT);
    }

    public AutoExtract(OrientationComponent orientation) {
        this(orientation, false);
    }

    @Override
    public Params params() {
        return new Params(display, orientation.params.hasExtractItems, orientation.params.hasExtractFluids);
    }

    @Override
    public Data extractData() {
        return new Data(orientation.extractItems, orientation.extractFluids);
    }

    @Override
    public Type<Params, Data> getType() {
        return TYPE;
    }

    public OrientationComponent getOrientation() {
        return orientation;
    }

    public enum Display {
        EXTRACT,
        INSERT,
        BOTH,
    }

    public record Params(Display display, boolean hasExtractItems, boolean hasExtractFluids) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Params> STREAM_CODEC = StreamCodec.composite(
                NeoForgeStreamCodecs.enumCodec(Display.class),
                Params::display,
                ByteBufCodecs.BOOL,
                Params::hasExtractItems,
                ByteBufCodecs.BOOL,
                Params::hasExtractFluids,
                Params::new);
    }

    public record Data(boolean extractItems, boolean extractFluids) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                Data::extractItems,
                ByteBufCodecs.BOOL,
                Data::extractFluids,
                Data::new);
    }
}
