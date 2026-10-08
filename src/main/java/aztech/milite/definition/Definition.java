package aztech.milite.definition;

import aztech.milite.MI;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Definition {
    public static List<Definition> TRANSLATABLE_DEFINITION = new ArrayList<>();

    private final ResourceLocation id;
    private final String englishName;

    public String getEnglishName() {
        return englishName;
    }

    public ResourceLocation getId() {
        return id;
    }

    public abstract String getTranslationKey();

    public Definition(String englishName, String path, boolean addToTranslation) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(englishName, "englishName");
        this.id = MI.id(path);
        this.englishName = englishName;
        if (addToTranslation) {
            TRANSLATABLE_DEFINITION.add(this);
        }
    }

    public Definition(String englishName, String path) {
        this(englishName, path, true);
    }

    public String getResourceAsString(boolean id) {
        if (id) {
            return this.id.toString();
        } else {
            return this.id.getPath();
        }
    }

    public String path() {
        return getResourceAsString(false);
    }

    public String id() {
        return getResourceAsString(true);
    }
}
