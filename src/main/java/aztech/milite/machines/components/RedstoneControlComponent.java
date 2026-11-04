package aztech.milite.machines.components;

import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.MachineComponent;
import aztech.milite.MI;
import aztech.milite.MIComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class RedstoneControlComponent implements MachineComponent.ServerOnly {
    public static final ResourceLocation ID = MI.id("redstone_control");

    private boolean enabled;
    private boolean requiresLowSignal;

    public boolean doAllowNormalOperation(MachineBlockEntity machine) {
        if (!enabled) {
            return true;
        }

        return requiresLowSignal != machine.hasRedstoneHighSignal();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(MachineBlockEntity be, boolean enabled) {
        this.enabled = enabled;
        be.setChanged();
        be.sync();
    }

    public boolean requiresLowSignal() {
        return requiresLowSignal;
    }

    public void setRequiresLowSignal(MachineBlockEntity be, boolean requiresLowSignal) {
        this.requiresLowSignal = requiresLowSignal;
        be.setChanged();
        be.sync();
    }

    @Override
    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putBoolean("redstoneControlEnabled", enabled);
        tag.putBoolean("redstoneControlLowSignal", requiresLowSignal);
    }

    @Override
    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        enabled = tag.getBoolean("redstoneControlEnabled");
        requiresLowSignal = tag.getBoolean("redstoneControlLowSignal");

        // 迁移
        if (tag.contains("redstoneModuleStack")) {
            enabled = true;
            String lowSignalKey = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(MIComponents.LOW_SIGNAL.get()).toString();
            requiresLowSignal = tag.getCompound("redstoneModuleStack").getCompound("components").getBoolean(lowSignalKey);
        }
    }
}
