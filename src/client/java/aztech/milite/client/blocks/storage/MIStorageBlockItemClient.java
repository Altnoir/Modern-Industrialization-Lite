package aztech.milite.client.blocks.storage;

import aztech.milite.blocks.storage.AbstractStorageBlockItem;
import aztech.milite.client.util.RenderHelper;
import aztech.milite.MIBlock;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

/**
 * Registers the client-side rendering data of MI's storage block items (barrels and tanks), so that they are drawn with
 * the block entity renderer in the inventory.
 *
 * <p>
 * This used to be done by overriding {@code Item#initializeClient}, which NeoForge deprecated for removal in 1.21 in
 * favour of {@link RegisterClientExtensionsEvent}. Doing it here also removes the need for the proxy hop that the old
 * override used to keep client-only classes out of the main source set.
 */
public final class MIStorageBlockItemClient {
    private MIStorageBlockItemClient() {}

    public static void register(RegisterClientExtensionsEvent event) {
        for (var definition : MIBlock.BLOCK_DEFINITIONS.values()) {
            Item item = definition.asItem();
            if (item instanceof AbstractStorageBlockItem<?>) {
                event.registerItem(STANDARD_ITEM_RENDERER, item);
            }
        }
    }

    private static final IClientItemExtensions STANDARD_ITEM_RENDERER = new IClientItemExtensions() {
        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return RenderHelper.BLOCK_AND_ENTITY_RENDERER;
        }
    };
}
