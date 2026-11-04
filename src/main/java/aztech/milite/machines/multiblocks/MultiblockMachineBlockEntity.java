package aztech.milite.machines.multiblocks;

import aztech.milite.MIAdvancementTriggers;
import aztech.milite.advancement.BuiltMultiblockContext;
import aztech.milite.compat.ftbquests.FTBQuestsFacade;
import aztech.milite.compat.rei.machines.ReiMachineRecipes;
import aztech.milite.machines.BEP;
import aztech.milite.machines.components.ActiveShapeComponent;
import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.components.ShapeValidComponent;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.guicomponents.MultiblockBuild;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class MultiblockMachineBlockEntity extends MachineBlockEntity {
    @Nullable
    private ShapeMatcher shapeMatcher;

    public MultiblockMachineBlockEntity(BEP bep, MachineGuiParameters guiParams, OrientationComponent.Params orientationParams) {
        super(bep, guiParams, orientationParams);
        this.shapeValid = new ShapeValidComponent();
        registerComponents(shapeValid);
        registerGuiComponent(new MultiblockBuild(this));
    }

    public final ShapeValidComponent shapeValid;

    public boolean isShapeValid() {
        return shapeValid.shapeValid;
    }

    public ShapeMatcher createShapeMatcher() {
        return new ShapeMatcher(level, worldPosition, orientation.facingDirection, getActiveShape(), shapeValid);
    }

    protected void onRematch(ShapeMatcher shapeMatcher) {}

    protected final void link() {
        if (shapeMatcher == null) {
            shapeMatcher = createShapeMatcher();
            shapeMatcher.registerListeners(level);
        }
        if (shapeMatcher.needsRematch()) {
            shapeValid.shapeValid = false;
            shapeMatcher.rematch(level);
            onRematch(shapeMatcher);

            if (shapeMatcher.isMatchSuccessful()) {
                shapeValid.shapeValid = true;
                triggerBuiltMultiblock();
            }

            if (shapeValid.update()) {
                sync(false);
            }
        }
    }

    private void triggerBuiltMultiblock() {
        var player = level.getServer().getPlayerList().getPlayer(placedBy.placerId);
        if (player != null) {
            var multiblockId = guiParams.blockId;
            var shapeId = getActiveShapeId();
            MIAdvancementTriggers.BUILT_MULTIBLOCK.get().trigger(player, multiblockId, shapeId);
            FTBQuestsFacade.INSTANCE.builtMultiblock(player, new BuiltMultiblockContext(multiblockId, shapeId));
        }
    }

    public final void unlink() {
        if (shapeMatcher != null) {
            shapeMatcher.unlinkHatches();
            shapeMatcher.unregisterListeners(level);
            shapeMatcher = null;
        }
    }

    @Override
    public boolean useWrench(Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (super.useWrench(player, hand, hitResult)) {
            if (!level.isClientSide) {
                unlink();
            }
            return true;
        }
        return false;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (!level.isClientSide) {
            unlink();
        }
    }

    public abstract ShapeTemplate getActiveShape();

    private static BlockPos minBetween(BlockPos a, BlockPos b) {
        return new BlockPos(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()));
    }

    private static BlockPos maxBetween(BlockPos a, BlockPos b) {
        return new BlockPos(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()));
    }

    private static BlockPos findCentreHole(ShapeTemplate shape, BlockPos min, BlockPos max) {
        int centreX = (min.getX() + max.getX()) / 2;
        int centreZ = (min.getZ() + max.getZ()) / 2;
        for (int y = max.getY(); y >= min.getY(); y--) {
            var candidate = new BlockPos(centreX, y, centreZ);
            if (!shape.simpleMembers.containsKey(candidate)) {
                return candidate;
            }
        }
        return new BlockPos(centreX, max.getY(), centreZ);
    }

    @Override
    public Vec3 getOverclockParticlePos() {
        if (!isShapeValid()) {
            return super.getOverclockParticlePos();
        }
        ShapeTemplate shape = getActiveShape();
        if (shape.simpleMembers.isEmpty()) {
            return super.getOverclockParticlePos();
        }

        BlockPos min = null;
        BlockPos max = null;
        for (BlockPos pos : shape.simpleMembers.keySet()) {
            min = min == null ? pos : minBetween(min, pos);
            max = max == null ? pos : maxBetween(max, pos);
        }
        BlockPos hole = findCentreHole(shape, min, max);
        BlockPos world = ShapeMatcher.toWorldPos(worldPosition, orientation.facingDirection, hole);
        return new Vec3(world.getX() + 0.5D, world.getY() + 1.05D, world.getZ() + 0.5D);
    }

    @Nullable
    public final String getActiveShapeId() {
        var activeShape = components.getNullable(ActiveShapeComponent.class);
        if (activeShape != null) {
            int activeShapeIndex = activeShape.getActiveShapeIndex();
            var machineId = guiParams.blockId;
            int shapeIndex = 0;
            for (var shape : ReiMachineRecipes.multiblockShapes) {
                if (shape.machine().equals(machineId)) {
                    if (shapeIndex == activeShapeIndex) {
                        return shape.alternative();
                    }
                    shapeIndex++;
                }
            }
        }
        return null;
    }

    /**
     * Can be used in the guide book to show an example of a big shape.
     */
    public ShapeTemplate getBigShape() {
        return getActiveShape();
    }

    public OrientationComponent getOrientation() {
        return orientation;
    }
}
