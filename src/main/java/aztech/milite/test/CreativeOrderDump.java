package aztech.milite.test;

import aztech.milite.items.CreativeSections;
import aztech.milite.test.framework.MIGameTest;
import aztech.milite.test.framework.MIGameTestHelper;
import net.minecraft.core.registries.BuiltInRegistries;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Writes the layout of the general creative tab, section by section, to a file, so that the layout can be reviewed
 * without launching the game.
 *
 * <p>
 * It fills the sections the same way the tab does and then reads them back, so what it writes is what the tab shows. It
 * asserts nothing; it exists to be read.
 */
public class CreativeOrderDump {
    @MIGameTest
    public void dumpCreativeOrder(MIGameTestHelper helper) {
        try {
            CreativeSections.populate();

            var lines = new ArrayList<String>();
            var total = 0;
            for (var section : CreativeSections.ALL) {
                var stacks = section.itemStacks();
                total += stacks.size();
                lines.add("");
                lines.add("======== " + section.translationKey() + "  (" + stacks.size() + " entries) ========");
                for (var stack : stacks) {
                    lines.add("  " + BuiltInRegistries.ITEM.getKey(stack.getItem()));
                }
            }
            lines.add("");
            lines.add("total entries: " + total);

            var path = Path.of(System.getProperty("java.io.tmpdir"), "moderntech_creative_order.txt");
            Files.write(path, lines);
        } catch (Exception e) {
            helper.fail("could not dump the order: " + e);
        }
        helper.succeed();
    }
}
