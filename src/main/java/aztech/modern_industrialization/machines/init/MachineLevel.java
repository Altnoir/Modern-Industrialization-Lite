/*
 * MIT License
 *
 * Copyright (c) 2026 Altnoir Null々
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

package aztech.modern_industrialization.machines.init;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

/**
 * The tier ladder of the mod. A recipe declares the level it needs, and a machine declares the level it is, so a recipe
 * may only run on a machine of its level or above.
 *
 * <p>
 * The order of the constants is the order of the ladder, and {@link #ordinal()} is used to compare two levels. Inserting
 * a constant in the middle therefore changes the meaning of every saved level, so new levels must be appended at the end
 * or the whole ladder must be migrated.
 *
 * <p>
 * This is deliberately separate from {@link MachineTier}, which carries the energy numbers of a machine. A machine keeps
 * using {@code MachineTier} for its energy buffer and its minimum consumption while this enum decides what it may craft.
 */
public enum MachineLevel implements StringRepresentable {
    BRONZE("bronze"),
    STEEL("steel"),
    ELECTRIC("electric"),
    ALUMINUM("aluminum"),
    STAINLESS_STEEL("stainless_steel"),
    TITANIUM("titanium"),
    IRIDIUM("iridium");

    public static final Codec<MachineLevel> CODEC = StringRepresentable.fromEnum(MachineLevel::values);

    private final String name;

    MachineLevel(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * @return true if a machine of this level may craft a recipe of {@code required}
     */
    public boolean canCraft(MachineLevel required) {
        return required.ordinal() <= ordinal();
    }

    /**
     * The level that goes with a machine tier, used by machines whose level is not stated explicitly.
     *
     * <p>
     * {@link MachineTier#MULTIBLOCK} and {@link MachineTier#UNLIMITED} have no single level of their own: a multiblock
     * takes its level from its own structure, so it must state it instead of relying on this mapping.
     */
    public static MachineLevel of(MachineTier tier) {
        return switch (tier) {
            case BRONZE -> BRONZE;
            case STEEL -> STEEL;
            case LV -> ELECTRIC;
            case MULTIBLOCK, UNLIMITED -> throw new IllegalArgumentException("Machine tier " + tier + " has no level of its own");
        };
    }

    public String getTranslationKey() {
        return "machine_level.milite." + name;
    }

    /**
     * The English name, with every word capitalised, for example {@code "Stainless Steel"}. The serialised name is not
     * suitable on its own because it is lower case and uses underscores.
     */
    public String getEnglishName() {
        StringBuilder englishName = new StringBuilder();
        for (String word : name.split("_")) {
            if (!englishName.isEmpty()) {
                englishName.append(' ');
            }
            englishName.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return englishName.toString();
    }

    /**
     * The English name followed by a space, ready to be put in front of a machine name.
     */
    public String getEnglishPrefix() {
        return getEnglishName() + " ";
    }

    /**
     * The name shown to the player, for example on a recipe in the viewer, where it replaces the energy the recipe used
     * to be labelled with.
     */
    public MutableComponent getDisplayName() {
        return Component.translatable(getTranslationKey());
    }

    public static MachineLevel byName(String name) {
        for (MachineLevel level : values()) {
            if (level.name.equals(name.toLowerCase(Locale.ROOT))) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown machine level " + name);
    }
}
