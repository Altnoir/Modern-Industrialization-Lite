package aztech.milite.items;

import aztech.milite.MIItem;
import aztech.milite.MIText;
import com.altnoir.abysslib.creative.CreativeTabSection;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static final List<CreativeTabSection> ALL = List.of(ITEMS, NUCLEAR, STORAGE, MACHINE_PARTS, MACHINES, ORES, MATERIALS, FLUIDS, MISC);

    private static final Map<Integer, CreativeTabSection> BY_GROUP = byGroup();

    private CreativeSections() {
    }

    public static void populate() {
        MIItem.ITEM_DEFINITIONS.values().stream()
                .sorted(Comparator.comparing(entry -> entry.sortOrder))
                .forEach(entry -> sectionOf(entry.sortOrder).add(entry));
    }

    private static CreativeTabSection sectionOf(SortOrder order) {
        var section = BY_GROUP.get(order.groupIndex());
        if (section == null) {
            throw new IllegalStateException("No creative tab section was given the sort order group of " + order
                    + ". Every group of SortOrder needs one, see CreativeSections.byGroup().");
        }
        return section;
    }

    private static Map<Integer, CreativeTabSection> byGroup() {
        var byGroup = new HashMap<Integer, CreativeTabSection>();
        add(byGroup, ITEMS, SortOrder.GUIDE_BOOK, SortOrder.FORGE_HAMMER, SortOrder.PROCESSING_TABLE, SortOrder.ITEMS_ORDERED.root());
        add(byGroup, NUCLEAR, SortOrder.NUCLEAR.root());
        add(byGroup, STORAGE, SortOrder.TANKS, SortOrder.BARRELS);
        add(byGroup, MACHINE_PARTS, SortOrder.STRUCTURES);
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
