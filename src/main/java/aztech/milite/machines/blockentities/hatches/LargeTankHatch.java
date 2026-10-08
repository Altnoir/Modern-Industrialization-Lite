package aztech.milite.machines.blockentities.hatches;

import aztech.milite.api.machine.holder.FluidStorageComponentHolder;
import aztech.milite.inventory.MIInventory;
import aztech.milite.inventory.SlotPositions;
import aztech.milite.machines.BEP;
import aztech.milite.machines.blockentities.multiblocks.LargeTankMultiblockBlockEntity;
import aztech.milite.machines.components.FluidStorageComponent;
import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.gui.MachineGuiParameters;
import aztech.milite.machines.multiblocks.HatchBlockEntity;
import aztech.milite.machines.multiblocks.HatchType;
import aztech.milite.machines.multiblocks.HatchTypes;
import aztech.milite.MICapabilities;
import aztech.milite.MIText;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class LargeTankHatch extends HatchBlockEntity implements FluidStorageComponentHolder {
    private final MIInventory inventory = new MIInventory(List.of(), List.of(), SlotPositions.empty(), SlotPositions.empty());

    @Nullable
    private LargeTankMultiblockBlockEntity controller = null;

    public LargeTankHatch(BEP bep) {
        super(bep, new MachineGuiParameters.Builder("large_tank_hatch", false).build(), OrientationComponent.Params.noFacingNoOutput());
    }

    @Override
    public MIInventory getInventory() {
        return inventory;
    }

    @Override
    public void openMenu(ServerPlayer player) {
        if (controller != null) {
            controller.openMenu(player);
        } else {
            player.displayClientMessage(MIText.NoLargeTank.text().withStyle(ChatFormatting.RED), true);
        }
    }

    @Override
    public HatchType getHatchType() {
        return HatchTypes.LARGE_TANK;
    }

    @Override
    public boolean upgradesToSteel() {
        return false;
    }

    @Override
    public void unlink() {
        super.unlink();
        controller = null;
        setChanged();
        invalidateCapabilities();
    }

    @Override
    public @Nullable FluidStorageComponent getFluidStorageComponent() {
        return controller == null ? null : controller.getFluidStorageComponent();
    }

    public void setController(LargeTankMultiblockBlockEntity controller) {
        this.controller = controller;
        setChanged();
        invalidateCapabilities();
    }

    private IFluidHandler getStorage() {
        return controller == null ? EmptyFluidHandler.INSTANCE : controller.getExposedFluidHandler();
    }

    public static void registerFluidApi(BlockEntityType<?> bet) {
        MICapabilities.onEvent(event -> {
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, bet, (be, direction) -> ((LargeTankHatch) be).getStorage());
        });
    }

    @Override
    protected boolean hasComparatorOutput() {
        return true;
    }

    @Override
    protected int getComparatorOutput() {
        if (controller != null) {
            return controller.getComparatorOutput();
        } else {
            return 0;
        }
    }
}
