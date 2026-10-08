package aztech.milite.blocks;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;

public interface WrenchableBlockEntity {
    boolean useWrench(Player player, InteractionHand hand, BlockHitResult hitResult);
}
