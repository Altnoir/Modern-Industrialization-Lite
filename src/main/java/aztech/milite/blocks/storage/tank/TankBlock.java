package aztech.milite.blocks.storage.tank;

import aztech.milite.blocks.storage.AbstractStorageBlock;
import aztech.milite.blocks.storage.StorageBehaviour;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.util.MobSpawning;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class TankBlock extends AbstractStorageBlock<FluidVariant> implements EntityBlock {
    public TankBlock(EntityBlock factory, StorageBehaviour<FluidVariant> behaviour) {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).destroyTime(4.0f).noOcclusion().isValidSpawn(MobSpawning.NO_SPAWN)
                .isRedstoneConductor(Blocks::never), factory,
                behaviour);
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
        if (((AbstractTankBlockEntity) world.getBlockEntity(pos)).onPlayerUse(player)) {
            return ItemInteractionResult.sidedSuccess(world.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
