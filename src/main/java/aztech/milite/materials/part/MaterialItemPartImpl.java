package aztech.milite.materials.part;

import aztech.milite.materials.MaterialBuilder;

import java.util.function.Consumer;

/**
 * The unique implementation of {@link MaterialItemPart}, to keep impl details out of the interface.
 */
final class MaterialItemPartImpl implements MaterialItemPart {
    private final PartKey key;
    private final String taggedItemId;
    private final String itemId;
    private final Consumer<MaterialBuilder.PartContext> registration;
    private final TextureGenParams textureGenParams;
    private final boolean internal;

    MaterialItemPartImpl(PartKey key, String taggedItemId, String itemId,
            Consumer<MaterialBuilder.PartContext> registration, TextureGenParams textureGenParams, boolean internal) {
        this.key = key;
        this.taggedItemId = taggedItemId;
        this.itemId = itemId;
        this.registration = registration;
        this.textureGenParams = textureGenParams;
        this.internal = internal;
    }

    @Override
    public PartKey key() {
        return key;
    }

    @Override
    public String getTaggedItemId() {
        return taggedItemId;
    }

    @Override
    public String getItemId() {
        return itemId;
    }

    @Override
    public void register(MaterialBuilder.PartContext partContext) {
        registration.accept(partContext);
    }

    @Override
    public TextureGenParams getTextureGenParams() {
        return textureGenParams;
    }

    @Override
    public boolean isInternal() {
        return internal;
    }
}
