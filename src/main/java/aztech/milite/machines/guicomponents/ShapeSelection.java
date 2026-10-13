package aztech.milite.machines.guicomponents;

import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.MI;
import com.google.common.base.Preconditions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.List;
import java.util.stream.IntStream;

public class ShapeSelection implements GuiComponentServer<List<ShapeSelection.LineInfo>, List<Integer>> {
    public static final Type<List<ShapeSelection.LineInfo>, List<Integer>> TYPE = new Type<>(
            MI.id("shape_selection"),
            LineInfo.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()));

    public final Behavior behavior;
    private final List<LineInfo> lines;

    public ShapeSelection(Behavior behavior, LineInfo... lines) {
        Preconditions.checkArgument(lines.length > 0);

        this.behavior = behavior;
        this.lines = List.of(lines);
    }

    @Override
    public List<LineInfo> params() {
        return lines;
    }

    @Override
    public List<Integer> extractData() {
        return IntStream.range(0, lines.size()).map(behavior::getCurrentIndex).boxed().toList();
    }

    @Override
    public Type<List<LineInfo>, List<Integer>> getType() {
        return TYPE;
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
