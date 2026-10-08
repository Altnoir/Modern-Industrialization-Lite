package aztech.milite.machines.guicomponents;

import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.MI;
import aztech.milite.util.Rectangle;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class ProgressBar implements GuiComponentServer<ProgressBar.Params, Float> {
    public static final Type<Params, Float> TYPE = new Type<>(MI.id("progress_bar"), Params.STREAM_CODEC, ByteBufCodecs.FLOAT);

    private final Params params;
    private final Supplier<Float> progressSupplier;

    public ProgressBar(Params params, Supplier<Float> progressSupplier) {
        this.params = params;
        this.progressSupplier = progressSupplier;
    }

    @Override
    public Params params() {
        return params;
    }

    @Override
    public Float extractData() {
        return progressSupplier.get();
    }

    @Override
    public Type<Params, Float> getType() {
        return TYPE;
    }

    /**
     * @param progressBarType The real path will be
     *                        {@code milite:textures/gui/progress_bar/<progressBarType>.png}.
     *                        Must have a size of width x 2*height.
     */
    public record Params(int renderX, int renderY, String progressBarType, int width, int height, boolean isVertical) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Params> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Params::renderX,
                ByteBufCodecs.VAR_INT,
                Params::renderY,
                ByteBufCodecs.STRING_UTF8,
                Params::progressBarType,
                ByteBufCodecs.VAR_INT,
                Params::width,
                ByteBufCodecs.VAR_INT,
                Params::height,
                ByteBufCodecs.BOOL,
                Params::isVertical,
                Params::new);

        public Params(int renderX, int renderY, String progressBarType) {
            this(renderX, renderY, progressBarType, false);
        }

        public Params(int renderX, int renderY, String progressBarType, boolean isVertical) {
            this(renderX, renderY, progressBarType, 20, 20, isVertical);
        }

        public Params {
            if (width < 2 || height < 2) {
                throw new IllegalArgumentException("Width and height must be at least 2, currently " + width + " and " + height);
            }
        }

        public ResourceLocation getTextureId() {
            return MI.id("textures/gui/progress_bar/" + progressBarType + ".png");
        }

        public int textureHeight() {
            return 2 * height;
        }

        public Rectangle toRectangle() {
            return new Rectangle(renderX, renderY, width, height);
        }
    }
}
