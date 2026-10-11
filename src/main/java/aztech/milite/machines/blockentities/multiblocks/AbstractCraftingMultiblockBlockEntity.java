package aztech.milite.machines.blockentities.multiblocks;

import aztech.milite.api.machine.holder.CrafterComponentHolder;
import aztech.milite.api.machine.holder.MultiblockInventoryComponentHolder;
import aztech.milite.inventory.MIInventory;
import aztech.milite.machines.BEP;
import aztech.milite.machines.components.*;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.guicomponents.ReiSlotLocking;
import aztech.milite.machines.models.MachineModelClientData;
import aztech.milite.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.milite.machines.multiblocks.ShapeMatcher;
import aztech.milite.machines.multiblocks.ShapeTemplate;
import aztech.milite.MI;
import aztech.milite.util.Tickable;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractCraftingMultiblockBlockEntity extends MultiblockMachineBlockEntity implements Tickable,
        MultiblockInventoryComponentHolder, CrafterComponentHolder {
    public AbstractCraftingMultiblockBlockEntity(BEP bep, ResourceLocation blockId, int guiHeight, OrientationComponent.Params orientationParams,
            ShapeTemplate[] shapeTemplates) {
        super(bep, new MachineGuiParameters.Builder(blockId, false).backgroundHeight(guiHeight).build(), orientationParams);

        this.activeShape = new ActiveShapeComponent(shapeTemplates);
        this.inventory = new MultiblockInventoryComponent();
        this.crafter = new CrafterComponent(this, inventory, getBehavior());
        this.isActive = new IsActiveComponent();
        registerGuiComponent(new ReiSlotLocking(crafter::lockRecipe, () -> operatingState != OperatingState.NOT_MATCHED));
        registerComponents(activeShape, crafter, isActive);
    }

    public AbstractCraftingMultiblockBlockEntity(BEP bep, ResourceLocation blockId, OrientationComponent.Params orientationParams,
            ShapeTemplate[] shapeTemplates) {
        this(bep, blockId, 200, orientationParams, shapeTemplates);
    }

    public AbstractCraftingMultiblockBlockEntity(BEP bep, String name, OrientationComponent.Params orientationParams,
            ShapeTemplate[] shapeTemplates) {
        this(bep, MI.id(name), orientationParams, shapeTemplates);
    }

    /**
     * Only called once in the constructor to allow for inner classes in subclasses.
     */
    protected abstract CrafterComponent.Behavior getBehavior();

    private OperatingState operatingState = OperatingState.NOT_MATCHED;

    protected final ActiveShapeComponent activeShape;
    protected final MultiblockInventoryComponent inventory;
    protected final CrafterComponent crafter;
    private final IsActiveComponent isActive;

    public ShapeTemplate getActiveShape() {
        return activeShape.getActiveShape();
    }

    @Override
    public MultiblockInventoryComponent getMultiblockInventoryComponent() {
        return inventory;
    }

    @Override
    public CrafterComponent getCrafterComponent() {
        return crafter;
    }

    @Override
    public final MIInventory getInventory() {
        return MIInventory.EMPTY;
    }

    @Override
    public final MachineModelClientData getMachineModelData() {
        return new MachineModelClientData(null, orientation.facingDirection).active(isActive.isActive);
    }

    @Override
    public final void tick() {
        if (!level.isClientSide) {
            link();

            boolean newActive = false;

            if (operatingState == OperatingState.TRYING_TO_RESUME) {
                if (crafter.tryContinueRecipe()) {
                    operatingState = OperatingState.NORMAL_OPERATION;
                }
            }

            if (operatingState == OperatingState.NORMAL_OPERATION) {
                if (crafter.tickRecipe()) {
                    newActive = true;
                }
            }

            isActive.updateActive(newActive, this);
        }
        tickExtra();
    }

    public void tickExtra() {}

    @Override
    protected void onRematch(ShapeMatcher shapeMatcher) {
        operatingState = OperatingState.NOT_MATCHED;
        if (shapeMatcher.isMatchSuccessful()) {
            inventory.rebuild(shapeMatcher);
            operatingState = OperatingState.TRYING_TO_RESUME;
        }
    }

    private enum OperatingState {
        /**
         * Shape is not matched, don't do anything.
         */
        NOT_MATCHED,
        /**
         * Trying to resume a recipe but the output might not fit anymore.
         * We wait until the output fits again before resuming normal operation.
         */
        TRYING_TO_RESUME,
        /**
         * Normal operation.
         */
        NORMAL_OPERATION
    }
}
