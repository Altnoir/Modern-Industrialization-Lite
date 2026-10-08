package aztech.milite.client.compat.viewer.impl.jei;

import aztech.milite.client.machines.gui.MachineMenuClient;
import aztech.milite.client.machines.guicomponents.ReiSlotLockingClient;
import aztech.milite.machines.recipe.MachineRecipe;
import aztech.milite.network.machines.ReiLockSlotsPacket;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IRecipeCatalystLookup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;
import java.util.Optional;

class MachineSlotLockingHandler implements IRecipeTransferHandler<MachineMenuClient, RecipeHolder<MachineRecipe>> {
    private final Supplier<IJeiRuntime> runtimeSupplier;
    private final mezz.jei.api.recipe.RecipeType<RecipeHolder<MachineRecipe>> type;
    private final IRecipeTransferHandlerHelper helper;

    public MachineSlotLockingHandler(IRecipeTransferHandlerHelper helper, Supplier<IJeiRuntime> runtimeSupplier,
            RecipeType<RecipeHolder<MachineRecipe>> type) {
        this.helper = helper;
        this.runtimeSupplier = runtimeSupplier;
        this.type = type;
    }

    private IRecipeCatalystLookup getLookup() {
        var runtime = runtimeSupplier.get();
        if (runtime != null) {
            return runtime.getRecipeManager().createRecipeCatalystLookup(type);
        }
        return null;
    }

    @Override
    public Class<? extends MachineMenuClient> getContainerClass() {
        return MachineMenuClient.class;
    }

    @Override
    public Optional<MenuType<MachineMenuClient>> getMenuType() {
        return Optional.empty();
    }

    @Override
    public mezz.jei.api.recipe.RecipeType<RecipeHolder<MachineRecipe>> getRecipeType() {
        return type;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(MachineMenuClient menu, RecipeHolder<MachineRecipe> recipe, IRecipeSlotsView recipeSlots,
            Player player,
            boolean maxTransfer, boolean doTransfer) {
        if (!canApply(menu))
            return helper.createInternalError();
        ReiSlotLockingClient slotLocking = menu.getComponent(ReiSlotLockingClient.class);
        if (slotLocking == null || !slotLocking.isLockingAllowed())
            return helper.createInternalError();
        if (doTransfer) {
            new ReiLockSlotsPacket(menu.containerId, recipe.id()).sendToServer();
        }
        return null;
    }

    private boolean canApply(MachineMenuClient handler) {
        // Check if the block is in the worktables - it's a hack but it should work. :P
        ResourceLocation blockId = handler.guiParams.blockId;

        var lookup = getLookup();
        if (lookup == null) {
            return false;
        }

        var item = BuiltInRegistries.ITEM.get(blockId);
        return lookup.getItemStack().anyMatch(is -> is.is(item));
    }
}
