package aztech.milite;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

/**
 * Holder for some keys that are needed before the block is even created.
 */
public class MIBlockKeys {
    public static final ResourceKey<Block> BASIC_MACHINE_HULL = block("basic_machine_hull");
    public static final ResourceKey<Block> ADVANCED_MACHINE_HULL = block("advanced_machine_hull");
    public static final ResourceKey<Block> TURBO_MACHINE_HULL = block("turbo_machine_hull");
    public static final ResourceKey<Block> HIGHLY_ADVANCED_MACHINE_HULL = block("highly_advanced_machine_hull");
    public static final ResourceKey<Block> QUANTUM_MACHINE_HULL = block("quantum_machine_hull");

    private static ResourceKey<Block> block(String path) {
        return ResourceKey.create(Registries.BLOCK, MI.id(path));
    }
}
