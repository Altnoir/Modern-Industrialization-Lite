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
    private final LongSupplier currentFeGeneration;
    private final long maxFeGeneration;

    public GeneratorMultiblockGui(Supplier<Boolean> isShapeValid, LongSupplier currentFeGeneration, long maxFeGeneration) {
        this.isShapeValid = isShapeValid;
        this.currentFeGeneration = currentFeGeneration;
        this.maxFeGeneration = maxFeGeneration;
    }

    @Override
    public Unit params() {
        return Unit.INSTANCE;
    }

    @Override
    public Data extractData() {
        return new Data(isShapeValid.get(), currentFeGeneration.getAsLong(), maxFeGeneration);
    }

    @Override
    public Type<Unit, Data> getType() {
        return TYPE;
    }

    public record Data(boolean isShapeValid, long currentFeGeneration, long maxFeGeneration) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                Data::isShapeValid,
                ByteBufCodecs.VAR_LONG,
                Data::currentFeGeneration,
                ByteBufCodecs.VAR_LONG,
                Data::maxFeGeneration,
                Data::new);
    }
}
