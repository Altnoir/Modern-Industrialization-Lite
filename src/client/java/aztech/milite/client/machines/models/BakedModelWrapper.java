package aztech.milite.client.machines.models;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockAndTintGetter;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;
import java.util.List;

/**
 * Wrapper for {@link BakedModel} which delegates all operations to its parent.
 * <p>
 * Useful for creating wrapper baked models which only override certain properties.
 */
abstract class BakedModelWrapper<T extends BakedModel> implements BakedModel {
    protected final Supplier<T> originalModel;

    public BakedModelWrapper(Supplier<T> originalModel) {
        this.originalModel = originalModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return originalModel.get().getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return originalModel.get().useAmbientOcclusion();
    }

    @Override
    public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
        return originalModel.get().useAmbientOcclusion(state, data, renderType);
    }

    @Override
    public boolean isGui3d() {
        return originalModel.get().isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return originalModel.get().usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        // CTM calls this before we can resolve the UseBlockModelBakedModel delegate.
        // Always return false instead of checking the delegate.
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return originalModel.get().getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return originalModel.get().getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return originalModel.get().getOverrides();
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext cameraTransformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        return originalModel.get().applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        return originalModel.get().getParticleIcon(data);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData,
            @Nullable RenderType renderType) {
        return originalModel.get().getQuads(state, side, rand, extraData, renderType);
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        return originalModel.get().getModelData(level, pos, state, modelData);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        return originalModel.get().getRenderTypes(state, rand, data);
    }

    @Override
    public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
        return originalModel.get().getRenderTypes(itemStack, fabulous);
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
        return originalModel.get().getRenderPasses(itemStack, fabulous);
    }
}
