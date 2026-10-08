package aztech.milite.client.compat.viewer.impl.emi;

import aztech.milite.client.screen.MIHandledScreen;
import aztech.milite.compat.viewer.ReiDraggable;
import aztech.milite.network.machines.DoSlotDraggingPacket;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.milite.util.Simulation;
import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class MIDragDropHandler implements EmiDragDropHandler<Screen> {
    @Override
    public boolean dropStack(Screen screen, EmiIngredient ingredient, int mouseX, int mouseY) {
        if (!(screen instanceof MIHandledScreen<?> gui)) {
            return false;
        }

        // Note: might provide peculiar behavior for tags. Checking for size == 1 is an option
        EmiStack stack = ingredient.getEmiStacks().get(0);

        FluidVariant fk = stack.getKey() instanceof Fluid f
                ? FluidVariant.of(new FluidStack(f.builtInRegistryHolder(), 1, stack.getComponentChanges()))
                : null;
        ItemVariant ik = stack.getKey() instanceof Item ? ItemVariant.of(stack.getItemStack()) : null;
        @Nullable
        GuiEventListener element = gui.getChildAt(mouseX, mouseY).orElse(null);
        if (element instanceof ReiDraggable dw) {
            if (ik != null) {
                return dw.dragItem(ik, Simulation.ACT);
            }
            if (fk != null) {
                return dw.dragFluid(fk, Simulation.ACT);
            }
        }
        AbstractContainerMenu handler = gui.getMenu();
        Slot slot = gui.getFocusedSlot();
        if (slot instanceof ReiDraggable dw) {
            int slotId = handler.slots.indexOf(slot);
            if (ik != null && dw.dragItem(ik, Simulation.ACT)) {
                new DoSlotDraggingPacket(handler.containerId, slotId, ik).sendToServer();
                return true;
            }
            if (fk != null && dw.dragFluid(fk, Simulation.ACT)) {
                new DoSlotDraggingPacket(handler.containerId, slotId, fk).sendToServer();
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(Screen screen, EmiIngredient ingredient, GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (!(screen instanceof MIHandledScreen<?> gui)) {
            return;
        }

        List<Rect2i> bounds = new ArrayList<>();
        EmiStack stack = ingredient.getEmiStacks().get(0);

        FluidVariant fk = stack.getKey() instanceof Fluid f
                ? FluidVariant.of(new FluidStack(f.builtInRegistryHolder(), 1, stack.getComponentChanges()))
                : null;
        ItemVariant ik = stack.getKey() instanceof Item ? ItemVariant.of(stack.getItemStack()) : null;
        for (GuiEventListener element : gui.children()) {
            if (element instanceof AbstractWidget cw && element instanceof ReiDraggable dw) {
                if (ik != null && dw.dragItem(ik, Simulation.SIMULATE)) {
                    bounds.add(getWidgetBounds(cw));
                }
                if (fk != null && dw.dragFluid(fk, Simulation.SIMULATE)) {
                    bounds.add(getWidgetBounds(cw));
                }
            }
        }
        AbstractContainerMenu handler = gui.getMenu();
        for (Slot slot : handler.slots) {
            if (slot instanceof ReiDraggable dw) {
                if (ik != null && dw.dragItem(ik, Simulation.SIMULATE)) {
                    bounds.add(getSlotBounds(slot, gui));
                }
                if (fk != null && dw.dragFluid(fk, Simulation.SIMULATE)) {
                    bounds.add(getSlotBounds(slot, gui));
                }
            }
        }

        for (var b : bounds) {
            guiGraphics.fill(b.getX(), b.getY(), b.getX() + b.getWidth(), b.getY() + b.getHeight(), 0x8822BB33);
        }
    }

    private static Rect2i getWidgetBounds(AbstractWidget cw) {
        return new Rect2i(cw.getX(), cw.getY(), cw.getWidth(), cw.getHeight());
    }

    private static Rect2i getSlotBounds(Slot slot, MIHandledScreen<?> screen) {
        return new Rect2i(slot.x + screen.getX(), slot.y + screen.getY(), 16, 16);
    }
}
