package aztech.milite.config;

import aztech.milite.MI;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.CheckReturnValue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class MIConfigBuilder {
    public static final Map<String, String> configTranslations = new ConcurrentHashMap<>();

    private static String configTranslationKey(String key) {
        return MI.ID + ".configuration." + key;
    }

    private final ModConfigSpec.Builder builder;

    public MIConfigBuilder() {
        this.builder = new ModConfigSpec.Builder();
    }

    public void pushSection(String key, String title) {
        String sectionTranslation = configTranslationKey(key);
        configTranslations.put(sectionTranslation, title);
        builder.push(key);
    }

    public void popSection() {
        builder.pop();
    }

    @CheckReturnValue
    public ModConfigSpec.Builder start(String key, String title, String... comment) {
        if (comment.length == 0) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }

        var translationKey = configTranslationKey(key);
        configTranslations.put(translationKey, title);
        configTranslations.put(translationKey + ".tooltip", String.join(" ", comment));

        return builder.translation(translationKey)
                .comment(comment);
    }

    public ModConfigSpec build() {
        return builder.build();
    }
}
