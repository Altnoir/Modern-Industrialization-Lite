package aztech.milite.client.compat.guideme;

import guideme.color.ConstantColor;
import guideme.document.LytRect;
import guideme.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class MIGuideMeRenderHelper {
    // Unfortunately GuideME does not provide a method that takes in UVs in int form
    public static void fillTexturedRect(RenderContext context, ResourceLocation textureLocation, int x, int y, int width, int height, int uOffset, int vOffset, int uWidth, int uHeight, int textureWidth, int textureHeight) {
        var texture = Minecraft.getInstance().getTextureManager().getTexture(textureLocation);
        context.fillTexturedRect(
                new LytRect(x, y, width, height),
                texture,
                ConstantColor.WHITE, ConstantColor.WHITE, ConstantColor.WHITE, ConstantColor.WHITE,
                uOffset / (float) textureWidth,
                vOffset / (float) textureHeight,
                (uOffset + uWidth) / (float) textureWidth,
                (vOffset + uHeight) / (float) textureHeight);
    }

    public static void fillTexturedRect(RenderContext context, ResourceLocation textureLocation, int x, int y, int width, int height, int uOffset, int vOffset, int uWidth, int uHeight) {
        fillTexturedRect(context, textureLocation, x, y, width, height, uOffset, vOffset, uWidth, uHeight, 256, 256);
    }
}
