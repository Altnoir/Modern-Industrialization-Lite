package aztech.milite.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

public interface DynamicToolItem {
    ItemStack SHEAR_STACK = new ItemStack(Items.SHEARS, 1);

    @ApiStatus.NonExtendable
    default boolean isSupportedBlock(ItemStack stack, BlockState state) {
        return stack.is(ItemTags.AXES) && state.is(BlockTags.MINEABLE_WITH_AXE)
                || stack.is(ItemTags.PICKAXES) && state.is(BlockTags.MINEABLE_WITH_PICKAXE)
                || stack.is(ItemTags.SHOVELS) && state.is(BlockTags.MINEABLE_WITH_SHOVEL)
                || stack.is(ItemTags.HOES) && state.is(BlockTags.MINEABLE_WITH_HOE)
                || stack.is(ItemTags.SWORDS) && state.is(BlockTags.SWORD_EFFICIENT);
        // TODO NEO
//                || stack.is(Tags.Items.SHEARS) &&
//                        (state.is(FabricMineableTags.SHEARS_MINEABLE) || Items.SHEARS.getDestroySpeed(
//                                SHEAR_STACK, state) > 1.0f)
    }
}
