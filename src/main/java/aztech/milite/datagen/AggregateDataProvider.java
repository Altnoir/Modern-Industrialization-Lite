package aztech.milite.datagen;

import com.mojang.logging.LogUtils;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.List;
import java.util.stream.Collectors;

public class AggregateDataProvider implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final PackOutput packOutput;
    private final String name;
    private final List<DataProvider> providers = new ArrayList<>();

    public AggregateDataProvider(PackOutput packOutput, String name) {
        this.packOutput = packOutput;
        this.name = name;
    }

    private <T extends DataProvider> T addProvider(T provider) {
        providers.add(provider);
        return provider;
    }

    public <T extends DataProvider> T addProvider(Function<PackOutput, T> providerConstructor) {
        return addProvider(providerConstructor.apply(packOutput));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        LOGGER.info("Will run the following providers in parallel:" +
                providers.stream().map(provider -> "\n - Modern Industrialization/%s".formatted(provider.getName())).collect(Collectors.joining()));
        return CompletableFuture.allOf(providers.stream().map(provider -> provider.run(output)).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return name + " (aggregated)";
    }
}
