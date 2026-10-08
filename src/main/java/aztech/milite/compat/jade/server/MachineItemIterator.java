package aztech.milite.compat.jade.server;

import aztech.milite.api.machine.component.ItemAccess;
import aztech.milite.api.machine.holder.MultiblockInventoryComponentHolder;
import aztech.milite.machines.MachineBlockEntity;
import net.minecraft.world.item.ItemStack;
import snownee.jade.addon.universal.ItemIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MachineItemIterator extends ItemIterator<MachineBlockEntity> {
    public MachineItemIterator() {
        super((accessor) -> (MachineBlockEntity) accessor.getTarget(), 0);
    }

    @Override
    public Stream<ItemStack> populate(MachineBlockEntity machine, int amount) {
        List<ItemStack> stacks = new ArrayList<>();
        if (machine instanceof MultiblockInventoryComponentHolder multiblock) {
            var component = multiblock.getMultiblockInventoryComponent();
            addStacks(stacks, component.getItemInputs());
            addStacks(stacks, component.getItemOutputs());
        } else {
            addStacks(stacks, machine.getInventory().getItemStacks());
        }
        return stacks.isEmpty() ? Stream.empty() : stacks.stream().limit(amount);
    }

    private static void addStacks(List<ItemStack> stacks, List<? extends ItemAccess> itemAccesses) {
        for (ItemAccess access : itemAccesses) {
            var stack = access.toStack();
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
    }
}
