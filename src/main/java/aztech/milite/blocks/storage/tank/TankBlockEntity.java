package aztech.milite.blocks.storage.tank;

import aztech.milite.thirdparty.fabrictransfer.api.bridge.SlotFluidHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidUtil;

public class TankBlockEntity extends AbstractTankBlockEntity {
    public final SlotFluidHandler fluidHandler = new SlotFluidHandler(this);

    public TankBlockEntity(BlockEntityType<?> bet, BlockPos pos, BlockState state) {
        super(bet, pos, state);
    }

    public boolean onPlayerUse(Player player) {
        return FluidUtil.interactWithFluidHandler(player, InteractionHand.MAIN_HAND, new SlotFluidHandler(this));
    }
}
