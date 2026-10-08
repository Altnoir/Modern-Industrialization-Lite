package aztech.milite.client.compat.guideme.recipe.header;

import guideme.document.block.LytBlock;
import guideme.document.LytRect;
import guideme.layout.LayoutContext;
import guideme.render.RenderContext;
import guideme.siteexport.ExportableResourceProvider;
import guideme.siteexport.ResourceExporter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class LytMachineRecipeHeaderConditionItemImage extends LytBlock implements ExportableResourceProvider {
    private final List<ItemStack> displayedItems;

    public LytMachineRecipeHeaderConditionItemImage(List<ItemStack> displayedItems) {
        this.displayedItems = displayedItems;
    }

    @Override
    protected LytRect computeLayout(LayoutContext context, int x, int y, int availableWidth) {
        return new LytRect(x, y, 8, 8);
    }

    @Override
    protected void onLayoutMoved(int deltaX, int deltaY) {}

    @Override
    public void renderBatch(RenderContext context, MultiBufferSource buffers) {}

    @Override
    public void render(RenderContext context) {
        if (!displayedItems.isEmpty()) {
            context.renderItem(getDisplayedItem(), bounds.x(), bounds.y(), 8, 8);
        }
    }

    @Override
    public void exportResources(ResourceExporter exporter) {
        for (ItemStack stack : displayedItems) {
            exporter.referenceItem(stack);
        }
    }

    private ItemStack getDisplayedItem() {
        int itemIndex = (int) ((System.currentTimeMillis() / 1500L) % displayedItems.size());
        return displayedItems.get(itemIndex).copyWithCount(1);
    }
}
