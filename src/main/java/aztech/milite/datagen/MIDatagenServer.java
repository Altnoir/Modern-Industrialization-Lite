package aztech.milite.datagen;

import aztech.milite.datagen.advancement.MIAdvancementsProvider;
import aztech.milite.datagen.datamap.MIDataMapProvider;
import aztech.milite.datagen.dynreg.DynamicRegistryDatagen;
import aztech.milite.datagen.loot.BlockLootTableProvider;
import aztech.milite.datagen.loot.MIGiftLoot;
import aztech.milite.datagen.recipe.*;
import aztech.milite.datagen.recipe.compat.CreateCompatRecipes;
import aztech.milite.datagen.structure.EmptyTestStructureGenerator;
import aztech.milite.datagen.tag.MIBlockTagProvider;
import aztech.milite.datagen.tag.MIFluidTagProvider;
import aztech.milite.datagen.tag.MIItemTagProvider;
import aztech.milite.datagen.tag.MIPoiTypeTagProvider;
import aztech.milite.datagen.translation.TranslationProvider;
import aztech.milite.MI;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.Set;

public class MIDatagenServer {
    public static void configure(
            DataGenerator gen,
            ExistingFileHelper fileHelper,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            boolean run,
            boolean runtimeDatagen) {
        var aggregate = gen.addProvider(run, new AggregateDataProvider(gen.getPackOutput(), "Server Data"));

        aggregate.addProvider(PetrochemRecipesProvider::new);
        aggregate.addProvider(PlankRecipesProvider::new);
        aggregate.addProvider(HeatExchangerRecipesProvider::new);
        aggregate.addProvider(HatchRecipesProvider::new);
        aggregate.addProvider(AlloyRecipesProvider::new);
        aggregate.addProvider(MaterialRecipesProvider::new);
        aggregate.addProvider(DyeRecipesProvider::new);
        aggregate.addProvider(ConditionalMachineRecipesProvider::new);
        aggregate.addProvider(ElectricAgeRecipesProvider::new);
        aggregate.addProvider(FixedMaterialRecipesProvider::new);
        aggregate.addProvider(FixedVanillaRecipesProvider::new);
        aggregate.addProvider(FusionRecipesProvider::new);
        aggregate.addProvider(MiscRecipesProvider::new);
        aggregate.addProvider(OilRecipesProvider::new);
        aggregate.addProvider(QuarryRecipesProvider::new);
        aggregate.addProvider(SteamAgeRecipesProvider::new);
        aggregate.addProvider(ToolsRecipesProvider::new);
        aggregate.addProvider(ProcessingTableRecipesProvider::new);
        if (!runtimeDatagen) {
            aggregate.addProvider(CreateCompatRecipes::new);
        }
        aggregate.addProvider(UpgradeProvider::new);
        aggregate.addProvider(MachineUpgradeRecipesProvider::new);
        aggregate.addProvider(VanillaCompatRecipesProvider::new);

        aggregate.addProvider(EmptyTestStructureGenerator::new);

        gen.addProvider(run, new LootTableProvider(gen.getPackOutput(), Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLootTableProvider::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(MIGiftLoot::new, LootContextParamSets.GIFT)),
                lookupProvider));

        gen.addProvider(run,
                new DatapackBuiltinEntriesProvider(gen.getPackOutput(), lookupProvider, DynamicRegistryDatagen.getBuilder(), Set.of(MI.ID)));

        gen.addProvider(run, new MIBlockTagProvider(gen.getPackOutput(), lookupProvider, fileHelper));
        gen.addProvider(run, new MIFluidTagProvider(gen.getPackOutput(), lookupProvider, fileHelper));
        gen.addProvider(run, new MIItemTagProvider(gen.getPackOutput(), lookupProvider, fileHelper, runtimeDatagen));
        gen.addProvider(run, new MIPoiTypeTagProvider(gen.getPackOutput(), lookupProvider, fileHelper));

        gen.addProvider(run, new MIDataMapProvider(gen.getPackOutput(), lookupProvider));

        var translationProvider = new TranslationProvider(gen.getPackOutput());
        gen.addProvider(run, new AdvancementProvider(gen.getPackOutput(), lookupProvider, fileHelper, List.of(
                new MIAdvancementsProvider(translationProvider))));

        // Must either remain separate or be made to use futures to wait for dependencies!
        gen.addProvider(run, translationProvider);
    }
}
