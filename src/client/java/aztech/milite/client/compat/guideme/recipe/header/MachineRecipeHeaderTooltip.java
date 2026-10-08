package aztech.milite.client.compat.guideme.recipe.header;

import aztech.milite.compat.rei.machines.MachineCategoryParams;
import aztech.milite.machines.recipe.MachineRecipe;
import guideme.document.interaction.GuideTooltip;
import guideme.siteexport.ResourceExporter;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

import java.util.List;

public class MachineRecipeHeaderTooltip implements GuideTooltip {
    private final MachineCategoryParams params;
    private final MachineRecipe recipe;

    public MachineRecipeHeaderTooltip(MachineCategoryParams params, MachineRecipe recipe) {
        this.params = params;
        this.recipe = recipe;
    }

    @Override
    public List<ClientTooltipComponent> getLines() {
        return params.getRecipeTooltip(recipe).stream()
                .map(Component::getVisualOrderText)
                .map(ClientTooltipComponent::create)
                .toList();
    }

    @Override
    public void exportResources(ResourceExporter exporter) {}
}
