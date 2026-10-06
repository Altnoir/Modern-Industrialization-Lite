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

package aztech.modern_industrialization.client.fluid;

import aztech.modern_industrialization.fluid.MIFluidType;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

/**
 * Registers the client-side rendering data of MI's fluids.
 *
 * <p>
 * This used to be done by overriding {@code FluidType#initializeClient}, which NeoForge deprecated for removal in
 * 1.21 in favour of {@link RegisterClientExtensionsEvent}.
 */
public final class MIFluidTypeClient {
    private MIFluidTypeClient() {}

    public static void register(RegisterClientExtensionsEvent event) {
        for (MIFluidType fluidType : MIFluidType.all()) {
            IClientFluidTypeExtensions extensions = MIFluidType.clientExtensions(fluidType.getFluidPath());
            event.registerFluidType(extensions, fluidType);
        }
    }
}
