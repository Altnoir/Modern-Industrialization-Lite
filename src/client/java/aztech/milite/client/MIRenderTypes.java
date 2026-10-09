package aztech.milite.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.jspecify.annotations.Nullable;

public class MIRenderTypes {
    private static @Nullable RenderType MACHINE_WRENCH_OVERLAY;
    private static @Nullable RenderType CUTOUT_HIGHLIGHT; // used by wrong block highlight
    private static @Nullable RenderType TRANSLUCENT_HIGHLIGHT; // used by hatch preview

    public static RenderType machineOverlay() {
        if (MACHINE_WRENCH_OVERLAY == null) {
            MACHINE_WRENCH_OVERLAY = Factory.makeMachineOverlay();
        }
        return MACHINE_WRENCH_OVERLAY;
    }

    public static RenderType cutoutHighlight() {
        if (CUTOUT_HIGHLIGHT == null) {
            CUTOUT_HIGHLIGHT = Factory.makeCutoutHighlight();
        }
        return CUTOUT_HIGHLIGHT;
    }

    public static RenderType translucentHighlight() {
        if (TRANSLUCENT_HIGHLIGHT == null) {
            TRANSLUCENT_HIGHLIGHT = Factory.makeTranslucentHighlight();
        }
        return TRANSLUCENT_HIGHLIGHT;
    }

    // TODO: PR more transitive access wideners to fabric
    private static class Factory extends RenderType {
        private Factory(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, Runnable runnable,
                Runnable runnable2) {
            super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
        }

        private static RenderType makeMachineOverlay() {
            return create("machine_overlay", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.QUADS, 65536, false, true,
                    CompositeState.builder()
                            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                            .setTextureState(NO_TEXTURE)
                            .setLightmapState(NO_LIGHTMAP)
                            .setShaderState(POSITION_COLOR_SHADER)
                            .createCompositeState(false));
        }

        private static RenderType makeCutoutHighlight() {
            // Use block vertex format to use the fast path in BufferBuilder, even if the shader doesn't use the extra vertex attributes.
            return create("cutout_highlight", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 65536, false, false,
                    CompositeState.builder()
                            .setTransparencyState(NO_TRANSPARENCY)
                            .setTextureState(new TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
                            .setLightmapState(LIGHTMAP)
                            .setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                            .createCompositeState(false));
        }

        private static RenderType makeTranslucentHighlight() {
            // Use block vertex format to use the fast path in BufferBuilder, even if the shader doesn't use the extra vertex attributes.
            return create("translucent_highlight", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 65536, false, true,
                    CompositeState.builder()
                            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                            .setTextureState(new TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
                            .setLightmapState(LIGHTMAP)
                            .setShaderState(MIShaders.TRANSLUCENT_HIGHLIGHT)
                            .setCullState(CULL)
                            .setDepthTestState(LEQUAL_DEPTH_TEST)
                            .createCompositeState(false));
        }
    }
}
