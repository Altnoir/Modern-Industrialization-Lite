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

package aztech.milite.materials.part;

import java.util.List;

/**
 * The order the parts of one material appear in the creative tab, so that the forms of a metal are listed the way a
 * player works through them: what comes out of the ground, then what is ground out of that, then what is cast, rolled
 * and finally assembled from it.
 *
 * <p>
 * Only parts that are items are listed. Block parts are sorted by the group they belong to instead, so the ore of a
 * metal sits with the ores and its casing with the casings.
 *
 * <p>
 * To move a part, move its name in {@link #ORDER}. A part that is not named here sorts after every part that is.
 */
public final class PartProgression {
    private static final List<String> ORDER = List.of(
            // The first things a player makes out of a material, and what comes straight out of the ground.
            "brick", "bricks", "raw", "crushed_dust", "tiny_dust", "dust",
            // What a furnace makes of that.
            "ingot", "nugget", "double_ingot", "hot_ingot", "gem",
            // What the rolling and cutting machines make of an ingot.
            "plate", "large_plate", "n_doped_plate", "p_doped_plate",
            // The machine parts, from the simple shapes to the assembled ones.
            "gear", "rod", "rod_magnetic", "bolt", "ring", "rotor", "wire", "wire_magnetic", "fine_wire",
            "blade", "drill_head", "drill", "hammer",
            // The parts that are a device in their own right.
            "battery");

    private PartProgression() {}

    /**
     * Where a part sits inside its material in the creative tab.
     *
     * @param partKey - the key a part was registered under, for example {@code plate}.
     * @returns the position of the part, or the end of the list when it is not one this knows about.
     */
    public static int rankOf(String partKey) {
        var rank = ORDER.indexOf(partKey);
        return rank < 0 ? ORDER.size() : rank;
    }

    /**
     * Where a part sits inside its material in the creative tab.
     *
     * @param part - the key a part was registered under.
     * @returns the position of the part, or the end of the list when it is not one this knows about.
     */
    public static int rankOf(PartKey part) {
        return rankOf(part.key);
    }
}
