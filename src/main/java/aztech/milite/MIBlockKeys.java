package aztech.milite;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

/**
 * Holder for some keys that are needed before the block is even created.
 */
public class MIBlockKeys {
    public static final ResourceKey<Block> ELECTRIC_MACHINE_HULL = block("electric_machine_hull");
    public static final ResourceKey<Block> ALUMINUM_MACHINE_HULL = block("aluminum_alloy_machine_hull");
    public static final ResourceKey<Block> STAINLESS_STEEL_MACHINE_HULL = block("stainless_steel_machine_hull");
    public static final ResourceKey<Block> TITANIUM_MACHINE_HULL = block("titanium_machine_hull");
    public static final ResourceKey<Block> IRIDIUM_MACHINE_HULL = block("iridium_machine_hull");

    private static ResourceKey<Block> block(String path) {
        return ResourceKey.create(Registries.BLOCK, MI.id(path));
    }
}
