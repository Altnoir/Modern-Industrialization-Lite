package aztech.milite.client.machines.models;

import aztech.milite.MI;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.function.Function;

public class UseBlockModelUnbakedModel implements IUnbakedGeometry<UseBlockModelUnbakedModel> {
    public static final ResourceLocation LOADER_ID = MI.id("use_block_model");
    public static final IGeometryLoader<UseBlockModelUnbakedModel> LOADER = (jsonObject, deserializationContext) -> {

        var blockId = GsonHelper.getAsString(jsonObject, "block");
        var block = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.parse(blockId))
                .orElseThrow(() -> new JsonSyntaxException("Expected \"block\" to be a block, was unknown string " + blockId));

        return new UseBlockModelUnbakedModel(block.defaultBlockState());
    };

    private final BlockState targetState;
    private final ModelResourceLocation delegate;

    private UseBlockModelUnbakedModel(BlockState targetState) {
        this.targetState = targetState;
        this.delegate = BlockModelShaper.stateToModelLocation(targetState);
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter,
            ModelState modelState, ItemOverrides overrides) {
        return new UseBlockModelBakedModel(targetState, Lazy.of(() -> Minecraft.getInstance().getModelManager().getModel(delegate)));
    }
}
