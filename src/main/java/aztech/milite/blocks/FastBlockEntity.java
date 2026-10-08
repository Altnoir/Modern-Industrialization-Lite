package aztech.milite.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Special BlockEntity that tries NOT to update comparators when markDirty is called, making it VERY FAST.
 */
public abstract class FastBlockEntity extends BlockEntity {
    public FastBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected boolean shouldSkipComparatorUpdate() {
        return true;
    }

    @Override
    public void setChanged() {
        if (shouldSkipComparatorUpdate()) {
            if (this.level != null) {
                this.level.blockEntityChanged(this.worldPosition);
            }
        } else {
            super.setChanged();
        }
    }

    public void sync() {
        if (!(level instanceof ServerLevel serverWorld)) {
            throw new IllegalStateException("Cannot call sync() on the logical client! Did you check world.isClient first?");
        }

        serverWorld.getChunkSource().blockChanged(this.getBlockPos());
    }
}
