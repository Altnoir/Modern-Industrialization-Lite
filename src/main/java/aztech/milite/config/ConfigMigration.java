package aztech.milite.config;

import aztech.milite.MI;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigMigration {
    private ConfigMigration() {}

    public static boolean ftbQuestsDefault() {
        return ftbQuestsDefault(FMLPaths.CONFIGDIR.get().resolve("milite-startup.toml"));
    }

    public static boolean ftbQuestsDefault(Path startupConfig) {
        if (!Files.isRegularFile(startupConfig)) {
            return true;
        }
        try (var config = CommentedFileConfig.of(startupConfig)) {
            config.load();
            Object value = config.get("compat.ftbQuestsIntegration");
            return !(value instanceof Boolean enabled) || enabled;
        } catch (RuntimeException exception) {
            MI.LOGGER.warn("Could not read legacy FTB Quests setting from {}", startupConfig, exception);
            return true;
        }
    }
}
