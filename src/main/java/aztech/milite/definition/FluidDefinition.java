package aztech.milite.definition;

import aztech.milite.fluid.MIBucketItem;
import aztech.milite.fluid.MIFluid;
import aztech.milite.fluid.MIFluidBlock;
import aztech.milite.fluid.MIFluidType;
import aztech.milite.items.SortOrder;
import aztech.milite.MIBlock;
import aztech.milite.MIFluids;
import aztech.milite.MIItem;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

public class FluidDefinition extends Definition implements FluidLike {
    public static final int LOW_OPACITY = 180;
    public static final int FULL_OPACITY = 255;
    public static final int NEAR_OPACITY = 240;
    public static final int MEDIUM_OPACITY = 230;

    private final DeferredHolder<Fluid, MIFluid> fluid;
    private DeferredBlock<MIFluidBlock> fluidBlock;
    private ItemDefinition<MIBucketItem> bucketItemDefinition;
    private DeferredHolder<FluidType, MIFluidType> fluidType;

    public final int color;
    public final int opacity;

    public final boolean isGas;

    public final FluidTexture fluidTexture;

    public FluidDefinition(String englishName, String id, int color, int opacity, FluidTexture texture, boolean isGas) {
        super(englishName, id);
        this.color = color;
        this.isGas = isGas;

        fluid = MIFluids.FLUIDS.register(id, () -> new MIFluid(fluidBlock, bucketItemDefinition, fluidType, color));
        fluidBlock = MIBlock.BLOCKS.register(id, () -> new MIFluidBlock(color));
        bucketItemDefinition = MIItem.item(englishName + " Bucket",
                id + "_bucket", s -> new MIBucketItem(fluid.get(), color, s), SortOrder.BUCKETS);
        fluidType = MIFluids.FLUID_TYPES.register(id,
                () -> {
                    var props = FluidType.Properties.create()
                            .descriptionId(fluidBlock.get().getDescriptionId());
                    if (isGas) {
                        props.density(-1000);
                    }
                    return new MIFluidType(fluidBlock, props);
                });

        this.fluidTexture = texture;
        this.opacity = opacity;
    }

    @Override
    public String getTranslationKey() {
        return "block.milite." + this.getId().getPath();
    }

    @Override
    public Fluid asFluid() {
        return fluid.get();
    }

    public MIFluidBlock asFluidBlock() {
        return fluidBlock.get();
    }

    public BucketItem getBucket() {
        return bucketItemDefinition.asItem();
    }

    public FluidVariant variant() {
        return FluidVariant.of(asFluid());
    }
}
