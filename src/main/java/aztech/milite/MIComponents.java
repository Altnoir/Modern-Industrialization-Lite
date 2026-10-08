package aztech.milite;

import aztech.milite.blocks.storage.ResourceStorage;
import aztech.milite.items.SteamDrillFuel;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.milite.util.MIExtraCodecs;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class MIComponents {
    private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MI.ID);

    public static final Supplier<DataComponentType<Boolean>> ACTIVATED = COMPONENTS.registerComponentType("activated",
            builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final Supplier<DataComponentType<BlockState>> CAMOUFLAGE = COMPONENTS.registerComponentType("camouflage",
            builder -> builder.persistent(BlockState.CODEC));
    public static final Supplier<DataComponentType<Long>> ENERGY = COMPONENTS.registerComponentType("energy",
            builder -> builder.persistent(MIExtraCodecs.NON_NEGATIVE_LONG));
    public static final Supplier<DataComponentType<Boolean>> LOW_SIGNAL = COMPONENTS.registerComponentType("low_signal",
            builder -> builder.persistent(Codec.BOOL));
    public static final Supplier<DataComponentType<Integer>> REMAINING_DISINTEGRATIONS = COMPONENTS.registerComponentType("remaining_disintegrations",
            builder -> builder.persistent(ExtraCodecs.POSITIVE_INT));
    public static final Supplier<DataComponentType<Boolean>> SILK_TOUCH = COMPONENTS.registerComponentType("silk_touch",
            builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final Supplier<DataComponentType<SteamDrillFuel>> STEAM_DRILL_FUEL = COMPONENTS.registerComponentType("steam_drill_fuel",
            builder -> builder.persistent(SteamDrillFuel.CODEC));
    public static final Supplier<DataComponentType<Integer>> WATER = COMPONENTS.registerComponentType("water",
            builder -> builder.persistent(ExtraCodecs.POSITIVE_INT));

    public static final Supplier<DataComponentType<SimpleFluidContent>> FLUID_CONTENT = COMPONENTS.registerComponentType("fluid_content",
            builder -> builder.persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC));
    public static final Supplier<DataComponentType<ResourceStorage<FluidVariant>>> FLUID_STORAGE = COMPONENTS.registerComponentType("fluid_storage",
            ResourceStorage.component(FluidVariant.CODEC, FluidVariant.STREAM_CODEC));
    public static final Supplier<DataComponentType<ResourceStorage<ItemVariant>>> ITEM_STORAGE = COMPONENTS.registerComponentType("item_storage",
            ResourceStorage.component(ItemVariant.CODEC, ItemVariant.STREAM_CODEC));

    public static void init(IEventBus modEventBus) {
        COMPONENTS.register(modEventBus);
    }

    private MIComponents() {}
}
