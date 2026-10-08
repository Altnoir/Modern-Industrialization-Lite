package aztech.milite.datagen.dynreg;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DynamicRegistryDatagen {
    private static final List<Runnable> actions = new ArrayList<>();
    private static final Map<ResourceKey<? extends Registry<?>>, List<RegistrySetBuilder.RegistryBootstrap<?>>> entries = new LinkedHashMap<>();

    public static void addAction(Runnable action) {
        actions.add(action);
    }

    public static <T> void add(ResourceKey<? extends Registry<T>> registry, RegistrySetBuilder.RegistryBootstrap<T> bootstrap) {
        entries.computeIfAbsent(registry, k -> new ArrayList<>()).add(bootstrap);
    }

    public static RegistrySetBuilder getBuilder() {
        var builder = new RegistrySetBuilder();
        actions.forEach(Runnable::run);
        for (var entry : entries.entrySet()) {
            builder.add((ResourceKey) entry.getKey(), ctx -> {
                for (var bootstrap : entry.getValue()) {
                    bootstrap.run((BootstrapContext) ctx);
                }
            });
        }
        return builder;
    }

    private DynamicRegistryDatagen() {}
}
