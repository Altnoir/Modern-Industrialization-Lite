package aztech.milite.client.machines.models;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class UseBlockModelBakedModel extends BakedModelWrapper<BakedModel> {
    private final BlockState targetState;

    public UseBlockModelBakedModel(BlockState targetState, Supplier<BakedModel> stateModel) {
        super(stateModel);
        this.targetState = targetState;
    }

    public BlockState getTargetState() {
        return targetState;
    }
}
