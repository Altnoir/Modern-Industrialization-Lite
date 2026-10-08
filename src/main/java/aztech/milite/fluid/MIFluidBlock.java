package aztech.milite.fluid;

import aztech.milite.util.FluidHelper;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * Allows the transfer API to get the colored name of the fluid by calling fluid.getBlockState().getName().
 */
public class MIFluidBlock extends Block {
    private final int color;

    public MIFluidBlock(int color) {
        // .air() to make `FluidType#canBePlacedInLevel` return false
        super(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).replaceable().pushReaction(PushReaction.DESTROY).liquid().air());
        this.color = FluidHelper.getColorMinLuminance(color);
    }

    public int getColor() {
        return color;
    }

    @Override
    public MutableComponent getName() {
        return super.getName().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
    }
}
