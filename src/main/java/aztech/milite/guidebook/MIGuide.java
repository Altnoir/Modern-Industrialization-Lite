package aztech.milite.guidebook;

import aztech.milite.MI;
import guideme.Guide;
import guideme.scene.element.SceneElementTagCompiler;
import net.minecraft.resources.ResourceLocation;

public class MIGuide {
    public static final ResourceLocation ID = MI.id("book");
    private static Guide guide;

    public static void init() {
        guide = Guide.builder(ID)
                .folder("mi_guidebook")
                .extension(SceneElementTagCompiler.EXTENSION_POINT, new MultiblockShapeCompiler())
                .build();
    }

    private MIGuide() {}
}
