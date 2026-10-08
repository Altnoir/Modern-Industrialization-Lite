package aztech.milite.materials.part;

import aztech.milite.materials.set.MaterialBlockSet;
import aztech.milite.materials.set.MaterialOreSet;
import aztech.milite.materials.set.MaterialRawSet;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;

public sealed interface TextureGenParams {
    record NoTexture() implements TextureGenParams {}

    record Block(MaterialBlockSet blockSet) implements TextureGenParams {}

    record CasingBlock() implements TextureGenParams {}

    record ColumnBlock() implements TextureGenParams {}

    record DepletedNuclear() implements TextureGenParams {}

    record DoubleIngot() implements TextureGenParams {}

    record Gem() implements TextureGenParams {}

    record HotIngot() implements TextureGenParams {}

    record Ore(ResourceLocation stoneType, MaterialOreSet oreSet) implements TextureGenParams {}

    record RawMetal(boolean isBlock, MaterialRawSet rawSet) implements TextureGenParams {}

    record SimpleRecoloredBlock() implements TextureGenParams {}

    record SimpleRecoloredItem(@Nullable PartKey basePart, @Nullable String overlay) implements TextureGenParams {
        public SimpleRecoloredItem() {
            this(null, null);
        }
    }
}
