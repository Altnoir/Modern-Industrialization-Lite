package aztech.milite.blocks.storage;

import aztech.milite.thirdparty.fabrictransfer.api.storage.StorageUtil;
import aztech.milite.thirdparty.fabrictransfer.api.storage.TransferVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class AbstractStorageBlock<T extends TransferVariant<?>> extends Block implements EntityBlock {
    public final EntityBlock factory;
    public final StorageBehaviour<T> behavior;

    public AbstractStorageBlock(Properties settings, EntityBlock factory, StorageBehaviour<T> behaviour) {
        super(settings);
        this.factory = factory;
        this.behavior = behaviour;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return factory.newBlockEntity(pos, state);
    }

    protected ItemStack getStack(@Nullable BlockEntity entity) {
        var storageBlockEntity = (AbstractStorageBlockEntity<?>) entity;
        ItemStack stack = new ItemStack(asItem());
        if (storageBlockEntity != null && (!storageBlockEntity.isEmpty() || storageBlockEntity.isLocked())) {
            storageBlockEntity.saveToItem(stack, storageBlockEntity.getLevel().registryAccess());
        }
        return stack;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(getStack(builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY)));
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state) {
        return getStack(world.getBlockEntity(pos));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return !behavior.isCreative();
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof AbstractStorageBlockEntity<?> storageBlockEntity) {
            return StorageUtil.calculateComparatorOutput(storageBlockEntity);
        }
        return 0;
    }
}
