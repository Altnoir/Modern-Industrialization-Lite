package aztech.milite.machines.guicomponents;

import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.MI;
import aztech.milite.nuclear.*;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.Unit;

import java.util.function.Supplier;
import java.util.List;
import java.util.Optional;

public record NuclearReactorGui(Supplier<Data> dataSupplier) implements GuiComponentServer<Unit, NuclearReactorGui.Data> {
    public static final Type<Unit, Data> TYPE = new Type<>(MI.id("nuclear_reactor_gui"), StreamCodec.unit(Unit.INSTANCE), Data.STREAM_CODEC);

    @Override
    public Unit params() {
        return Unit.INSTANCE;
    }

    @Override
    public Data extractData() {
        return dataSupplier.get();
    }

    @Override
    public Type<Unit, Data> getType() {
        return TYPE;
    }

    final private static double neutronsMax = 8192;

    public static double neutronColorScheme(double neutronNumber) {
        neutronNumber = Math.min(neutronNumber, neutronsMax);
        return Math.log(1 + 10 * neutronNumber) / Math.log(1 + 10 * neutronsMax);
    }

    public record Data(
            boolean valid,
            int gridSizeX, int gridSizeY,
            List<TileData> tilesData,
            double feProduction, double feFuelConsumption) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                Data::valid,
                ByteBufCodecs.VAR_INT,
                Data::gridSizeX,
                ByteBufCodecs.VAR_INT,
                Data::gridSizeY,
                TileData.STREAM_CODEC.apply(ByteBufCodecs.list()),
                Data::tilesData,
                ByteBufCodecs.DOUBLE,
                Data::feProduction,
                ByteBufCodecs.DOUBLE,
                Data::feFuelConsumption,
                Data::new);

        public int toIndex(int x, int y) {
            return toIndex(x, y, gridSizeY);
        }

        public static int toIndex(int x, int y, int sizeY) {
            return x * sizeY + y;
        }
    }

    /**
     * Wrapper record for {@link NuclearTileData} to have a suitable equals implementation.
     */
    public record TileData(Optional<NuclearTileData> data) {
        public static final StreamCodec<RegistryFriendlyByteBuf, TileData> STREAM_CODEC = StreamCodec.ofMember(NuclearTileData::write, NuclearTileData::read)
                .map(TileData::new, TileData::data);

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TileData(var odata) && NuclearTileData.areEquals(this.data, odata);
        }
    }
}
