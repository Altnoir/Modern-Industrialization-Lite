package aztech.milite.compat.kubejs.machine;

import aztech.milite.machines.models.MachineCasings;
import aztech.milite.MI;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RegisterCasingsEventJS implements KubeEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterCasingsEventJS.class);

    @Deprecated
    public void register(String... names) {
        LOGGER.warn("register is deprecated, use registerNamed instead");
        for (var name : names) {
            registerNamed(name, null);
        }
    }

    public void registerNamed(String name, String englishName) {
        if (name.contains(":")) {
            throw new IllegalArgumentException("Casing name cannot contain ':'.");
        }
        MachineCasings.create(MI.id(name), englishName);
    }

    public void registerBlockImitation(String name, ResourceLocation block) {
        Objects.requireNonNull(block, "block may not be null");
        if (name.contains(":")) {
            throw new IllegalArgumentException("Casing name cannot contain ':'.");
        }

        MachineCasings.createBlockImitation(MI.id(name), () -> BuiltInRegistries.BLOCK.get(block));
    }
}
