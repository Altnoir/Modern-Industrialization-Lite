package aztech.milite.datagen.datamap;

import aztech.milite.*;
import aztech.milite.api.datamaps.FluidFuel;
import aztech.milite.api.datamaps.ItemTooltip;
import aztech.milite.api.datamaps.MIDataMaps;
import aztech.milite.datagen.loot.MILootTables;
import aztech.milite.definition.FluidDefinition;
import aztech.milite.materials.MaterialRegistry;
import aztech.milite.materials.part.MIParts;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.RaidHeroGift;

import java.util.concurrent.CompletableFuture;
import java.util.List;

public class MIDataMapProvider extends DataMapProvider {
    public MIDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider registries) {
        gatherFurnaceFuels();

        gatherFluidFuels();
        gatherItemTooltips();

        builder(NeoForgeDataMaps.RAID_HERO_GIFTS)
                .add(MIRegistries.INDUSTRIALIST, new RaidHeroGift(MILootTables.INDUSTRIALIST_GIFT), false);
    }

    private void gatherFurnaceFuels() {
        addFuel("coke", 6400);
        addFuel("coke_dust", 6400);
        addFuel("coke_block", 6400 * 9);
        addFuel("coal_crushed_dust", 1600);
        builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(ItemTags.create(ResourceLocation.parse("c:dusts/coal")), new FurnaceFuel(1600), false);
        addFuel("coal_tiny_dust", 160);
        addFuel("lignite_coal", 1600);
        addFuel("lignite_coal_block", 16000);
        addFuel("lignite_coal_crushed_dust", 1600);
        addFuel("lignite_coal_dust", 1600);
        addFuel("lignite_coal_tiny_dust", 160);
        addFuel("carbon_dust", 6400);
        addFuel("carbon_tiny_dust", 640);
        addFuel(MIItem.GUIDE_BOOK.path(), 300);
    }

    private void addFuel(String path, int value) {
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(MI.id(path), new FurnaceFuel(value), false);
    }

    private void gatherFluidFuels() {
        int creosote = 160;

        addFluidFuel(MIFluids.HYDROGEN, 2);
        addFluidFuel(MIFluids.CRUDE_OIL, 16);
        addFluidFuel(MIFluids.SYNTHETIC_OIL, 16);
        addFluidFuel(MIFluids.RAW_BIODIESEL, 50);
        addFluidFuel(MIFluids.NAPHTHA, 80);
        addFluidFuel(MIFluids.CREOSOTE, creosote);
        addFluidFuel(MIFluids.LIGHT_FUEL, 160);
        addFluidFuel(MIFluids.HEAVY_FUEL, 240);
        addFluidFuel(MIFluids.BIODIESEL, 250);
        addFluidFuel(MIFluids.DIESEL, 400);
        addFluidFuel(MIFluids.BOOSTED_DIESEL, 800);

        addFluidFuel(ResourceLocation.fromNamespaceAndPath("immersiveengineering", "creosote"), creosote, new ModLoadedCondition("immersiveengineering"));
    }

    private void addFluidFuel(FluidDefinition fluidDefinition, int fePerMb) {
        addFluidFuel(fluidDefinition.getId(), fePerMb);
    }

    private void addFluidFuel(ResourceLocation fluidId, int fePerMb, ICondition... conditions) {
        builder(MIDataMaps.FLUID_FUELS).add(fluidId, new FluidFuel(fePerMb), false, conditions);
    }

    private void gatherItemTooltips() {
        addItemTooltip("capacitor", MIText.HasBetterYieldAssemblerRecipe);
        addItemTooltip("inductor", MIText.HasBetterYieldAssemblerRecipe);
        addItemTooltip("resistor", MIText.HasBetterYieldAssemblerRecipe);
        // Gears get 2x the yield using the assembler.
        for (var material : MaterialRegistry.getMaterials().values()) {
            var part = material.getNullablePart(MIParts.GEAR);
            if (part != null) {
                addItemTooltip(part.getItemId().split(":")[1], MIText.HasBetterYieldAssemblerRecipe);
            }
        }
    }

    private void addItemTooltip(String path, MIText text) {
        var component = text.text().setStyle(MITooltips.DEFAULT_STYLE);
        builder(MIDataMaps.ITEM_TOOLTIPS).add(MI.id(path), new ItemTooltip(List.of(component)), false);
    }
}
