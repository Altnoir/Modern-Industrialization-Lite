package aztech.milite.machines.guicomponents;

import aztech.milite.machines.gui.GuiComponentServer;
import aztech.milite.MI;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.Supplier;

public record ReiSlotLocking(SlotLockable slotLockable,
                             Supplier<Boolean> allowLocking) implements GuiComponentServer<Unit, Boolean> {
    public static final Type<Unit, Boolean> TYPE = new Type<>(MI.id("rei_slot_locking"), StreamCodec.unit(Unit.INSTANCE), ByteBufCodecs.BOOL);

    @Override
    public Unit params() {
        return Unit.INSTANCE;
    }

    @Override
    public Boolean extractData() {
        return allowLocking.get();
    }

    @Override
    public Type<Unit, Boolean> getType() {
        return TYPE;
    }

    @FunctionalInterface
    public interface SlotLockable {
        void lockSlots(ResourceLocation recipeId, Inventory inventory);
    }
}
