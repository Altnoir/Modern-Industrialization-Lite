package aztech.milite.compat.almostunified;

import com.almostreliable.unified.api.AlmostUnified;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

public class AlmostUnifiedFacadeImpl implements AlmostUnifiedFacade {
    @Nullable
    @Override
    public Item getTargetItem(ItemLike item) {
        return AlmostUnified.INSTANCE.getVariantItemTarget(item);
    }
}
