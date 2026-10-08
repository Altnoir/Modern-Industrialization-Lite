package aztech.milite.client.compat.viewer.impl.rei;

import aztech.milite.client.screen.MIHandledScreen;
import aztech.milite.compat.viewer.ReiDraggable;
import aztech.milite.network.machines.DoSlotDraggingPacket;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.milite.util.Simulation;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.drag.DraggableStack;
import me.shedaniel.rei.api.client.gui.drag.DraggableStackVisitor;
import me.shedaniel.rei.api.client.gui.drag.DraggedAcceptorResult;
import me.shedaniel.rei.api.client.gui.drag.DraggingContext;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class MIDraggableStackVisitor implements DraggableStackVisitor<Screen> {
    @Override
    public DraggedAcceptorResult acceptDraggedStack(DraggingContext<Screen> context, DraggableStack stack) {
        return acceptsStack(context, stack) ? DraggedAcceptorResult.ACCEPTED : DraggedAcceptorResult.PASS;
    }

    private boolean acceptsStack(DraggingContext<Screen> context, DraggableStack stack) {
        FluidVariant fk = stack.getStack().getValue() instanceof FluidStack fs ? FluidVariant.of(FluidStackHooksForge.toForge(fs)) : null;
        ItemVariant ik = stack.getStack().getValue() instanceof ItemStack is ? ItemVariant.of(is) : null;
        @Nullable
        GuiEventListener element = context.getScreen().getChildAt(context.getCurrentPosition().x, context.getCurrentPosition().y)
                .orElse(null);
        if (element instanceof ReiDraggable dw) {
            if (ik != null) {
                return dw.dragItem(ik, Simulation.ACT);
            }
            if (fk != null) {
                return dw.dragFluid(fk, Simulation.ACT);
            }
        }
        if (context.getScreen() instanceof MIHandledScreen<?> handledScreen) {
            AbstractContainerMenu handler = handledScreen.getMenu();
            Slot slot = handledScreen.getFocusedSlot();
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
        }
        return false;
    }

    @Override
    public Stream<BoundsProvider> getDraggableAcceptingBounds(DraggingContext<Screen> context, DraggableStack stack) {
        List<BoundsProvider> bounds = new ArrayList<>();
        FluidVariant fk = stack.getStack().getValue() instanceof FluidStack fs ? FluidVariant.of(FluidStackHooksForge.toForge(fs)) : null;
        ItemVariant ik = stack.getStack().getValue() instanceof ItemStack is ? ItemVariant.of(is) : null;
        for (GuiEventListener element : context.getScreen().children()) {
            if (element instanceof AbstractWidget cw && element instanceof ReiDraggable dw) {
                if (ik != null && dw.dragItem(ik, Simulation.SIMULATE)) {
                    bounds.add(getWidgetBounds(cw));
                }
                if (fk != null && dw.dragFluid(fk, Simulation.SIMULATE)) {
                    bounds.add(getWidgetBounds(cw));
                }
            }
        }
        if (context.getScreen() instanceof MIHandledScreen<?> handledScreen) {
            AbstractContainerMenu handler = handledScreen.getMenu();
            for (Slot slot : handler.slots) {
                if (slot instanceof ReiDraggable dw) {
                    if (ik != null && dw.dragItem(ik, Simulation.SIMULATE)) {
                        bounds.add(getSlotBounds(slot, handledScreen));
                    }
                    if (fk != null && dw.dragFluid(fk, Simulation.SIMULATE)) {
                        bounds.add(getSlotBounds(slot, handledScreen));
                    }
                }
            }
        }
        return bounds.stream();
    }

    @Override
    public <R extends Screen> boolean isHandingScreen(R screen) {
        return screen instanceof MIHandledScreen;
    }

    private static DraggableStackVisitor.BoundsProvider getWidgetBounds(AbstractWidget cw) {
        return DraggableStackVisitor.BoundsProvider.ofRectangle(new Rectangle(cw.getX(), cw.getY(), cw.getWidth(), cw.getHeight()));
    }

    private static DraggableStackVisitor.BoundsProvider getSlotBounds(Slot slot, MIHandledScreen<?> screen) {
        return DraggableStackVisitor.BoundsProvider.ofRectangle(new Rectangle(slot.x + screen.getX(), slot.y + screen.getY(), 16, 16));
    }
}
