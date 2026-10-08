package aztech.milite.machines.guicomponents;

import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.MI;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.function.Supplier;

public class TemperatureBar implements GuiComponentServer<TemperatureBar.Params, Integer> {
    public static final Type<Params, Integer> TYPE = new Type<>(MI.id("temperature_bar"), Params.STREAM_CODEC, ByteBufCodecs.VAR_INT);

    private final Params params;
    private final Supplier<Integer> temperatureSupplier;

    public TemperatureBar(Params params, Supplier<Integer> temperatureSupplier) {
        this.params = params;
        this.temperatureSupplier = temperatureSupplier;
    }

    @Override
    public Params params() {
        return params;
    }

    @Override
    public Integer extractData() {
        return temperatureSupplier.get();
    }

    @Override
    public Type<Params, Integer> getType() {
        return TYPE;
    }

    public record Params(int renderX, int renderY, int temperatureMax) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Params> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Params::renderX,
                ByteBufCodecs.VAR_INT,
                Params::renderY,
                ByteBufCodecs.VAR_INT,
                Params::temperatureMax,
                Params::new);
    }
}
