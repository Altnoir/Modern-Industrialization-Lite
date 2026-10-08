package aztech.milite.fluid;

import aztech.milite.MI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
