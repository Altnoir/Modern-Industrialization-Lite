/*
 * MIT License
 *
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package aztech.modern_industrialization.api.energy;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIBlockKeys;
import aztech.modern_industrialization.compat.kubejs.KubeJSProxy;
import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.storage.StoragePreconditions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

/**
 * A single energy tier. In MI Lite the tiers are purely expressed in RF/t: the value of a tier is the base
 * amount of RF/t that a machine of that tier consumes or produces, and {@link #getMaxTransfer()} is the
 * ceiling for machine buffers of that tier.
 *
 * <p>
 * MI Lite no longer ships cables: machines expose NeoForge's standard energy capability, so the tier is now
 * just a machine class marker (it selects the machine casing model and caps the machine's internal buffer),
 * not a cable voltage.
 */
public final class CableTier implements Comparable<CableTier> {
    // static references
    public static CableTier LV = new CableTier("lv", "LV", "Low Voltage", 32, null);
    public static CableTier MV = new CableTier("mv", "MV", "Medium Voltage", 128, MIBlockKeys.ADVANCED_MACHINE_HULL);
    public static CableTier HV = new CableTier("hv", "HV", "High Voltage", 1024, MIBlockKeys.TURBO_MACHINE_HULL);
    public static CableTier EV = new CableTier("ev", "EV", "Extreme Voltage", 8192, MIBlockKeys.HIGHLY_ADVANCED_MACHINE_HULL);
    public static CableTier SUPERCONDUCTOR = new CableTier("superconductor", "SV", "Superconductor", 128000000,
            MIBlockKeys.QUANTUM_MACHINE_HULL);

    public final String name;
    public final String shortEnglishName;
    public final String longEnglishName;
    /**
     * The base amount of RF/t of this tier.
     */
    public final long rf;
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
    public CableTier(String name, String shortEnglishName, String longEnglishName, long rf, ResourceLocation itemKey, boolean builtin) {
        StoragePreconditions.notNegative(rf);

        this.name = name;
        this.shortEnglishName = shortEnglishName;
        this.longEnglishName = longEnglishName;
        this.rf = rf;
        this.itemKey = itemKey;
        this.casing = MachineCasings.create(MI.id(name), shortEnglishName);
        this.builtin = builtin;
    }

    private CableTier(String name, String shortEnglishName, String longEnglishName, long rf, @Nullable ResourceKey<Block> key) {
        this(name, shortEnglishName, longEnglishName, rf, key == null ? null : key.location(), true);
    }

    public String shortEnglishKey() {
        return "cable_tier_short.modern_industrialization." + name;
    }

    public MutableComponent shortEnglishName() {
        return Component.translatable(shortEnglishKey());
    }

    public String longEnglishKey() {
        return "cable_tier_long.modern_industrialization." + name;
    }

    public MutableComponent longEnglishName() {
        return Component.translatable(longEnglishKey());
    }

    /**
     * @return The maximum RF/t that a machine of this tier can hold in its internal buffer.
     */
    public long getMaxTransfer() {
        return rf * 8;
    }

    public long getEu() {
        return rf;
    }

    @Override
    public int compareTo(CableTier other) {
        return Long.compare(rf, other.rf);
    }

    @Override
    public String toString() {
        return name;
    }

    // "registry" of name -> instance.
    private final static Map<String, CableTier> tiers = new HashMap<>();

    /**
     * @return The largest base RF/t of all registered tiers, used to cap machine buffers.
     */
    public static long maxRf() {
        return allTiers().stream().mapToLong(t -> t.rf).max().orElse(0);
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
            if (existingTier.rf == tier.rf) {
                throw new IllegalArgumentException("A tier with rf " + tier.rf + " already exists!");
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
     * @param name The name of the tier, e.g. <code>lv</code>.
     * @return An instance of the cable tier with the provided name.
     */
    public static CableTier getTier(String name) {
        CableTier existing = tiers.get(name);

        if (existing == null) {
            throw new NoSuchElementException("No such cable tier: " + name);
        }

        return existing;
    }

    /**
     * @return A list of all registered CableTier instances, sorted by their EU.
     */
    public static List<CableTier> allTiers() {
        return tiers.values().stream().sorted().toList();
    }

    static {
        addTier(LV);
        addTier(MV);
        addTier(HV);
        addTier(EV);
        addTier(SUPERCONDUCTOR);

        KubeJSProxy.instance.fireCableTiersEvent();
    }
}
