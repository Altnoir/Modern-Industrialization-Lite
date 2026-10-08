package aztech.milite.thirdparty.fabrictransfer.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public final class DebugMessages {
    public static String forGlobalPos(@Nullable Level world, BlockPos pos) {
        String dimension = world != null ? world.dimension().location().toString() : "<no dimension>";
        return dimension + "@" + pos.toShortString();
    }

    public static String forPlayer(Player player) {
        return player.getName() + "/" + player.getStringUUID();
    }

    public static String forInventory(@Nullable Container inventory) {
        if (inventory == null) {
            return "~~NULL~~"; // like in crash reports
        } else if (inventory instanceof Inventory playerInventory) {
            return forPlayer(playerInventory.player);
        } else {
            String result = inventory.toString();

            if (inventory instanceof BlockEntity blockEntity) {
                result += " (%s, %s)".formatted(blockEntity.getBlockState(), forGlobalPos(blockEntity.getLevel(), blockEntity.getBlockPos()));
            }

            return result;
        }
    }
}
