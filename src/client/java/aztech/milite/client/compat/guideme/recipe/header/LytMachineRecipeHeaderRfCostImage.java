package aztech.milite.client.compat.guideme.recipe.header;

import aztech.milite.client.compat.guideme.MIGuideMeRenderHelper;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.compat.rei.machines.SteamMode;
import aztech.milite.MI;
import guideme.document.block.LytBlock;
import guideme.document.LytRect;
import guideme.layout.LayoutContext;
import guideme.render.RenderContext;
import net.minecraft.client.renderer.MultiBufferSource;

public class LytMachineRecipeHeaderRfCostImage extends LytBlock {
    private final SteamMode steamMode;

    public LytMachineRecipeHeaderRfCostImage(SteamMode steamMode) {
        this.steamMode = steamMode;
    }

    @Override
    protected LytRect computeLayout(LayoutContext context, int x, int y, int availableWidth) {
        return new LytRect(x, y, 9, 9);
    }

    @Override
    protected void onLayoutMoved(int deltaX, int deltaY) {}

    @Override
    public void renderBatch(RenderContext context, MultiBufferSource buffers) {}

    @Override
    public void render(RenderContext context) {
        var bounds = getBounds();
        switch (steamMode) {
            case BOTH -> MIGuideMeRenderHelper.fillTexturedRect(context, MachineScreen.SLOT_ATLAS, bounds.x(), bounds.y(), 9, 9, 80, 18, 20, 20);
            case STEAM_ONLY -> MIGuideMeRenderHelper.fillTexturedRect(context, MI.id("textures/item/steam_bucket.png"), bounds.x(), bounds.y(), 9, 9, 0, 0, 16, 16, 16, 16);
            case ELECTRIC_ONLY -> MIGuideMeRenderHelper.fillTexturedRect(context, MachineScreen.SLOT_ATLAS, bounds.x() + 1, bounds.y(), 7, 9, 243, 0, 13, 18);
        }
    }
}
