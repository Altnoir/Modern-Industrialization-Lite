package aztech.milite.util;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class MobSpawning {
    public static final BlockBehaviour.StateArgumentPredicate<EntityType<?>> NO_SPAWN = (s, w, p, t) -> false;
}
