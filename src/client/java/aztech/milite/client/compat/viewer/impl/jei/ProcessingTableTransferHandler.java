package aztech.milite.client.compat.viewer.impl.jei;

import aztech.milite.MIRegistries;
import aztech.milite.blocks.processingtable.ProcessingTableRecipe;
import aztech.milite.blocks.processingtable.ProcessingTableScreenHandler;
import aztech.milite.network.machines.MoveRecipePacket;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

class ProcessingTableTransferHandler
        implements IRecipeTransferHandler<ProcessingTableScreenHandler, RecipeHolder<ProcessingTableRecipe>> {
    private final RecipeType<RecipeHolder<ProcessingTableRecipe>> type;

    ProcessingTableTransferHandler(RecipeType<RecipeHolder<ProcessingTableRecipe>> type) {
        this.type = type;
    }

    @Override
    public Class<? extends ProcessingTableScreenHandler> getContainerClass() {
        return ProcessingTableScreenHandler.class;
    }

    @Override
    public Optional<MenuType<ProcessingTableScreenHandler>> getMenuType() {
        return Optional.of(MIRegistries.PROCESSING_TABLE_MENU.get());
    }

    @Override
    public RecipeType<RecipeHolder<ProcessingTableRecipe>> getRecipeType() {
        return type;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(ProcessingTableScreenHandler menu,
            RecipeHolder<ProcessingTableRecipe> recipe, IRecipeSlotsView recipeSlots, Player player,
            boolean maxTransfer, boolean doTransfer) {
        if (doTransfer) {
            new MoveRecipePacket(menu.containerId, recipe.id(), 0, maxTransfer ? 64 : 1).sendToServer();
        }
        return null;
    }
}
