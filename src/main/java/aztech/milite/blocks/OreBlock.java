package aztech.milite.blocks;

import aztech.milite.materials.part.OrePart;
import aztech.milite.MIItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

// An MIBlock that converts a book into the guide book when right clicked
public class OreBlock extends Block {
    public final OrePart.OrePartParams params;
    public final String materialName;

    public OreBlock(BlockBehaviour.Properties settings, OrePart.OrePartParams params, String materialName) {
        super(settings);
        this.params = params;
        this.materialName = materialName;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack handStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
        if (handStack.getItem() == Items.BOOK) {
            handStack.shrink(1);
            player.getInventory().placeItemBackInInventory(new ItemStack(MIItem.GUIDE_BOOK));
            return ItemInteractionResult.sidedSuccess(world.isClientSide);
        }
        return super.useItemOn(handStack, state, world, pos, player, hand, hit);
    }

    @Override
    public int getExpDrop(BlockState state, LevelAccessor level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker,
            ItemStack tool) {
        return this.params.xpDropped().sample(level.getRandom());
    }
}
