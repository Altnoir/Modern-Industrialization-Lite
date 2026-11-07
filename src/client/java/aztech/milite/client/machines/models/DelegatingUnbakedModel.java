package aztech.milite.client.machines.models;

import aztech.milite.MI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.function.Function;

/**
 * A model that simply delegates to another block/item model, resolved by its model id.
 *
 * <p>
 * This used to live inside MI's pipe renderer package. ModernTech no longer ships pipes, but the casing models
 * ({@code models/machine_casing/*.json}) still rely on this loader, so it was moved here.
 */
public class DelegatingUnbakedModel implements IUnbakedGeometry<DelegatingUnbakedModel> {
    public static final IGeometryLoader<DelegatingUnbakedModel> LOADER = (jsonObject, deserializationContext) -> {
        var delegate = ResourceLocation.parse(GsonHelper.getAsString(jsonObject, "delegate"));
        return new DelegatingUnbakedModel(delegate);
    };

    private final ModelResourceLocation delegate;

    private DelegatingUnbakedModel(ResourceLocation delegate) {
        this.delegate = new ModelResourceLocation(delegate, "standalone");
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter,
            ModelState modelState, ItemOverrides overrides) {
        return Lazy.of(() -> Minecraft.getInstance().getModelManager().getModel(delegate)).get();
    }

    /**
     * @return the loader id used by {@code DelegatingModelBuilder}.
     */
    public static ResourceLocation loaderId() {
        return MI.id("delegate");
    }
}
