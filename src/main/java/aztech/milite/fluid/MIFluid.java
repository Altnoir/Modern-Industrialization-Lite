package aztech.milite.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.function.Supplier;

/**
 * A fluid that can only be used for crafting, i.e. not be placed in the world.
 */
public class MIFluid extends Fluid {
    private final Supplier<MIFluidBlock> block;
    private final ItemLike bucketItem;
    private final Supplier<MIFluidType> fluidType;
    public final int color;

    public MIFluid(Supplier<MIFluidBlock> block, ItemLike bucketItem, Supplier<MIFluidType> fluidType, int color) {
        this.block = block;
        this.bucketItem = bucketItem;
        this.fluidType = fluidType;
        this.color = color;
    }

    @Override
    public Item getBucket() {
        return bucketItem.asItem();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter world, BlockPos pos, Fluid fluid, Direction direction) {
        return true;
    }

    @Override
    protected Vec3 getFlow(BlockGetter world, BlockPos pos, FluidState state) {
        return Vec3.ZERO;
    }

    @Override
    public int getTickDelay(LevelReader world) {
        return 0;
    }

    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Override
    public float getHeight(FluidState state, BlockGetter world, BlockPos pos) {
        return 0;
    }

    @Override
    public float getOwnHeight(FluidState state) {
        return 0;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return block.get().defaultBlockState();
    }

    @Override
    public boolean isSource(FluidState state) {
        return true;
    }

    @Override
    public int getAmount(FluidState state) {
        return 0;
    }

    @Override
    public VoxelShape getShape(FluidState state, BlockGetter world, BlockPos pos) {
        return null;
    }

    @Override
    public FluidType getFluidType() {
        return fluidType.get();
    }
}
