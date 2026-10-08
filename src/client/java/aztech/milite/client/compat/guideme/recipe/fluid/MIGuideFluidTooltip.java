package aztech.milite.client.compat.guideme.recipe.fluid;

import aztech.milite.MITooltips;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariantAttributes;
import aztech.milite.util.FluidHelper;
import aztech.milite.util.TextHelper;
import guideme.document.interaction.GuideTooltip;
import guideme.siteexport.ResourceExporter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforgespi.language.IModInfo;

import java.util.ArrayList;
import java.util.List;

public class MIGuideFluidTooltip implements GuideTooltip {
    private final FluidVariant fluid;
    private final ItemStack icon;
    private final long amount;
    private final float probability;
    private final boolean input;

    public MIGuideFluidTooltip(FluidVariant fluid, long amount, float probability, boolean input) {
        this.fluid = fluid;
        this.icon = fluid.getFluid().getFluidType().getBucket(fluid.toStack(1));
        this.amount = amount;
        this.probability = probability;
        this.input = input;
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }

    @Override
    public List<ClientTooltipComponent> getLines() {
        List<Component> lines = new ArrayList<>();

        lines.add(FluidVariantAttributes.getName(fluid));

        if (Minecraft.getInstance().options.advancedItemTooltips) {
            lines.add(Component.literal(BuiltInRegistries.FLUID.getKey(fluid.getFluid()).toString()).withStyle(ChatFormatting.DARK_GRAY));
        }

        if (amount > 1) {
            lines.add(FluidHelper.getFluidAmount(amount).withStyle(MITooltips.DEFAULT_STYLE));
        }

        var modDisplayName = ModList.get()
                .getModContainerById(NeoForgeRegistries.FLUID_TYPES.getKey(fluid.getFluid().getFluidType()).getNamespace())
                .map(ModContainer::getModInfo)
                .map(IModInfo::getDisplayName);
        if (modDisplayName.isPresent()) {
            lines.add(Component.literal(modDisplayName.orElse("")).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
        }

        if (probability != 1) {
            lines.add(TextHelper.getProbabilityTooltip(probability, input));
        }

        return lines.stream()
                .map(Component::getVisualOrderText)
                .map(ClientTooltipComponent::create)
                .toList();
    }

    @Override
    public void exportResources(ResourceExporter exporter) {
        exporter.referenceFluid(fluid.getFluid());
    }
}
