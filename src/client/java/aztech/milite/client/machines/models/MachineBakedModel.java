package aztech.milite.client.machines.models;

import aztech.milite.client.util.ModelHelper;
import aztech.milite.machines.models.MachineCasing;
import aztech.milite.machines.models.MachineModelClientData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockAndTintGetter;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MachineBakedModel implements IDynamicBakedModel {
    public static float Z_OFFSET = 5e-4f; // Cannot be lower due to Embeddium compact vertex format

    private static final ChunkRenderTypeSet CUTOUT_MIPPED = ChunkRenderTypeSet.of(RenderType.cutoutMipped());

    public static final String CASING_FOLDER = "machine_casing";

    public static ModelResourceLocation getCasingModelId(MachineCasing casing) {
        return ModelResourceLocation.standalone(casing.key.withPath(p -> CASING_FOLDER + "/" + p));
    }

    public static BakedModel getCasingModel(MachineCasing casing) {
        return Minecraft.getInstance().getModelManager().getModel(getCasingModelId(casing));
    }

    private final MachineCasing baseCasing;
    private final @Nullable TextureAtlasSprite[] defaultOverlays;
    private final Map<MachineCasing, @Nullable TextureAtlasSprite[]> tieredOverlays;
    private final boolean noOverlayOnOutputSide;
    private final MachineModelClientData defaultData;

    MachineBakedModel(MachineCasing baseCasing,
            @Nullable TextureAtlasSprite[] defaultOverlays,
            Map<MachineCasing, @Nullable TextureAtlasSprite[]> tieredOverlays,
            boolean noOverlayOnOutputSide) {
        this.baseCasing = baseCasing;
        this.defaultOverlays = defaultOverlays;
        this.tieredOverlays = tieredOverlays;
        this.noOverlayOnOutputSide = noOverlayOnOutputSide;
        this.defaultData = new MachineModelClientData(baseCasing, Direction.NORTH);
    }

    public MachineCasing getBaseCasing() {
        return baseCasing;
    }

    public @Nullable TextureAtlasSprite[] getSprites(@Nullable MachineCasing casing) {
        if (casing == null) {
            return defaultOverlays;
        }
        return tieredOverlays.getOrDefault(casing, defaultOverlays);
    }

    /**
     * Returns null if nothing should be rendered.
     */
    @Nullable
    public static TextureAtlasSprite getSprite(@Nullable TextureAtlasSprite[] sprites, Direction side, Direction facingDirection, boolean isActive) {
        int spriteId;
        if (side.getAxis().isHorizontal()) {
            spriteId = (facingDirection.get2DDataValue() - side.get2DDataValue() + 4) % 4 * 2;
        } else {
            spriteId = (facingDirection.get2DDataValue() + 4) * 2;

            if (side == Direction.DOWN) {
                spriteId += 8;
            }
        }
        if (isActive) {
            spriteId++;
        }
        return sprites[spriteId];
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        var machineData = modelData.get(MachineModelClientData.KEY);
        if (machineData == null) {
            return modelData;
        }

        MachineCasing casing = Objects.requireNonNullElse(machineData.casing, baseCasing);
        return getCasingModel(casing).getModelData(level, pos, state, modelData);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        var data = extraData.get(MachineModelClientData.KEY);
        if (data == null) {
            data = defaultData;
        }

        MachineCasing casing = Objects.requireNonNullElse(data.casing, baseCasing);
        var sprites = getSprites(casing);

        List<BakedQuad> quads = new ArrayList<>();
        var vc = new QuadBakingVertexConsumer();

        if (side != null) {
            // Casing
            quads.addAll(getCasingModel(casing).getQuads(state, side, rand, extraData, renderType));
            // Machine overlays
            if (!noOverlayOnOutputSide || side != data.outputDirection) {
                // Draw the "front" texture on the north side if the machine has no facing
                var facingDirection = Objects.requireNonNullElse(data.frontDirection, Direction.NORTH);
                TextureAtlasSprite sprite = getSprite(sprites, side, facingDirection, false);
                if (sprite != null) {
                    quads.add(ModelHelper.bakeSprite(vc, side, sprite, -Z_OFFSET));
                }
            }
        }

        // Output overlays
        if (data.outputDirection != null && side == data.outputDirection) {
            if (sprites[24] != null) {
                quads.add(ModelHelper.bakeSprite(vc, data.outputDirection, sprites[24], -3 * Z_OFFSET));
            }
            if (data.itemAutoExtract) {
                quads.add(ModelHelper.bakeSprite(vc, data.outputDirection, sprites[25], -3 * Z_OFFSET));
            }
            if (data.fluidAutoExtract) {
                quads.add(ModelHelper.bakeSprite(vc, data.outputDirection, sprites[26], -3 * Z_OFFSET));
            }
        }

        return quads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return getCasingModel(baseCasing).getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return ModelHelper.MODEL_TRANSFORM_BLOCK;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        return CUTOUT_MIPPED;
    }
}
