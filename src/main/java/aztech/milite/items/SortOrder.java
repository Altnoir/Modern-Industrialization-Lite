package aztech.milite.items;

import aztech.milite.nuclear.NuclearOrder;

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
    public static final SortOrder GUIDE_BOOK = new SortOrder();
    public static final SortOrder FORGE_HAMMER = new SortOrder();
    public static final SortOrder PROCESSING_TABLE = new SortOrder();

    public static final SortOrderGroup.Counted ITEMS_ORDERED = new SortOrderGroup.Counted(new SortOrder());
    public static final SortOrderGroup.Parametrized<NuclearOrder> NUCLEAR = new SortOrderGroup.Parametrized<>(new SortOrder());

    public static final SortOrder TANKS = new SortOrder();
    public static final SortOrder BARRELS = new SortOrder();

    private static final int KIND_CASING = 0;
    private static final int KIND_HULL = 1;
    private static final int KIND_HATCH = 2;
    private static final int KIND_COIL = 3;
    private static final int KIND_FUSION_CHAMBER = 4;

    public static final SortOrder STRUCTURES = new SortOrder();
    public static final SortOrder CASINGS = STRUCTURES.and(KIND_CASING);
    public static final SortOrder MACHINE_HULLS = STRUCTURES.and(KIND_HULL);
    public static final SortOrder HATCHES = STRUCTURES.and(KIND_HATCH);
    public static final SortOrder COILS = STRUCTURES.and(KIND_COIL);
    public static final SortOrder FUSION_CHAMBER = STRUCTURES.and(KIND_FUSION_CHAMBER);

    public static final SortOrder MACHINES = new SortOrder();
    public static final SortOrder ORES = new SortOrder();
    public static final SortOrder RAW_ORE_BLOCKS = new SortOrder();
    public static final SortOrder STORAGE_BLOCKS = new SortOrder();
    public static final SortOrder MATERIALS = new SortOrder();
    public static final SortOrder BUCKETS = new SortOrder();
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
     * Puts a level ahead of the kind this order names, so that every structure of one level is listed together instead
     * of one kind of structure after another.
     */
    public SortOrder withLevel(Comparable level) {
        Comparable[] newArray = new Comparable[objects.length + 1];
        newArray[0] = objects[0];
        newArray[1] = level;
        System.arraycopy(objects, 1, newArray, 2, objects.length - 1);
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

    /**
     * Whether this order names a block that is part of a machine: a machine itself, or a casing, hull, coil, hatch or
     * fusion chamber it is built from. Those blocks are heard as metal rather than as the stone they are made of.
     */
    public boolean isMachineStructure() {
        int group = groupIndex();
        return group == MACHINES.groupIndex() || group == CASINGS.groupIndex() || group == MACHINE_HULLS.groupIndex()
                || group == COILS.groupIndex() || group == HATCHES.groupIndex() || group == FUSION_CHAMBER.groupIndex();
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
