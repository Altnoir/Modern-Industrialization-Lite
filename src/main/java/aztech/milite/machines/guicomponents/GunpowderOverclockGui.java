package aztech.milite.machines.guicomponents;

import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.MI;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.function.Supplier;

public record GunpowderOverclockGui(Params params,
                                    Supplier<Integer> remTickSupplier) implements GuiComponentServer<GunpowderOverclockGui.Params, Integer> {
    public static final Type<Params, Integer> TYPE = new Type<>(MI.id("gunpowder_overclock_gui"), Params.STREAM_CODEC, ByteBufCodecs.VAR_INT);

    @Override
    public Integer extractData() {
        return remTickSupplier.get();
    }

    @Override
    public Type<Params, Integer> getType() {
        return TYPE;
    }

    public record Params(int renderX, int renderY) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Params> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Params::renderX,
                ByteBufCodecs.VAR_INT,
                Params::renderY,
                Params::new);
    }
}
