package aztech.milite.resource;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.SharedConstants;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

public class GeneratedPathPackResources extends FastPathPackResources {
    private final PackType type;

    public GeneratedPathPackResources(Path root, PackType type) {
        super(new PackLocationInfo("mi_generated_pack", Component.literal("mi_generated_pack"), PackSource.BUILT_IN, Optional.empty()), root);
        this.type = type;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... elements) {
        // TODO: it's unclear that this is still necessary
        if (elements.length == 1 && "pack.mcmeta".equals(elements[0])) {
            return () -> new ByteArrayInputStream("""
                    {
                        "pack": {
                            "description": "Generated resources for Modern Industrialization",
                            "pack_format": %d
                        }
                    }
                    """.formatted(SharedConstants.getCurrentVersion().getPackVersion(type)).getBytes());
        }
        return super.getRootResource(elements);
    }
}
