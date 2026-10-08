package aztech.milite.items;

import aztech.milite.MIItem;
import aztech.milite.MIText;
import com.altnoir.abysslib.creative.CreativeTabSection;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The sections of the general creative tab, in the order the tab lists them.
 *
 * <p>
 * A section is a heading inside the tab rather than a tab of its own: the tab stays one list, and a section only claims
 * the start of its first row, which the library's renderer draws the heading on. Which section an entry lands in is
 * decided by the group of its {@link SortOrder}, which is the order the tab is sorted by anyway, so the sections say
 * out loud what the order already meant.
 */
public final class CreativeSections {
    public static final CreativeTabSection ITEMS = section(MIText.SectionItems);
    public static final CreativeTabSection NUCLEAR = section(MIText.SectionNuclear);
    public static final CreativeTabSection STORAGE = section(MIText.SectionStorage);
    public static final CreativeTabSection MACHINE_PARTS = section(MIText.SectionMachineParts);
    public static final CreativeTabSection MACHINES = section(MIText.SectionMachines);
    public static final CreativeTabSection ORES = section(MIText.SectionOres);
    public static final CreativeTabSection MATERIALS = section(MIText.SectionMaterials);
    public static final CreativeTabSection FLUIDS = section(MIText.SectionFluids);
    public static final CreativeTabSection MISC = section(MIText.SectionMisc);

    /**
     * The sections in the order the tab lists them. The order here is the order of the tab, because the tab walks its
     * sections in the order it was given them.
     */
    public static final List<CreativeTabSection> ALL = List.of(ITEMS, NUCLEAR, STORAGE, MACHINE_PARTS, MACHINES, ORES,
            MATERIALS, FLUIDS, MISC);

    /**
     * The section each group of {@link SortOrder} belongs to.
     */
    private static final Map<Integer, CreativeTabSection> BY_GROUP = byGroup();

    private CreativeSections() {
    }

    /**
     * Add every item to the section of its group, in the order the tab shows them.
     *
     * <p>
     * The tab calls this every time it rebuilds its contents, and it has emptied the sections by then, so this adds
     * every entry again rather than only the ones that changed.
     */
    public static void populate() {
        MIItem.ITEM_DEFINITIONS.values().stream()
                .sorted(Comparator.comparing(entry -> entry.sortOrder))
                .forEach(entry -> sectionOf(entry.sortOrder).add(entry));
    }

    /**
     * Which section an entry belongs to.
     *
     * @throws IllegalStateException when a group of {@link SortOrder} was never given a section, which would otherwise
     *                               put its entries in the wrong place without saying so.
     */
    private static CreativeTabSection sectionOf(SortOrder order) {
        var section = BY_GROUP.get(order.groupIndex());
        if (section == null) {
            throw new IllegalStateException("No creative tab section was given the sort order group of " + order
                    + ". Every group of SortOrder needs one, see CreativeSections.byGroup().");
        }
        return section;
    }

    /**
     * The sections and the groups they own. A group that is missing here throws rather than quietly joining a
     * neighbouring section.
     */
    private static Map<Integer, CreativeTabSection> byGroup() {
        var byGroup = new HashMap<Integer, CreativeTabSection>();
        // The guidebook comes first along with the two blocks it starts a player on: hammering and the processing table
        // are where the mod begins, and they belong with the rest of the items rather than under a heading of their own.
        add(byGroup, ITEMS, SortOrder.GUIDE_BOOK, SortOrder.FORGE_HAMMER, SortOrder.PROCESSING_TABLE,
                SortOrder.ITEMS_ORDERED.root());
        add(byGroup, NUCLEAR, SortOrder.NUCLEAR.root());
        add(byGroup, STORAGE, SortOrder.TANKS, SortOrder.BARRELS);
        // Everything a machine or a multiblock is built from: only the machines themselves belong to the next section.
        add(byGroup, MACHINE_PARTS, SortOrder.CASINGS, SortOrder.MACHINE_HULLS, SortOrder.COILS, SortOrder.HATCHES,
                SortOrder.FUSION_CHAMBER);
        add(byGroup, MACHINES, SortOrder.MACHINES);
        add(byGroup, ORES, SortOrder.ORES, SortOrder.RAW_ORE_BLOCKS, SortOrder.STORAGE_BLOCKS);
        add(byGroup, MATERIALS, SortOrder.MATERIALS);
        add(byGroup, FLUIDS, SortOrder.BUCKETS);
        add(byGroup, MISC, SortOrder.BLOCKS_OTHERS);
        return Map.copyOf(byGroup);
    }

    private static void add(Map<Integer, CreativeTabSection> byGroup, CreativeTabSection section, SortOrder... groups) {
        for (var group : groups) {
            if (byGroup.put(group.groupIndex(), section) != null) {
                throw new IllegalStateException("Two creative tab sections claim the sort order group of " + group);
            }
        }
    }

    private static CreativeTabSection section(MIText title) {
        return new CreativeTabSection(title.getTranslationKey());
    }
}
