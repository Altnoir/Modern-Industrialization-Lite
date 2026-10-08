package aztech.milite.client;

import aztech.milite.client.util.RenderHelper;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.function.Supplier;
import java.util.List;

public class DynamicTooltip extends Tooltip {
    private final Supplier<List<Component>> tooltipSupplier;

    public DynamicTooltip(Supplier<List<Component>> tooltipSupplier) {
        super(null, null);
        this.tooltipSupplier = tooltipSupplier;
    }

    @Override
    public List<FormattedCharSequence> toCharSequence(Minecraft minecraft) {
        return RenderHelper.splitTooltip(tooltipSupplier.get());
    }
}
