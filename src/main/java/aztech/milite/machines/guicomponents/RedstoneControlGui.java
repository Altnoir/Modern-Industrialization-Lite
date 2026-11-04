package aztech.milite.machines.guicomponents;

import aztech.milite.MI;
import aztech.milite.machines.components.RedstoneControlComponent;
import aztech.milite.machines.gui.GuiComponentServer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;

public class RedstoneControlGui implements GuiComponentServer<Unit, RedstoneControlGui.Data> {
    public static final Type<Unit, Data> TYPE = new Type<>(MI.id("redstone_control_gui"), StreamCodec.unit(Unit.INSTANCE),
            Data.STREAM_CODEC);

    private final RedstoneControlComponent redstoneControl;

    public RedstoneControlGui(RedstoneControlComponent redstoneControl) {
        this.redstoneControl = redstoneControl;
    }

    @Override
    public Unit params() {
        return Unit.INSTANCE;
    }

    @Override
    public Data extractData() {
        return new Data(redstoneControl.isEnabled(), redstoneControl.requiresLowSignal());
    }

    @Override
    public Type<Unit, Data> getType() {
        return TYPE;
    }

    public RedstoneControlComponent getComponent() {
        return redstoneControl;
    }

    public record Data(boolean enabled, boolean requiresLowSignal) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                Data::enabled,
                ByteBufCodecs.BOOL,
                Data::requiresLowSignal,
                Data::new);
    }
}
