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

import aztech.modern_industrialization.items.CreativeSections;
import aztech.modern_industrialization.test.framework.MIGameTest;
import aztech.modern_industrialization.test.framework.MIGameTestHelper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import net.minecraft.core.registries.BuiltInRegistries;

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

            var path = Path.of(System.getProperty("java.io.tmpdir"), "milite_creative_order.txt");
            Files.write(path, lines);
        } catch (Exception e) {
            helper.fail("could not dump the order: " + e);
        }
        helper.succeed();
    }
}
