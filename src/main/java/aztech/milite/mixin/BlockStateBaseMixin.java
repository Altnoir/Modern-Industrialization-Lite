package aztech.milite.mixin;

import aztech.milite.items.SteamDrillHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBaseMixin {
    @Inject(method = "getDestroyProgress", at = @At("HEAD"), cancellable = true)
    public void getDestroyProgress(Player pPlayer, BlockGetter pLevel, BlockPos pPos, CallbackInfoReturnable<Float> cir) {
        SteamDrillHooks.overrideDestroyProgress(pPlayer, pLevel, cir);
    }
}
