package aztech.milite.client.compat.guideme.recipe.header;

import aztech.milite.compat.rei.machines.MachineCategoryParams;
import aztech.milite.machines.recipe.MachineRecipe;
import guideme.document.block.LytAxisBox;
import guideme.document.interaction.GuideTooltip;
import guideme.document.interaction.InteractiveElement;
import guideme.document.LytRect;
import guideme.layout.LayoutContext;
import guideme.layout.Layouts;

import java.util.Optional;

public class LytMachineRecipeHeader extends LytAxisBox implements InteractiveElement {
    private final MachineCategoryParams params;
    private final MachineRecipe recipe;

    private final int width;

    public LytMachineRecipeHeader(MachineCategoryParams params, MachineRecipe recipe, int width) {
        this.params = params;
        this.recipe = recipe;
        this.width = width;
    }

    @Override
    protected LytRect computeBoxLayout(LayoutContext context, int x, int y, int availableWidth) {
        return Layouts.horizontalLayout(context,
                children,
                x,
                y,
                Math.min(width, availableWidth),
                true,
                0,
                0,
                0,
                0,
                getGap(),
                getAlignItems(),
                false);
    }

    @Override
    public Optional<GuideTooltip> getTooltip(float x, float y) {
        return Optional.of(new MachineRecipeHeaderTooltip(params, recipe));
    }
}
