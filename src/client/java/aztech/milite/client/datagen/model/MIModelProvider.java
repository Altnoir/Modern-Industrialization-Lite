package aztech.milite.client.datagen.model;

import aztech.milite.datagen.model.BaseModelProvider;
import aztech.milite.datagen.model.MachineModelsToGenerate;
import aztech.milite.definition.BlockDefinition;
import aztech.milite.definition.FluidDefinition;
import aztech.milite.definition.ItemDefinition;
import aztech.milite.MI;
import aztech.milite.MIBlock;
import aztech.milite.MIFluids;
import aztech.milite.MIItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
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

        // Athena's connected-texture block states only apply in the world. Use vanilla cube models for block items
        // so they keep the non-connected base textures and never inherit the CTM model's atlas mapping.
        for (String blockId : new String[] {
                "blastproof_casing", "bronze_machine_casing_pipe", "bronze_plated_bricks",
                "clean_stainless_steel_machine_casing", "cupronickel_coil", "frostproof_machine_casing",
                "heatproof_machine_casing", "iridium_machine_casing_pipe", "kanthal_coil",
                "nuclear_alloy_machine_casing_pipe", "nuclear_casing", "plasma_handling_iridium_machine_casing",
                "solid_titanium_machine_casing", "stainless_steel_machine_casing_pipe", "steel_machine_casing_pipe",
                "superconductor_coil", "titanium_machine_casing_pipe" }) {
            var itemModel = itemModels().getBuilder(blockId)
                    .parent(itemModels().getExistingFile(ResourceLocation.withDefaultNamespace("block/cube_all")));
            if (blockId.endsWith("_coil")) {
                itemModel.texture("side", MI.id("block/" + blockId + "_side"))
                        .texture("end", MI.id("block/" + blockId + "_top"));
            } else {
                itemModel.texture("all", MI.id("block/" + blockId));
            }
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
