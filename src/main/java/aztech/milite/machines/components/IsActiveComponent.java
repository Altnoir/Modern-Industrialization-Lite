package aztech.milite.machines.components;

import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.MachineComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class IsActiveComponent implements MachineComponent.ClientOnly {
    public boolean isActive = false;

    public void updateActive(boolean newActive, MachineBlockEntity be) {
        if (newActive != isActive) {
            isActive = newActive;
            be.sync(false);
        }
    }

    @Override
    public void writeClientNbt(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putBoolean("isActive", isActive);
    }

    @Override
    public void readClientNbt(CompoundTag tag, HolderLookup.Provider registries) {
        isActive = tag.getBoolean("isActive");
    }
}
