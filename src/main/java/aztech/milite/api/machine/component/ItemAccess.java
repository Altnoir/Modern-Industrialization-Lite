package aztech.milite.api.machine.component;

import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import com.google.common.primitives.Ints;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

public interface ItemAccess {
    @ApiStatus.Internal // Don't rely on these types existing forever!
    ItemVariant getVariant();

    long getAmount();

    default ItemStack toStack() {
        return getVariant().toStack(Ints.saturatedCast(getAmount()));
    }
}
