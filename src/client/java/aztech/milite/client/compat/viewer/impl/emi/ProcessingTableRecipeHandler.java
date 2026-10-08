package aztech.milite.client.compat.viewer.impl.emi;

import aztech.milite.blocks.processingtable.ProcessingTableScreenHandler;
import aztech.milite.client.compat.viewer.usage.ProcessingTableCategory;
import aztech.milite.network.machines.MoveRecipePacket;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class ProcessingTableRecipeHandler implements StandardRecipeHandler<ProcessingTableScreenHandler> {
    @Override
    public List<Slot> getInputSources(ProcessingTableScreenHandler handler) {
        List<Slot> inputs = new ArrayList<>();
        // Player inventory
        for (int i = 0; i < 36; ++i) {
            inputs.add(handler.getSlot(i));
        }
        // Extra
        inputs.add(handler.firstInput);
        inputs.add(handler.secondInput);
        return inputs;
    }

    @Override
    public List<Slot> getCraftingSlots(ProcessingTableScreenHandler handler) {
        return List.of(handler.firstInput, handler.secondInput);
    }

    @Override
    @Nullable
    public Slot getOutputSlot(ProcessingTableScreenHandler handler) {
        return handler.output;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().getId().equals(ProcessingTableCategory.ID);
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<ProcessingTableScreenHandler> context) {
        new MoveRecipePacket(
                context.getScreenHandler().containerId,
                recipe.getId(),
                switch (context.getDestination()) {
                    case NONE -> 0;
                    case CURSOR -> 1;
                    case INVENTORY -> 2;
                },
                context.getAmount()).sendToServer();

        Minecraft.getInstance().setScreen(context.getScreen());
        return true;
    }
}
