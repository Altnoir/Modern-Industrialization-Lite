package aztech.milite.inventory;

import aztech.milite.MI;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;

/**
 * Implement this on a slot to render its background automatically.
 */
public interface BackgroundRenderedSlot {
    /**
     * @return the {@link ResourceLocation} of the slot atlas texture to use for the slot background. Return null to render no background.
     */
    @Nullable
    default ResourceLocation getBackgroundAtlasLocation() {
        return MI.id("textures/gui/container/slot_atlas.png");
    }

    default int getBackgroundU() {
        return 0;
    }

    default int getBackgroundV() {
        return 0;
    }
}
