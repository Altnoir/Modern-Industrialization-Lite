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

package aztech.milite.materials;

import java.util.List;

/**
 * The order the materials appear in the creative tab: the order a player can first get hold of them, rather than the
 * alphabetical order they are declared in. The metals that can be mined on the first day come first, then what those
 * are turned into, then everything that needs a machine to make.
 *
 * <p>
 * To move a material, move its name in {@link #ORDER}. A material that is not named here sorts after every material
 * that is, so a new material that ends up at the back of the tab is one that was forgotten here.
 */
public final class MaterialProgression {
    private static final List<String> ORDER = List.of(
            // The blocks the mod starts with: fire clay for the bricks, then the metals a player can mine on day one.
            "fire_clay", "brick", "copper", "tin", "iron", "coal", "lignite_coal", "salt",
            // What a steam age factory turns those into, in the order it can make them.
            "bronze", "coke", "carbon", "steel", "sulfur",
            // The vanilla materials, which are mined rather than made.
            "redstone", "quartz", "lapis", "gold", "diamond", "emerald", "ruby",
            // The ores that are only worth mining once there are machines to process them.
            "bauxite", "lead", "nickel", "silver", "antimony", "manganese", "chromium", "beryllium", "monazite",
            "tungsten", "cadmium", "neodymium", "yttrium", "platinum", "sodium",
            // The electric age: aluminium from bauxite, then the alloys, the coils and the circuits.
            "aluminum", "annealed_copper", "cupronickel", "kanthal", "electrum", "invar", "soldering_alloy", "silicon",
            "stainless_steel", "titanium", "battery_alloy", "blastproof_alloy", "iridium",
            // The last things a player gets hold of, and the nuclear fuel cycle.
            "nuclear_alloy", "superconductor", "uranium", "uranium_238", "uranium_235", "plutonium", "le_uranium",
            "he_uranium", "le_mox", "he_mox");

    private MaterialProgression() {}

    /**
     * Where a material sits in the creative tab.
     *
     * @param materialName - the name a material was registered under, for example {@code bronze}.
     * @returns the position of the material, or the end of the list when it is not one this knows about.
     */
    public static int rankOf(String materialName) {
        var rank = ORDER.indexOf(materialName);
        return rank < 0 ? ORDER.size() : rank;
    }
}
