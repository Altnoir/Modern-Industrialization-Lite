package aztech.milite.client;

import aztech.milite.MI;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

public class MIShaders {
    @Nullable
    private static ShaderInstance TRANSLUCENT_HIGHLIGHT_INSTANCE;

    private static ShaderInstance translucentHighlight() {
        return TRANSLUCENT_HIGHLIGHT_INSTANCE;
    }

    public static final RenderStateShard.ShaderStateShard TRANSLUCENT_HIGHLIGHT = new RenderStateShard.ShaderStateShard(MIShaders::translucentHighlight);

    public static void init(IEventBus bus) {
        bus.addListener(MIShaders::registerShaders);
    }

    private static void registerShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), MI.id("translucent_highlight"), DefaultVertexFormat.BLOCK), (shader) -> TRANSLUCENT_HIGHLIGHT_INSTANCE = shader);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
