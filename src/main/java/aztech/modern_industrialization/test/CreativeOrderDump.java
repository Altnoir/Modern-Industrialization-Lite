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

package aztech.modern_industrialization.test;

import aztech.modern_industrialization.MIItem;
import aztech.modern_industrialization.items.SortOrder;
import aztech.modern_industrialization.test.framework.MIGameTest;
import aztech.modern_industrialization.test.framework.MIGameTestHelper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Writes the general tab order to a file, so a layout can be reviewed without launching the game. It sorts exactly like
 * the tab does, group separators included, and names no group because the groups are anonymous in the sort chain.
 */
public class CreativeOrderDump {
    @MIGameTest
    public void dumpCreativeOrder(MIGameTestHelper helper) {
        try {
            var lines = new ArrayList<String>();
            Object previousRoot = null;
            int group = 0;
            int count = 0;
            for (var entry : MIItem.ITEM_DEFINITIONS.values().stream().sorted(Comparator.comparing(e -> e.sortOrder)).toList()) {
                var root = rootOf(entry.sortOrder);
                if (root != previousRoot) {
                    if (group > 0) {
                        lines.add("  (" + count + " entries)");
                    }
                    group++;
                    count = 0;
                    previousRoot = root;
                    lines.add("");
                    lines.add("======== group " + group + ", sort index " + root + " ========");
                }
                count++;
                lines.add("  " + entry.getId() + "   " + entry.getEnglishName());
            }
            lines.add("  (" + count + " entries)");
            var path = Path.of(System.getProperty("java.io.tmpdir"), "milite_creative_order.txt");
            Files.write(path, lines);
        } catch (Exception e) {
            helper.fail("could not dump the order: " + e);
        }
        helper.succeed();
    }

    /**
     * The number of the root order an entry was derived from: the first element of an {@code and} chain, or the number
     * a root carries itself. Reflection because the chain is private and this only feeds a debug dump.
     */
    private static Object rootOf(SortOrder order) throws ReflectiveOperationException {
        var field = SortOrder.class.getDeclaredField("objects");
        field.setAccessible(true);
        var objects = (Comparable<?>[]) field.get(order);
        return objects.length == 0 ? order : objects[0];
    }
}
