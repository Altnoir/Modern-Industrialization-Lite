package aztech.milite.machines.guicomponents;

import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.MI;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.Unit;

import java.util.function.Supplier;

public record LargeTankFluidDisplay(
        Supplier<Data> fluidDataSupplier) implements GuiComponentServer<Unit, LargeTankFluidDisplay.Data> {
    public static final Type<Unit, Data> TYPE = new Type<>(MI.id("large_tank_fluid_display"), StreamCodec.unit(Unit.INSTANCE), Data.STREAM_CODEC);

    @Override
    public Unit params() {
        return Unit.INSTANCE;
    }

    @Override
    public Data extractData() {
        return fluidDataSupplier.get();
    }

    @Override
    public Type<Unit, Data> getType() {
        return TYPE;
    }

    public record Data(FluidVariant fluid, long amount, long capacity) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                FluidVariant.STREAM_CODEC,
                Data::fluid,
                ByteBufCodecs.VAR_LONG,
                Data::amount,
                ByteBufCodecs.VAR_LONG,
                Data::capacity,
                Data::new);
    }
}
