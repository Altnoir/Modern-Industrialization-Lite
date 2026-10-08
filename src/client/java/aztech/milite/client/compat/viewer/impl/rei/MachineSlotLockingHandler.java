package aztech.milite.client.compat.viewer.impl.rei;

import aztech.milite.client.machines.gui.MachineMenuClient;
import aztech.milite.client.machines.guicomponents.ReiSlotLockingClient;
import aztech.milite.machines.recipe.MachineRecipe;
import aztech.milite.network.machines.ReiLockSlotsPacket;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

class MachineSlotLockingHandler implements TransferHandler {
    @Override
    public Result handle(Context context) {
        if (!(context.getDisplay() instanceof ViewerCategoryRei<?>.ViewerDisplay d && d.recipe instanceof RecipeHolder<?>(
                ResourceLocation id, Object value
        )
                && value instanceof MachineRecipe recipe))
            return Result.createNotApplicable();
        if (!(context.getMenu() instanceof MachineMenuClient handler))
            return Result.createNotApplicable();
        if (!canApply(handler, d.getCategoryIdentifier()))
            return Result.createNotApplicable();
        ReiSlotLockingClient slotLocking = handler.getComponent(ReiSlotLockingClient.class);
        if (slotLocking == null || !slotLocking.isLockingAllowed())
            return Result.createNotApplicable();
        if (context.isActuallyCrafting()) {
            new ReiLockSlotsPacket(handler.containerId, id).sendToServer();
        }
        return Result.createSuccessful().blocksFurtherHandling();
    }

    private boolean canApply(MachineMenuClient handler, CategoryIdentifier<?> category) {
        // Check if the block is in the worktables - it's a hack but it should work. :P
        ResourceLocation blockId = handler.guiParams.blockId;
        List<EntryIngredient> workstations = CategoryRegistry.getInstance().get(category).getWorkstations();
        for (EntryIngredient workstationEntries : workstations) {
            for (EntryStack<?> entry : workstationEntries) {
                Item item = entry.<ItemStack>cast().getValue().getItem();
                if (BuiltInRegistries.ITEM.getKey(item).equals(blockId)) {
                    return true;
                }
            }
        }
        return false;
    }
}
