package aztech.milite.materials.part;

import aztech.milite.items.SortOrder;
import org.jspecify.annotations.Nullable;

public record CasingPart(String englishName, PartKey key) implements PartKeyProvider {
    public CasingPart(String englishName, String key) {
        this(englishName, new PartKey(key));
    }

    public PartTemplate of(PartEnglishNameFormatter formatter, @Nullable String path, float resistance) {
        PartTemplate regPart = new PartTemplate(formatter, this.key)
                .asBlock(SortOrder.CASINGS, new TextureGenParams.CasingBlock(), 5, resistance, 1, false);
        if (path != null) {
            return regPart.withCustomPath(path, path);
        }
        return regPart;
    }

    public PartTemplate of(String englishName, String path, float resistance) {
        return of(new PartEnglishNameFormatter.Overridden(englishName), path, resistance);
    }

    public PartTemplate of(String englishName, String path) {
        return of(new PartEnglishNameFormatter.Overridden(englishName), path, 6f);
    }

    public PartTemplate of(float resistance) {
        return of(new PartEnglishNameFormatter.Default(englishName), null, resistance);
    }

    public PartTemplate of() {
        return of(6);
    }
}
