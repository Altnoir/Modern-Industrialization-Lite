package aztech.milite.machines.components;

import aztech.milite.machines.MachineComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class PlacedByComponent implements MachineComponent {
    @Nullable
    public UUID placerId = null;

    @Override
    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        if (placerId != null) {
            tag.putString("placer", placerId.toString());
        }
    }

    @Override
    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        try {
            placerId = UUID.fromString(tag.getString("placer"));
        } catch (IllegalArgumentException iae) {
            placerId = null;
        }
    }

    public void onPlaced(@Nullable LivingEntity placer) {
        if (placer instanceof Player) {
            placerId = placer.getUUID();
        } else {
            placerId = null;
        }
    }
}
