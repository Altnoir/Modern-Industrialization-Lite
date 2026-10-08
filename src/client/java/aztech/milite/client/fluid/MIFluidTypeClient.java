package aztech.milite.client.fluid;

import aztech.milite.fluid.MIFluidType;
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
