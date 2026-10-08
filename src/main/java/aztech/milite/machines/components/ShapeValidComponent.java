package aztech.milite.machines.components;

import aztech.milite.machines.MachineComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

import java.util.HashSet;
import java.util.Set;

/**
 * Syncing whether the multiblock shape is currently valid with the clients, to
 * decide if the BER should display something or not.
 */
public class ShapeValidComponent implements MachineComponent.ClientOnly {
    private boolean lastShapeValid = false;
    public boolean shapeValid = false;

    // Used to communicate what block entities are mismatching to the client, since it does not have full block entity awareness like the server does
    private Set<BlockPos> lastMismatchingBlockEntities = new HashSet<>();
    private final Set<BlockPos> mismatchingBlockEntities = new HashSet<>();

    public boolean isBlockEntityMatchingAt(BlockPos pos) {
        return !mismatchingBlockEntities.contains(pos);
    }

    public void clearMismatchingBlockEntities() {
        mismatchingBlockEntities.clear();
    }

    public void addMismatchingBlockEntity(BlockPos pos) {
        mismatchingBlockEntities.add(pos);
    }

    /**
     * Return true if this component should be synced with the client.
     */
    public boolean update() {
        if (lastShapeValid != shapeValid || !lastMismatchingBlockEntities.equals(mismatchingBlockEntities)) {
            lastShapeValid = shapeValid;
            lastMismatchingBlockEntities = Set.copyOf(mismatchingBlockEntities);
            return true;
        }
        return false;
    }

    @Override
    public void writeClientNbt(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putBoolean("shapeValid", shapeValid);

        ListTag mismatchingBlockEntitiesTag = new ListTag();
        for (BlockPos pos : mismatchingBlockEntities) {
            CompoundTag blockTag = new CompoundTag();
            blockTag.put("pos", NbtUtils.writeBlockPos(pos));
            mismatchingBlockEntitiesTag.add(blockTag);
        }
        tag.put("mismatchingBlockEntities", mismatchingBlockEntitiesTag);
    }

    @Override
    public void readClientNbt(CompoundTag tag, HolderLookup.Provider registries) {
        shapeValid = tag.getBoolean("shapeValid");

        mismatchingBlockEntities.clear();
        ListTag mismatchingBlockEntitiesTag = tag.getList("mismatchingBlockEntities", Tag.TAG_COMPOUND);
        for (Tag blockTag : mismatchingBlockEntitiesTag) {
            if (blockTag instanceof CompoundTag blockCompoundTag) {
                NbtUtils.readBlockPos(blockCompoundTag, "pos").ifPresent(mismatchingBlockEntities::add);
            }
        }
    }
}
