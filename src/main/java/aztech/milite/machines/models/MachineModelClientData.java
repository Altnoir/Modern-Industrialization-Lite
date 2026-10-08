package aztech.milite.machines.models;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class MachineModelClientData {
    public static final ModelProperty<MachineModelClientData> KEY = new ModelProperty<>();

    /**
     * May be null to use the default casing.
     */
    @Nullable
    public final MachineCasing casing;
    @Nullable
    public Direction frontDirection;
    public boolean isActive = false;
    /**
     * May be null for no output.
     */
    @Nullable
    public Direction outputDirection = null;
    public boolean itemAutoExtract = false;
    public boolean fluidAutoExtract = false;

    public MachineModelClientData() {
        this(null);
    }

    public MachineModelClientData(@Nullable MachineCasing casing) {
        this.casing = casing;
    }

    public MachineModelClientData(@Nullable MachineCasing casing, Direction frontDirection) {
        this.casing = casing;
        this.frontDirection = frontDirection;
    }

    public MachineModelClientData active(boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MachineModelClientData that = (MachineModelClientData) o;
        return isActive == that.isActive && itemAutoExtract == that.itemAutoExtract && fluidAutoExtract == that.fluidAutoExtract
                && Objects.equals(casing, that.casing) && frontDirection == that.frontDirection && outputDirection == that.outputDirection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(casing, frontDirection, isActive, outputDirection, itemAutoExtract, fluidAutoExtract);
    }
}
