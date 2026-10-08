package aztech.milite.machines.components;

import aztech.milite.machines.MachineComponent;
import aztech.milite.machines.models.MachineModelClientData;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public class OrientationComponent implements MachineComponent {
    public Direction facingDirection = Direction.NORTH;
    public Direction outputDirection = null;
    public boolean extractItems = false;
    public boolean extractFluids = false;
    public final Params params;
    private final BlockEntity machine;

    public OrientationComponent(Params params, BlockEntity machine) {
        this.params = params;
        if (params.hasOutput) {
            outputDirection = Direction.NORTH;
        }
        this.machine = machine;
    }

    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        if (params.hasFacing) {
            facingDirection = Direction.from3DDataValue(tag.getInt("facingDirection"));
        }
        if (params.hasOutput) {
            outputDirection = Direction.from3DDataValue(tag.getInt("outputDirection"));
        }
        extractItems = tag.getBoolean("extractItems");
        extractFluids = tag.getBoolean("extractFluids");
    }

    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        if (params.hasFacing) {
            tag.putInt("facingDirection", facingDirection.get3DDataValue());
        }
        if (params.hasOutput) {
            tag.putInt("outputDirection", outputDirection.get3DDataValue());
        }
        if (extractItems) {
            tag.putBoolean("extractItems", true);
        }
        if (extractFluids) {
            tag.putBoolean("extractFluids", true);
        }
    }

    public void writeModelData(MachineModelClientData data) {
        if (params.hasFacing) {
            data.frontDirection = facingDirection;
        }
        if (params.hasOutput) {
            data.outputDirection = outputDirection;
            data.itemAutoExtract = extractItems;
            data.fluidAutoExtract = extractFluids;
        }
    }

    /**
     * Try to rotate the machine, and return true if something was rotated.
     */
    public boolean useWrench(Player player, InteractionHand hand, Direction face) {
        if (player.isShiftKeyDown()) {
            if (params.hasOutput) {
                outputDirection = face;
                machine.invalidateCapabilities();
                return true;
            }
        } else if (params.hasFacing) {
            if (params.canBeVertical || face.getAxis().isHorizontal()) {
                facingDirection = face;
            }
            // We consume the event to prevent the GUI from opening.
            return true;
        }
        return false;
    }

    public void onPlaced(@Nullable LivingEntity placer, ItemStack itemStack) {
        if (params.hasFacing) {
            // The placer can be null using some mods' automatic placement: pick NORTH arbitrarily.
            Direction dir = placer != null ? (params.canBeVertical ? placer.getNearestViewDirection() : placer.getDirection()) : Direction.NORTH;
            facingDirection = dir.getOpposite();
        }
        if (params.hasOutput) {
            outputDirection = placer != null ? placer.getNearestViewDirection() : Direction.NORTH;
        }
    }

    public static class Params {
        public final boolean hasFacing;
        public final boolean hasOutput;
        public final boolean hasExtractItems;
        public final boolean hasExtractFluids;
        /**
         * MI itself does not make use of this field, and it purely exists for addons. If you want to use this field, you will need to make your own
         * model loader.
         */
        public final boolean canBeVertical;

        private Params(boolean hasFacing, boolean hasOutput, boolean hasExtractItems, boolean hasExtractFluids, boolean canBeVertical) {
            this.hasFacing = hasFacing;
            this.hasOutput = hasOutput;
            this.hasExtractItems = hasExtractItems;
            this.hasExtractFluids = hasExtractFluids;
            this.canBeVertical = canBeVertical;
        }

        public Params(boolean hasOutput, boolean hasExtractItems, boolean hasExtractFluids, boolean canBeVertical) {
            this(true, hasOutput, hasExtractItems, hasExtractFluids, canBeVertical);
        }

        public Params(boolean hasOutput, boolean hasExtractItems, boolean hasExtractFluids) {
            this(hasOutput, hasExtractItems, hasExtractFluids, false);
        }

        public static Params noFacingNoOutput() {
            return noFacingNoOutput(false, false);
        }

        public static Params noFacingNoOutput(boolean hasExtractItems, boolean hasExtractFluids) {
            return new Params(false, false, hasExtractItems, hasExtractFluids, false);
        }

        public static Params noFacing(boolean hasExtractItems, boolean hasExtractFluids) {
            return new Params(false, true, hasExtractItems, hasExtractFluids, false);
        }
    }
}
