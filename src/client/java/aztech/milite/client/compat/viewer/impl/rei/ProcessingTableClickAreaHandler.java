package aztech.milite.client.compat.viewer.impl.rei;

import aztech.milite.client.blocks.processingtable.ProcessingTableScreen;
import aztech.milite.client.compat.viewer.usage.ProcessingTableCategory;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.registry.screen.ClickArea;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

class ProcessingTableClickAreaHandler implements ClickArea<ProcessingTableScreen> {
    @Override
    public Result handle(ClickAreaContext<ProcessingTableScreen> context) {
        Point point = context.getMousePosition();

        if (!context.getScreen().isOverArrow(point.x, point.y)) {
            return ClickArea.Result.fail();
        }

        return ClickArea.Result.success().category(CategoryIdentifier.of(ProcessingTableCategory.ID));
    }
}
