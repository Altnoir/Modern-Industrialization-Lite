package aztech.milite.machines.gui;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Server part of a synced component.
 *
 * @param <P> Parameters, synced to the client when the menu is opened.
 * @param <D> Data synced to the client when the menu is opened and when it changes. Must be equals-comparable.
 */
public interface GuiComponentServer<P, D> extends GuiComponent {
    P params();

    /**
     * Extracts the syncable data from the state of the component, block entity, etc.
     */
    D extractData();

    /**
     * Return the id of the component. Must match that of the {@code GuiComponentClient}
     * registered with {@code GuiComponentsClient#register}.
     */
    Type<P, D> getType();

    record Type<P, D>(
            ResourceLocation id,
            StreamCodec<? super RegistryFriendlyByteBuf, P> paramsCodec,
            StreamCodec<? super RegistryFriendlyByteBuf, D> dataCodec) {}
}
