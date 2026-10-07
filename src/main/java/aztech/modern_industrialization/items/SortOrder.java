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

package aztech.modern_industrialization.items;

import aztech.modern_industrialization.nuclear.NuclearOrder;

/**
 * Where an entry sits in the general creative tab.
 *
 * <p>
 * The order of the constants below is the order of the tab, because each root order takes the next number for itself
 * when the class is initialised. Inserting a constant therefore moves everything declared after it, so the list is
 * written as the sequence a player reads: the two blocks that start the mod, the hand ordered items, then the blocks by
 * family, then the material forms, and the leftovers last.
 *
 * <p>
 * Two entries that carry the same order compare equal and keep the order they came in, which is the order of
 * {@code MIItem.ITEM_DEFINITIONS}: the id, alphabetically. A group that wants a better order than that has to say so
 * with {@link #and}.
 */
public final class SortOrder implements Comparable<SortOrder> {
    /**
     * The guidebook, so the first thing in the tab is how to play.
     */
    public static final SortOrder GUIDE_BOOK = new SortOrder();
    /**
     * The forge hammer and the processing table, the two blocks the mod starts with.
     */
    public static final SortOrder FORGE_HAMMER = new SortOrder();
    public static final SortOrder PROCESSING_TABLE = new SortOrder();
    /**
     * Loose items: components, circuits, tools and armour, numbered in the order they are declared in MIItem.
     */
    public static final SortOrderGroup.Counted ITEMS_ORDERED = new SortOrderGroup.Counted(new SortOrder());
    /**
     * Nuclear parts, ordered by their role and then by material.
     */
    public static final SortOrderGroup.Parametrized<NuclearOrder> NUCLEAR = new SortOrderGroup.Parametrized<>(new SortOrder());
    /**
     * Blocks by family: what stores things, what a machine is built from, then the machines themselves.
     */
    public static final SortOrder TANKS = new SortOrder();
    public static final SortOrder BARRELS = new SortOrder();
    public static final SortOrder CASINGS = new SortOrder();
    /**
     * The machine hull a machine is built into, which belongs beside the casings rather than with the leftovers.
     */
    public static final SortOrder MACHINE_HULLS = new SortOrder();
    public static final SortOrder COILS = new SortOrder();
    /**
     * Multiblock hatches, which a multiblock uses to exchange items, fluids and energy with the outside.
     */
    public static final SortOrder HATCHES = new SortOrder();
    /**
     * The fusion chamber, a structural block of the fusion reactor rather than a block in its own right.
     */
    public static final SortOrder FUSION_CHAMBER = new SortOrder();
    public static final SortOrder MACHINES = new SortOrder();
    /**
     * The material forms, in the order a player meets them: ore, raw block, block, then the items made from it.
     */
    public static final SortOrder ORES = new SortOrder();
    public static final SortOrder RAW_ORE_BLOCKS = new SortOrder();
    public static final SortOrder STORAGE_BLOCKS = new SortOrder();
    public static final SortOrder MATERIALS = new SortOrder();
    public static final SortOrder BUCKETS = new SortOrder();
    /**
     * The default for a block that never states an order, and the home of the odds and ends. It is last on purpose: a
     * block that forgets to say where it goes is then found at the end rather than in the middle of a family.
     */
    public static final SortOrder BLOCKS_OTHERS = new SortOrder();

    private static int nextGlobalOrder = 0;

    private final Comparable[] objects;

    // Root constructor
    private SortOrder() {
        this(nextGlobalOrder++);
    }

    private SortOrder(Comparable... objects) {
        this.objects = objects;
    }

    public SortOrder and(Comparable anotherObject) {
        Comparable[] newArray = new Comparable[objects.length + 1];
        System.arraycopy(objects, 0, newArray, 0, objects.length);
        newArray[objects.length] = anotherObject;
        return new SortOrder(newArray);
    }

    /**
     * The group this order belongs to: the constant it was derived from, or itself when it is a constant. A group takes
     * the next number for itself when the class is initialised and everything derived from it with {@link #and} carries
     * that number first, so this names the group without having to keep a list of the constants.
     */
    public int groupIndex() {
        return ((Number) objects[0]).intValue();
    }

    @Override
    public int compareTo(SortOrder o) {
        int i;
        for (i = 0; i < Math.min(o.objects.length, objects.length); ++i) {
            @SuppressWarnings("unchecked")
            int comp = objects[i].compareTo(o.objects[i]);
            if (comp != 0) {
                return comp;
            }
        }

        if (i < objects.length) {
            return 1;
        } else if (i < o.objects.length) {
            return -1;
        } else {
            return 0;
        }
    }
}
