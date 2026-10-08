package aztech.milite.client.compat.viewer.impl.rei;

import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariantAttributes;
import aztech.milite.util.FluidHelper;
import aztech.milite.util.TextHelper;
import com.google.common.primitives.Ints;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.List;

public class ReiSlotUtil {
    private ReiSlotUtil() {}

    public static EntryStack<?> createFluidEntryStack(FluidVariant fluid, long amount, float probability, boolean input) {
        @Nullable
        Component probabilityText = TextHelper.getProbabilityTooltip(probability, input);
        return EntryStacks.of(FluidStackHooksForge.fromForge(fluid.toStack(Ints.saturatedCast(amount))))
                .setting(EntryStack.Settings.TOOLTIP_PROCESSOR, (stack, oldTooltip) -> {
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(FluidVariantAttributes.getName(fluid));
                    tooltip.add(FluidHelper.getFluidAmount(amount));
                    if (probabilityText != null) {
                        tooltip.add(probabilityText);
                    }
                    return Tooltip.create(tooltip);
                });
    }

    public static EntryStack<?> createFluidNoAmount(FluidVariant fluid) {
        return EntryStacks.of(FluidStackHooksForge.fromForge(fluid.toStack(FluidType.BUCKET_VOLUME))).setting(EntryStack.Settings.TOOLTIP_PROCESSOR,
                (stack, oldTooltip) -> {
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(FluidVariantAttributes.getName(fluid));
                    return Tooltip.create(tooltip);
                });
    }

    public static Function<EntryStack<?>, List<Component>> getProbabilitySetting(float probability, boolean input) {
        @Nullable
        Component tooltip = TextHelper.getProbabilityTooltip(probability, input);
        return es -> tooltip == null ? List.of() : List.of(tooltip);
    }
}
