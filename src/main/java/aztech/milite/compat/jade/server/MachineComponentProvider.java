package aztech.milite.compat.jade.server;

import aztech.milite.api.machine.component.FluidAccess;
import aztech.milite.api.machine.holder.CrafterComponentHolder;
import aztech.milite.api.machine.holder.EnergyComponentHolder;
import aztech.milite.api.machine.holder.EnergyListComponentHolder;
import aztech.milite.api.machine.holder.FluidStorageComponentHolder;
import aztech.milite.api.machine.holder.MultiblockInventoryComponentHolder;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.MI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.addon.universal.ItemCollector;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.EnergyView;
import snownee.jade.api.view.FluidView;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ProgressView;
import snownee.jade.api.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract sealed class MachineComponentProvider<S, C>
        implements IServerExtensionProvider<S>, IClientExtensionProvider<S, C> {
    @Override
    public ResourceLocation getUid() {
        return MI.id("machine");
    }

    public static final class Energy extends MachineComponentProvider<CompoundTag, EnergyView> {
        @Override
        public List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {
            if (accessor.getTarget() instanceof EnergyListComponentHolder holder) {
                var components = holder.getEnergyComponents();

                if (!components.isEmpty()) {
                    long stored = 0;
                    long capacity = 0;

                    for (var component : components) {
                        stored += component.getEu();
                        capacity += component.getCapacity();
                    }

                    return List.of(new ViewGroup<>(List.of(EnergyView.of(stored, capacity))));
                }
            } else if (accessor.getTarget() instanceof EnergyComponentHolder holder) {
                var component = holder.getEnergyComponent();
                return List.of(new ViewGroup<>(List.of(EnergyView.of(component.getEu(), component.getCapacity()))));
            }

            return List.of();
        }

        @Override
        public List<ClientViewGroup<EnergyView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> list) {
            // MI Lite exposes machine energy through NeoForge's RF-compatible energy API.
            return ClientViewGroup.map(list, tag -> EnergyView.read(tag, " RF"), null);
        }
    }

    public static final class Fluids extends MachineComponentProvider<CompoundTag, FluidView> {
        @Override
        public List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {
            var machine = (MachineBlockEntity) accessor.getTarget();
            if (machine instanceof MultiblockInventoryComponentHolder holder) {
                var component = holder.getMultiblockInventoryComponent();
                var inputs = component.getFluidInputs();
                var outputs = component.getFluidOutputs();

                if (!inputs.isEmpty() || !outputs.isEmpty()) {
                    var fluidData = new ViewGroup<CompoundTag>(new ArrayList<>());
                    addFluids(fluidData, inputs);
                    addFluids(fluidData, outputs);
                    return List.of(fluidData);
                } else {
                    return List.of();
                }
            } else if (machine instanceof FluidStorageComponentHolder holder) {
                var component = holder.getFluidStorageComponent();

                if (component != null) {
                    var fluid = component.getVariant();
                    var fluidData = new ViewGroup<CompoundTag>(new ArrayList<>());
                    fluidData.views.add(FluidView.writeDefault(MIJadeCommonPlugin.fluidStack(fluid, component.getAmount()), component.getCapacity()));
                    return List.of(fluidData);
                } else {
                    return List.of();
                }
            } else {
                var stacks = machine.getInventory().getFluidStacks();
                var fluidData = new ViewGroup<CompoundTag>(new ArrayList<>());
                addFluids(fluidData, stacks);
                return List.of(fluidData);
            }
        }

        private void addFluids(ViewGroup<CompoundTag> data, List<? extends FluidAccess> stacks) {
            for (var stack : stacks) {
                data.views.add(FluidView.writeDefault(MIJadeCommonPlugin.fluidStack(stack.getVariant(), stack.getAmount()), stack.getCapacity()));
            }
        }

        @Override
        public List<ClientViewGroup<FluidView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> list) {
            return ClientViewGroup.map(list, FluidView::readDefault, null);
        }
    }

    public static final class Items extends MachineComponentProvider<ItemStack, ItemView> {
        @Override
        public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
            var itemCollector = new ItemCollector<>(new MachineItemIterator());
            return itemCollector.update(accessor);
        }

        @Override
        public List<ClientViewGroup<ItemView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ItemStack>> list) {
            return ClientViewGroup.map(list, ItemView::new, null);
        }
    }

    // TODO: probably needs to be rewritten to be standalone for a better display
    public static final class Progress extends MachineComponentProvider<CompoundTag, ProgressView> {
        @Override
        public List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {
            if (accessor.getTarget() instanceof CrafterComponentHolder holder) {
                var component = holder.getCrafterComponent();
                var progress = component.getProgress();

                if (progress > 0.0f) {
                    var inventory = holder.getCrafterComponent().getInventory();
                    var progressData = new ViewGroup<CompoundTag>(new ArrayList<>());

                    // TODO potentially add inventory information if the Jade API ever supports it
//                    for (var stack : inventory.getItemInputs()) {
//                        progressData.input(stack.toStack());
//                    }
//
//                    for (var stack : inventory.getItemOutputs()) {
//                        progressData.output(stack.toStack());
//                    }

                    progressData.views.add(ProgressView.create(progress));
                    return List.of(progressData);
                }
            }
            return List.of();
        }

        @Override
        public List<ClientViewGroup<ProgressView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> list) {
            return ClientViewGroup.map(list, ProgressView::read, null);
        }
    }
}
