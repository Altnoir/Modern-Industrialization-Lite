package aztech.milite.machines.guicomponents;

import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.MI;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;

import java.util.function.Supplier;

public class MachineProblemsDisplay implements GuiComponentServer<Unit, MachineProblemsDisplay.Data> {
    public static final Type<Unit, MachineProblemsDisplay.Data> TYPE = new Type<>(MI.id("machine_problems_display"), StreamCodec.unit(Unit.INSTANCE), MachineProblemsDisplay.Data.STREAM_CODEC);

    private final Supplier<Boolean> hasProblemsSupplier;

    public MachineProblemsDisplay(Supplier<Boolean> hasProblemsSupplier) {
        this.hasProblemsSupplier = hasProblemsSupplier;
    }

    @Override
    public Unit params() {
        return Unit.INSTANCE;
    }

    @Override
    public Data extractData() {
        return new Data(hasProblemsSupplier.get());
    }

    @Override
    public Type<Unit, Data> getType() {
        return TYPE;
    }

    public record Data(boolean hasProblems) {
        public static final StreamCodec<ByteBuf, Data> STREAM_CODEC = ByteBufCodecs.BOOL.map(Data::new, Data::hasProblems);
    }
}
