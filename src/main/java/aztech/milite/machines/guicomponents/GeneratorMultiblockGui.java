package aztech.milite.machines.guicomponents;

import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.MI;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.Unit;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class GeneratorMultiblockGui implements GuiComponentServer<Unit, GeneratorMultiblockGui.Data> {
    public static final Type<Unit, Data> TYPE = new Type<>(MI.id("generator_multiblock_gui"), StreamCodec.unit(Unit.INSTANCE), Data.STREAM_CODEC);

    private final Supplier<Boolean> isShapeValid;
    private final LongSupplier currentRfGeneration;
    private final long maxRfGeneration;

    public GeneratorMultiblockGui(Supplier<Boolean> isShapeValid, LongSupplier currentRfGeneration, long maxRfGeneration) {
        this.isShapeValid = isShapeValid;
        this.currentRfGeneration = currentRfGeneration;
        this.maxRfGeneration = maxRfGeneration;
    }

    @Override
    public Unit params() {
        return Unit.INSTANCE;
    }

    @Override
    public Data extractData() {
        return new Data(isShapeValid.get(), currentRfGeneration.getAsLong(), maxRfGeneration);
    }

    @Override
    public Type<Unit, Data> getType() {
        return TYPE;
    }

    public record Data(boolean isShapeValid, long currentRfGeneration, long maxRfGeneration) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                Data::isShapeValid,
                ByteBufCodecs.VAR_LONG,
                Data::currentRfGeneration,
                ByteBufCodecs.VAR_LONG,
                Data::maxRfGeneration,
                Data::new);
    }
}
