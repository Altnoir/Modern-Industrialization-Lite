package aztech.milite.machines.components;

import aztech.milite.machines.MachineComponent;
import aztech.milite.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.milite.machines.multiblocks.ShapeTemplate;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public class ActiveShapeComponent implements MachineComponent {
    public final ShapeTemplate[] shapeTemplates;
    private int activeShape = 0;

    public ActiveShapeComponent(ShapeTemplate[] shapeTemplates) {
        this.shapeTemplates = shapeTemplates;
    }

    public void incrementShape(MultiblockMachineBlockEntity machine, int delta) {
        setShape(machine, Mth.clamp(activeShape + delta, 0, shapeTemplates.length - 1));
    }

    public void setShape(MultiblockMachineBlockEntity machine, int newShape) {
        if (newShape != activeShape) {
            activeShape = newShape;
            machine.setChanged();
            machine.unlink();
            machine.sync(false);
        }
    }

    public ShapeTemplate getActiveShape() {
        return shapeTemplates[activeShape];
    }

    public int getActiveShapeIndex() {
        return activeShape;
    }

    @Override
    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("activeShape", activeShape);
    }

    @Override
    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        activeShape = tag.getInt("activeShape");
    }
}
