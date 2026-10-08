package aztech.milite.datagen.tag;

import aztech.milite.machines.blockentities.ReplicatorMachineBlockEntity;
import aztech.milite.MI;
import aztech.milite.MIFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.Tags;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MIFluidTagProvider extends FluidTagsProvider {
    public MIFluidTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MI.ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        for (var def : MIFluids.FLUID_DEFINITIONS.values()) {
            if (def.isGas) {
                tag(Tags.Fluids.GASEOUS).add(def.asFluid());
            }

            // Give a #c: tag to every MI fluid. That should allow other mods to use MI's fluids in many cases.
            tag(FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", def.path())))
                    .add(def.asFluid());
        }

        tag(ReplicatorMachineBlockEntity.BLACKLISTED_FLUIDS)
                .add(MIFluids.UU_MATTER.asFluid());
    }
}
