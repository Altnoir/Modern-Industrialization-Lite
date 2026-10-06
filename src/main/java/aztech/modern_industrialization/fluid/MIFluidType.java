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

package aztech.modern_industrialization.fluid;

import aztech.modern_industrialization.MI;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jspecify.annotations.Nullable;

public class MIFluidType extends FluidType {
    /**
     * Every registered MI fluid type, so that the client can attach rendering data to all of them.
     */
    private static final List<MIFluidType> ALL = new ArrayList<>();

    private final DeferredBlock<MIFluidBlock> fluidBlock;

    public MIFluidType(DeferredBlock<MIFluidBlock> fluidBlock, Properties properties) {
        super(properties);
        this.fluidBlock = fluidBlock;
        ALL.add(this);
    }

    public static List<MIFluidType> all() {
        return List.copyOf(ALL);
    }

    /**
     * @return the path of the fluid this type belongs to, used to build its texture locations.
     */
    public String getFluidPath() {
        return fluidBlock.getId().getPath();
    }

    /**
     * @return the client-side rendering data of this fluid type. The caller is responsible for making sure this is
     *         only used on the client, as {@link IClientFluidTypeExtensions} is client-only.
     */
    public static IClientFluidTypeExtensions clientExtensions(String fluidPath) {
        return new IClientFluidTypeExtensions() {
            private @Nullable ResourceLocation textureLocation;

            @Override
            public ResourceLocation getStillTexture() {
                if (textureLocation == null) {
                    textureLocation = MI.id("fluid/%s_still".formatted(fluidPath));
                }
                return textureLocation;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return IClientFluidTypeExtensions.of(Fluids.WATER).getFlowingTexture();
            }
        };
    }
}
