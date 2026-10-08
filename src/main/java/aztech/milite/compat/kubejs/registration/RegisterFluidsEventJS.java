package aztech.milite.compat.kubejs.registration;

import aztech.milite.definition.FluidDefinition;
import aztech.milite.definition.FluidTexture;
import aztech.milite.MIFluids;
import dev.latvian.mods.kubejs.event.KubeEvent;

public class RegisterFluidsEventJS implements KubeEvent {
    public void register(String englishName, String internalName, int color, String texture, boolean isGas, String opacity) {
        color |= 0xff000000; // Color must always be fully opaque

        var fluidTexture = switch (texture) {
            case "lava" -> FluidTexture.LAVA_LIKE;
            case "plasma" -> FluidTexture.PLASMA_LIKE;
            case "steam" -> FluidTexture.STEAM_LIKE;
            case "water" -> FluidTexture.WATER_LIKE;
            default -> throw new IllegalArgumentException("Invalid texture type: " + texture);
        };
        var opacityValue = switch (opacity) {
            case "low" -> FluidDefinition.LOW_OPACITY;
            case "medium" -> FluidDefinition.MEDIUM_OPACITY;
            case "high" -> FluidDefinition.NEAR_OPACITY;
            case "full" -> FluidDefinition.FULL_OPACITY;
            default -> throw new IllegalArgumentException("Invalid opacity type: " + opacity);
        };

        MIFluids.fluid(englishName, internalName, color, opacityValue, fluidTexture, isGas);
    }
}
