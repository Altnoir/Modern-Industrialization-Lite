package aztech.milite.client.compat.viewer.impl.rei;

import aztech.milite.client.compat.viewer.impl.MachineScreenPredicateTest;
import aztech.milite.client.machines.gui.MachineMenuClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.compat.rei.machines.ReiMachineRecipes;
import aztech.milite.util.Rectangle;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ClickArea;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;

class MachineClickAreaHandler implements ClickArea<MachineScreen> {
    @Override
    public Result handle(ClickAreaContext<MachineScreen> context) {
        MachineMenuClient screenHandler = context.getScreen().getMenu();
        ResourceLocation blockId = screenHandler.guiParams.blockId;
        List<ReiMachineRecipes.ClickAreaCategory> categories = ReiMachineRecipes.machineToClickAreaCategory.getOrDefault(blockId,
                Collections.emptyList());
        Rectangle rectangle = ReiMachineRecipes.machineToClickArea.get(blockId);
        Point point = context.getMousePosition().clone();
        point.translate(-context.getScreen().x(), -context.getScreen().y());
        if (categories.size() > 0 && rectangle != null && contains(rectangle, point)) {
            ClickArea.Result result = ClickArea.Result.success();
            boolean foundSome = false;
            for (ReiMachineRecipes.ClickAreaCategory cac : categories) {
                if (!MachineScreenPredicateTest.test(cac.predicate, context.getScreen()))
                    continue;
                List<Display> displays = DisplayRegistry.getInstance().get(CategoryIdentifier.of(cac.category));
                if (displays.size() > 0) {
                    result.category(CategoryIdentifier.of(cac.category));
                    foundSome = true;
                }
            }
            return foundSome ? result : ClickArea.Result.fail();
        } else {
            return ClickArea.Result.fail();
        }
    }

    private static boolean contains(Rectangle rectangle, Point mousePosition) {
        return rectangle.x() <= mousePosition.x && mousePosition.x <= rectangle.x() + rectangle.w() && rectangle.y() <= mousePosition.y
                && mousePosition.y <= rectangle.y() + rectangle.h();
    }
}
