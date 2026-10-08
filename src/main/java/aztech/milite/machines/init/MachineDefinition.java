package aztech.milite.machines.init;

import aztech.milite.definition.BlockDefinition;
import aztech.milite.machines.MachineBlock;
import aztech.milite.machines.MachineBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public record MachineDefinition<T extends MachineBlockEntity>(Supplier<BlockEntityType<T>> blockEntityType,
        BlockDefinition<MachineBlock> blockDefinition) {
    public MachineBlock asBlock() {
        return blockDefinition.asBlock();
    }
}
