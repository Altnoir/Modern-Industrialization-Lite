package aztech.milite.api.energy;

import aztech.milite.compat.kubejs.KubeJSProxy;
import aztech.milite.machines.models.MachineCasing;
import aztech.milite.machines.models.MachineCasings;
import aztech.milite.MI;
import aztech.milite.MIBlockKeys;
import aztech.milite.thirdparty.fabrictransfer.api.storage.StoragePreconditions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A single energy tier. In MI Lite the tiers are purely expressed in FE/t: the value of a tier is the base
 * amount of FE/t that a machine of that tier consumes or produces, and {@link #getMaxTransfer()} is the
 * ceiling for machine buffers of that tier.
 *
 * <p>
 * MI Lite no longer ships cables: machines expose NeoForge's standard energy capability, so the tier is now
 * just a machine class marker (it selects the machine casing model and caps the machine's internal buffer),
 * not a cable voltage.
 */
public final class CableTier implements Comparable<CableTier> {
    public static CableTier ELECTRIC = new CableTier("electric", "Electric", "Low Voltage", 32, null);
    public static CableTier ALUMINUM = new CableTier("aluminum", "Aluminum", "Medium Voltage", 128,
            MIBlockKeys.ALUMINUM_MACHINE_HULL);
    public static CableTier STAINLESS_STEEL = new CableTier("stainless_steel", "Stainless Steel", "High Voltage", 1024,
            MIBlockKeys.STAINLESS_STEEL_MACHINE_HULL);
    public static CableTier TITANIUM = new CableTier("titanium", "Titanium", "Extreme Voltage", 8192,
            MIBlockKeys.TITANIUM_MACHINE_HULL);
    public static CableTier IRIDIUM = new CableTier("iridium", "Iridium", "Superconductor", 128000000,
            MIBlockKeys.IRIDIUM_MACHINE_HULL);

    public final String name;
    public final String shortEnglishName;
    public final String longEnglishName;
    /**
     * The base amount of FE/t of this tier.
     */
    public final long fe;
    /**
     * {@code null} for LV only.
     */
    @Nullable
    public final ResourceLocation itemKey;
    /**
     * {@code true} if this is present in base MI, {@code false} if added using some API.
     */
    public final boolean builtin;

    /**
     * The {@link MachineCasing} that uses this cable tier.
     */
    @ApiStatus.Internal
    public final MachineCasing casing;

    @ApiStatus.Internal
    public CableTier(String name, String shortEnglishName, String longEnglishName, long fe, ResourceLocation itemKey, boolean builtin) {
        StoragePreconditions.notNegative(fe);

        this.name = name;
        this.shortEnglishName = shortEnglishName;
        this.longEnglishName = longEnglishName;
        this.fe = fe;
        this.itemKey = itemKey;
        this.casing = MachineCasings.create(MI.id(name), shortEnglishName);
        this.builtin = builtin;
    }

    private CableTier(String name, String shortEnglishName, String longEnglishName, long fe, @Nullable ResourceKey<Block> key) {
        this(name, shortEnglishName, longEnglishName, fe, key == null ? null : key.location(), true);
    }

    public String shortEnglishKey() {
        return "cable_tier_short.milite." + name;
    }

    public MutableComponent shortEnglishName() {
        return Component.translatable(shortEnglishKey());
    }

    public String longEnglishKey() {
        return "cable_tier_long.milite." + name;
    }

    public MutableComponent longEnglishName() {
        return Component.translatable(longEnglishKey());
    }

    /**
     * @return The maximum FE/t that a machine of this tier can hold in its internal buffer.
     */
    public long getMaxTransfer() {
        return fe * 8;
    }

    public long getFe() {
        return fe;
    }

    @Override
    public int compareTo(CableTier other) {
        return Long.compare(fe, other.fe);
    }

    @Override
    public String toString() {
        return name;
    }

    private final static Map<String, CableTier> tiers = new HashMap<>();
    private final static Map<String, String> LEGACY_NAMES = Map.of("lv", "electric", "mv", "aluminum", "hv",
            "stainless_steel", "ev", "titanium", "superconductor", "iridium");

    /**
     * @return The largest base FE/t of all registered tiers, used to cap machine buffers.
     */
    public static long maxFe() {
        return allTiers().stream().mapToLong(t -> t.fe).max().orElse(0);
    }

    /**
     * Adds a new cable tier to the internal mapping of tiers. If the tier already existed,
     * then this will error.
     *
     * @param tier The new tier instance to register.
     */
    @ApiStatus.Internal
    public static void addTier(CableTier tier) {
        for (var existingTier : tiers.values()) {
            if (existingTier.name.equals(tier.name)) {
                throw new IllegalArgumentException("Tier " + tier + " already exists!");
            }
            if (existingTier.fe == tier.fe) {
                throw new IllegalArgumentException("A tier with fe " + tier.fe + " already exists!");
            }
            if (Objects.equals(existingTier.itemKey, tier.itemKey)) {
                throw new IllegalArgumentException("A tier with block key " + tier.itemKey + " already exists!");
            }
        }

        tiers.put(tier.name, tier);
    }

    /**
     * Looks up a cable tier registered by name.
     *
     * @param name The name of the tier, e.g. <code>electric</code>.
     * @return An instance of the cable tier with the provided name.
     */
    public static CableTier getTier(String name) {
        // Worlds saved before the tiers were named after their material still store the old voltage names.
        CableTier existing = tiers.get(LEGACY_NAMES.getOrDefault(name, name));

        if (existing == null) {
            throw new NoSuchElementException("No such cable tier: " + name);
        }

        return existing;
    }

    /**
     * @return A list of all registered CableTier instances, sorted by their FE.
     */
    public static List<CableTier> allTiers() {
        return tiers.values().stream().sorted().toList();
    }

    static {
        addTier(ELECTRIC);
        addTier(ALUMINUM);
        addTier(STAINLESS_STEEL);
        addTier(TITANIUM);
        addTier(IRIDIUM);

        KubeJSProxy.instance.fireCableTiersEvent();
    }
}
