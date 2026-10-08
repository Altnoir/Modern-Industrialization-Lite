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

package aztech.milite.client.datagen.model;

import aztech.milite.MI;
import aztech.milite.MIBlock;
import aztech.milite.MIFluids;
import aztech.milite.MIItem;
import aztech.milite.datagen.model.BaseModelProvider;
import aztech.milite.datagen.model.MachineModelsToGenerate;
import aztech.milite.definition.BlockDefinition;
import aztech.milite.definition.FluidDefinition;
import aztech.milite.definition.ItemDefinition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MIModelProvider extends BaseModelProvider {
    public MIModelProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (BlockDefinition<?> blockDefinition : MIBlock.BLOCK_DEFINITIONS.values()) {
            blockDefinition.modelGenerator.accept(blockDefinition.asBlock(), this);
        }

        for (FluidDefinition fluidDefinition : MIFluids.FLUID_DEFINITIONS.values()) {
            existingModel(fluidDefinition.asFluidBlock(), Blocks.AIR);
        }

        // Item models as well...
        for (ItemDefinition<?> itemDefinition : MIItem.ITEM_DEFINITIONS.values()) {
            itemDefinition.modelGenerator.accept(itemDefinition.asItem(), itemModels());
        }

        // Machine models
        for (var entry : MachineModelsToGenerate.props.entrySet()) {
            simpleBlockWithItem(BuiltInRegistries.BLOCK.get(MI.id(entry.getKey())), models()
                    .getBuilder(entry.getKey())
                    .customLoader((bmb, exFile) -> new MachineModelBuilder<>(entry.getValue(), bmb, exFile))
                    .end());
        }
    }
}
