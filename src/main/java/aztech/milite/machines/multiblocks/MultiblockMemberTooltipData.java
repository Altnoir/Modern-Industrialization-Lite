package aztech.milite.machines.multiblocks;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jspecify.annotations.Nullable;

public record MultiblockMemberTooltipData(RegistryAccess registries, SimpleMember member, @Nullable HatchFlags hatchFlags)
        implements TooltipComponent {
}
