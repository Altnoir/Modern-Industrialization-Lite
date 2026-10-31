package aztech.milite.client.compat.viewer.impl.emi;

import aztech.milite.blocks.forgehammer.ForgeHammerScreenHandler;
import aztech.milite.client.compat.viewer.usage.ForgeHammerCategory;
import aztech.milite.network.machines.MoveRecipePacket;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class ForgeHammerRecipeHandler implements StandardRecipeHandler<ForgeHammerScreenHandler> {
    @Override
    public List<Slot> getInputSources(ForgeHammerScreenHandler handler) {
        List<Slot> inputs = new ArrayList<>();
        // Player inventory
        for (int i = 0; i < 36; ++i) {
            inputs.add(handler.getSlot(i));
        }
        inputs.add(handler.input);
        return inputs;
    }

    @Override
    public List<Slot> getCraftingSlots(ForgeHammerScreenHandler handler) {
        return List.of(handler.input);
    }

    @Override
    @Nullable
    public Slot getOutputSlot(ForgeHammerScreenHandler handler) {
        return handler.output;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().getId().equals(ForgeHammerCategory.ID);
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<ForgeHammerScreenHandler> context) {
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
