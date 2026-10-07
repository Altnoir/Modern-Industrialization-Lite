/*
 * MIT License
 *
 * Copyright (c) 2026 Altnoir Null々
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package aztech.modern_industrialization.client.blocks.storage;

import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.blocks.storage.AbstractStorageBlockItem;
import aztech.modern_industrialization.client.util.RenderHelper;
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
