package aztech.milite.client.util;

import aztech.milite.client.thirdparty.fabricrendering.MutableQuadView;
import aztech.milite.client.thirdparty.fabricrendering.QuadBuffer;
import aztech.milite.client.thirdparty.fabricrendering.QuadEmitter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.jspecify.annotations.Nullable;

public class QuadCube {
    private final ResourceLocation spriteLocation;
    private BakedQuad @Nullable [] quads;

    public QuadCube(ResourceLocation spriteLocation) {
        this.spriteLocation = spriteLocation;
    }

    public BakedQuad[] getQuads() {
        var sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteLocation);
        // Rebuild quads if the sprite changed
        if (quads == null || quads[0].getSprite() != sprite) {
            quads = buildQuads(sprite);
        }
        return quads;
    }

    private static BakedQuad[] buildQuads(TextureAtlasSprite sprite) {
        var quads = new BakedQuad[6];
        for (Direction direction : Direction.values()) {
            QuadEmitter emitter = new QuadBuffer();
            emitter.square(direction, 0, 0, 1, 1, 0);
            emitter.spriteBake(sprite, MutableQuadView.BAKE_LOCK_UV);
            quads[direction.get3DDataValue()] = emitter.toBakedQuad(sprite);
        }
        return quads;
    }
}
