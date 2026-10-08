package aztech.milite.blocks.storage.barrel;

import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record BarrelTooltipData(ItemVariant variant, long amount, long capacity, boolean creative) implements TooltipComponent {}
