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

    /**
     * Pipe casings carry a horizontal axis state so that connected textures can tell which way a run points.
     */
    public PartTemplate asPipe(PartEnglishNameFormatter formatter, @Nullable String path, float resistance) {
        PartTemplate regPart = new PartTemplate(formatter, this.key)
                .asPipeBlock(SortOrder.CASINGS, 5, resistance);
        if (path != null) {
            return regPart.withCustomPath(path, path);
        }
        return regPart;
    }

    public PartTemplate asPipe(float resistance) {
        return asPipe(new PartEnglishNameFormatter.Default(englishName), null, resistance);
    }

    public PartTemplate asPipe() {
        return asPipe(6);
    }

    public PartTemplate of(String englishName, String path, float resistance) {
        return of(new PartEnglishNameFormatter.Overridden(englishName), path, resistance);
    }

    public PartTemplate of(PartEnglishNameFormatter formatter, @Nullable String path, float resistance, SortOrder sortOrder) {
        PartTemplate regPart = new PartTemplate(formatter, this.key)
                .asBlock(sortOrder, new TextureGenParams.CasingBlock(), 5, resistance, 1, false);
        if (path != null) {
            return regPart.withCustomPath(path, path);
        }
        return regPart;
    }

    public PartTemplate of(String englishName, String path, float resistance, SortOrder sortOrder) {
        return of(new PartEnglishNameFormatter.Overridden(englishName), path, resistance, sortOrder);
    }

    /** sortOrder 自带等级档，不再按材料补 */
    public PartTemplate of(PartEnglishNameFormatter formatter, @Nullable String path, float resistance, SortOrder sortOrder,
                           boolean absoluteSortOrder) {
        PartTemplate regPart = new PartTemplate(formatter, this.key)
                .asBlock(sortOrder, new TextureGenParams.CasingBlock(), 5, resistance, 1, false, absoluteSortOrder);
        if (path != null) {
            return regPart.withCustomPath(path, path);
        }
        return regPart;
    }

    public PartTemplate of(String englishName, String path, float resistance, SortOrder sortOrder, boolean absoluteSortOrder) {
        return of(new PartEnglishNameFormatter.Overridden(englishName), path, resistance, sortOrder, absoluteSortOrder);
    }

    public PartTemplate of(String englishName, String path) {
        return of(new PartEnglishNameFormatter.Overridden(englishName), path, 6f);
    }

    public PartTemplate of(String englishName, String path, SortOrder sortOrder) {
        return of(new PartEnglishNameFormatter.Overridden(englishName), path, 6f, sortOrder);
    }

    public PartTemplate of(float resistance) {
        return of(new PartEnglishNameFormatter.Default(englishName), null, resistance);
    }

    public PartTemplate of() {
        return of(6);
    }
}
