package aztech.milite.blocks.creativestorageunit;

import aztech.milite.blocks.FastBlockEntity;
import aztech.milite.MIRegistries;
import aztech.milite.util.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jspecify.annotations.Nullable;

public class CreativeStorageUnitBlockEntity extends FastBlockEntity implements Tickable {
    @SuppressWarnings("unchecked")
    private final @Nullable BlockCapabilityCache<IEnergyStorage, Direction>[] caches = new BlockCapabilityCache[6];

    public CreativeStorageUnitBlockEntity(BlockPos pos, BlockState state) {
        super(MIRegistries.CREATIVE_STORAGE_UNIT_BE.get(), pos, state);
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            var serverWorld = (ServerLevel) level;

            for (Direction direction : Direction.values()) {
                if (caches[direction.ordinal()] == null) {
                    caches[direction.ordinal()] = BlockCapabilityCache.create(Capabilities.EnergyStorage.BLOCK, serverWorld,
                            worldPosition.relative(direction), direction.getOpposite());
                }

                var target = caches[direction.ordinal()].getCapability();
                if (target != null) {
                    target.receiveEnergy(Integer.MAX_VALUE, false);
                }
            }
        }
    }
}
