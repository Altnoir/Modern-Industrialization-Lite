package aztech.milite.machines.guicomponents;

import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.MI;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public record EnergyBar(Params params, Supplier<Long> feSupplier,
                        Supplier<Long> maxFeSupplier) implements GuiComponentServer<EnergyBar.Params, EnergyBar.Data> {
    public static final Type<Params, Data> TYPE = new Type<>(MI.id("energy_bar"), Params.STREAM_CODEC, Data.STREAM_CODEC);

    @Override
    public Data extractData() {
        return new Data(feSupplier.get(), maxFeSupplier.get());
    }

    @Override
    public Type<Params, Data> getType() {
        return TYPE;
    }

    public record Params(int renderX, int renderY) {
        public static final StreamCodec<FriendlyByteBuf, Params> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Params::renderX,
                ByteBufCodecs.VAR_INT,
                Params::renderY,
                Params::new);
    }

    public record Data(long fe, long maxFe) {
        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_LONG,
                Data::fe,
                ByteBufCodecs.VAR_LONG,
                Data::maxFe,
                Data::new);
    }
}
