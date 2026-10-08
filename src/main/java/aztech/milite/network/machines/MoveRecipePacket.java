package aztech.milite.network.machines;

import aztech.milite.inventory.MoveRecipeHandler;
import aztech.milite.network.BasePacket;
import aztech.milite.network.MIStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record MoveRecipePacket(int containedId, ResourceLocation recipeId, int fillAction, int amount) implements BasePacket {
    public static final StreamCodec<ByteBuf, MoveRecipePacket> STREAM_CODEC = StreamCodec.composite(
            MIStreamCodecs.BYTE,
            MoveRecipePacket::containedId,
            ResourceLocation.STREAM_CODEC,
            MoveRecipePacket::recipeId,
            MIStreamCodecs.BYTE,
            MoveRecipePacket::fillAction,
            ByteBufCodecs.INT,
            MoveRecipePacket::amount,
            MoveRecipePacket::new);

    @Override
    public void handle(Context ctx) {
        ctx.assertOnServer();

        AbstractContainerMenu menu = ctx.getPlayer().containerMenu;
        if (menu.containerId == containedId && menu instanceof MoveRecipeHandler handler) {
            handler.moveRecipe(recipeId, fillAction, amount);
        }
    }
}
