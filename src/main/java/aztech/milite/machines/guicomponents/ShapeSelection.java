package aztech.milite.machines.guicomponents;

import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.MI;
import aztech.milite.MIText;
import com.google.common.base.Preconditions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.List;
import java.util.stream.IntStream;

public class ShapeSelection implements GuiComponentServer<ShapeSelection.Params, List<Integer>> {
    public static final Type<Params, List<Integer>> TYPE = new Type<>(
            MI.id("shape_selection"),
            Params.STREAM_CODEC,
            ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()));

    public final Behavior behavior;
    private final Params params;

    public ShapeSelection(Behavior behavior, LineInfo... lines) {
        this(behavior, MIText.ShapeSelectionTitle.text(), MIText.ShapeSelectionDescription.text(), lines);
    }

    public ShapeSelection(Behavior behavior, Component title, Component description, LineInfo... lines) {
        Preconditions.checkArgument(lines.length > 0);

        this.behavior = behavior;
        this.params = new Params(title, description, List.of(lines));
    }

    @Override
    public Params params() {
        return params;
    }

    @Override
    public List<Integer> extractData() {
        return IntStream.range(0, params.lines().size()).map(behavior::getCurrentIndex).boxed().toList();
    }

    @Override
    public Type<Params, List<Integer>> getType() {
        return TYPE;
    }

    /**
     * @param title       the tooltip title of the slider
     * @param description the tooltip below it
     * @param lines       one slider per line
     */
    public record Params(Component title, Component description, List<LineInfo> lines) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Params> STREAM_CODEC = StreamCodec.composite(
                ComponentSerialization.STREAM_CODEC,
                Params::title,
                ComponentSerialization.STREAM_CODEC,
                Params::description,
                LineInfo.STREAM_CODEC.apply(ByteBufCodecs.list()),
                Params::lines,
                Params::new);
    }

    public interface Behavior {
        /**
         * @param delta +1 if clicked on right button, -1 if clicked on left button
         */
        void handleClick(int clickedLine, int delta);

        int getCurrentIndex(int line);
    }

    public record LineInfo(List<Component> translations, List<Integer> numbers) {
        public static final StreamCodec<RegistryFriendlyByteBuf, LineInfo> STREAM_CODEC = StreamCodec.composite(
                ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()),
                LineInfo::translations,
                ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()),
                LineInfo::numbers,
                LineInfo::new);

        public int numValues() {
            return translations.size();
        }

        public boolean hasNumbers() {
            return numbers.size() == translations.size();
        }

        public int number(int index) {
            return numbers.get(index);
        }
    }
}
