package aztech.milite.blocks.storage.tank.creativetank;

import aztech.milite.blocks.storage.tank.AbstractTankBlockEntity;
import aztech.milite.MIRegistries;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.FluidUtil;

public class CreativeTankBlockEntity extends AbstractTankBlockEntity {
    public CreativeTankBlockEntity(BlockPos pos, BlockState state) {
        super(MIRegistries.CREATIVE_TANK_BE.get(), pos, state);
    }

    @Override
    public boolean onPlayerUse(Player player) {
        if (isResourceBlank()) {
            var fluid = FluidUtil.getFluidContained(player.getItemInHand(InteractionHand.MAIN_HAND));
            if (fluid.isPresent()) {
                setFluid(FluidVariant.of(fluid.get()));
                return true;
            }
            return !isResourceBlank();
        } else {
            // Fill all of the stacks, fuck it!
            var fluidHandler = player.getItemInHand(InteractionHand.MAIN_HAND).getCapability(Capabilities.FluidHandler.ITEM);
            if (fluidHandler != null) {
                int inserted = fluidHandler.fill(resource.toStack(Integer.MAX_VALUE), IFluidHandler.FluidAction.EXECUTE);
                if (inserted > 0) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, fluidHandler.getContainer());
                    return true;
                }
            }
        }
        return false;
    }

    public void setFluid(FluidVariant variant) {
        resource = variant;
        onChanged();
    }
}
