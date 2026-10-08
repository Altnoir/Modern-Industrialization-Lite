package aztech.milite.compat.kubejs.registration;

import aztech.milite.api.energy.CableTier;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.resources.ResourceLocation;

/**
 * Event class for registering new cable tiers.
 */
public final class RegisterCableTiersEventJS implements KubeEvent {
    // useful primarily for setting EU based off of a previous tier's value.
    @Info("""
            Gets a previously registered cable tier by name.
            """)
    public CableTier get(String name) {
        return CableTier.getTier(name);
    }

    @SuppressWarnings("unused") // shh, intellij
    @Info("""
            Registers a new cable tier.
            """)
    public CableTier register(String name, String shortEnglishName, String longEnglishName, long rf, ResourceLocation itemKey) {
        CableTier tier = new CableTier(name, shortEnglishName, longEnglishName, rf, itemKey, false);
        CableTier.addTier(tier);
        return tier;
    }
}
