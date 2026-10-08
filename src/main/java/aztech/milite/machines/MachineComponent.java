package aztech.milite.machines;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface MachineComponent {
    void writeNbt(CompoundTag tag, HolderLookup.Provider registries);

    void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine);

    default void writeClientNbt(CompoundTag tag, HolderLookup.Provider registries) {
        writeNbt(tag, registries);
    }

    default void readClientNbt(CompoundTag tag, HolderLookup.Provider registries) {
        readNbt(tag, registries, false);
    }

    interface ClientOnly extends MachineComponent {
        @Override
        default void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {}

        @Override
        default void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {}

        @Override
        void writeClientNbt(CompoundTag tag, HolderLookup.Provider registries);

        @Override
        void readClientNbt(CompoundTag tag, HolderLookup.Provider registries);
    }

    interface ServerOnly extends MachineComponent {
        @Override
        default void writeClientNbt(CompoundTag tag, HolderLookup.Provider registries) {}

        @Override
        default void readClientNbt(CompoundTag tag, HolderLookup.Provider registries) {}
    }
}
