package aztech.milite.client.machines;

import aztech.milite.client.compat.sodium.SodiumCompat;
import aztech.milite.client.machines.models.MachineBakedModel;
import aztech.milite.client.util.ModelHelper;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.machines.models.MachineCasing;
import aztech.milite.machines.models.MachineModelClientData;
import aztech.milite.MI;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.IdentityHashMap;

/**
 * Renders an overlay if the machine is active.
 */
public class MachineBlockEntityRenderer<T extends MachineBlockEntity> implements BlockEntityRenderer<T> {
    private final BlockModelShaper blockModels;
    @Nullable
    private BlockState lastBlockState = null;
    @Nullable
    private MachineBakedModel model = null;
    private final IdentityHashMap<MachineCasing, Object[]> quadCache = new IdentityHashMap<>();
    private static final Object NO_QUAD = new Object();

    @Nullable
    private static final MethodHandle UNWRAP_BAKED_MODEL;
    static {
        MethodHandle unwrapBakedModel = null;
        // Support for Continuity's model wrapping
        if (ModList.get().isLoaded("fabric_renderer_api_v1")) {
            try {
                var wrapperBakedModel = Class.forName("net.fabricmc.fabric.api.renderer.v1.model.WrapperBakedModel");
                var unwrap = wrapperBakedModel.getMethod("unwrap", BakedModel.class);
                unwrapBakedModel = MethodHandles.lookup().unreflect(unwrap);
            } catch (ReflectiveOperationException e) {
                LogUtils.getLogger().error("Failed to reflect WrapperBakedModel.unwrap method", e);
            }
        }
        UNWRAP_BAKED_MODEL = unwrapBakedModel;
    }

    public MachineBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.blockModels = ctx.getBlockRenderDispatcher().getBlockModelShaper();
    }

    @Nullable
    private BakedQuad getCachedQuad(MachineModelClientData data, Direction d) {
        var facing = data.frontDirection;
        if (data.frontDirection == null) {
            return null;
        }
        int cachedQuadIndex = facing.ordinal() * 6 + d.ordinal();
        var casing = data.casing;
        var cachedQuads = quadCache.computeIfAbsent(casing, c -> new Object[36]);

        if (cachedQuads[cachedQuadIndex] == null) {
            TextureAtlasSprite sprite = model == null ? null : MachineBakedModel.getSprite(model.getSprites(casing), d, facing, true);
            if (sprite != null) {
                var vc = new QuadBakingVertexConsumer();
                cachedQuads[cachedQuadIndex] = ModelHelper.bakeSprite(vc, d, sprite, -2 * MachineBakedModel.Z_OFFSET);
            } else {
                cachedQuads[cachedQuadIndex] = NO_QUAD;
            }
        }

        var quad = cachedQuads[cachedQuadIndex];
        return quad == NO_QUAD ? null : (BakedQuad) quad;
    }

    @Nullable
    private MachineBakedModel getMachineModel(BlockState state) {
        var model = blockModels.getBlockModel(state);

        if (UNWRAP_BAKED_MODEL != null) {
            try {
                model = (BakedModel) UNWRAP_BAKED_MODEL.invokeExact((BakedModel) model);
            } catch (Throwable throwable) {
                throw new RuntimeException("Failed to unwrap machine model", throwable);
            }
        }

        if (model instanceof MachineBakedModel mbm) {
            return mbm;
        } else {
            MI.LOGGER.warn("Model {} should have been a MachineBakedModel, but was {}", state, model.getClass());
            return null;
        }
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vcp, int light, int overlay) {
        BlockState state = entity.getBlockState();
        if (lastBlockState == null) {
            lastBlockState = state;
            model = getMachineModel(state);
        } else if (lastBlockState != state) {
            throw new IllegalStateException("Tried to use the same machine BER with two block states: " + state + " and " + lastBlockState);
        }

        MachineModelClientData data = entity.getMachineModelData();
        if (data.isActive) {
            VertexConsumer vc = vcp.getBuffer(Sheets.cutoutBlockSheet());

            for (Direction d : Direction.values()) {
                BakedQuad quad = getCachedQuad(data, d);
                if (quad != null) {
                    int faceLight = LevelRenderer.getLightColor(entity.getLevel(), entity.getBlockState(), entity.getBlockPos().relative(d));
                    vc.putBulkData(matrices.last(), quad, 1.0f, 1.0f, 1.0f, 1.0f, faceLight, OverlayTexture.NO_OVERLAY);

                    SodiumCompat.markSpriteActive(quad.getSprite());
                }
            }
        }
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
