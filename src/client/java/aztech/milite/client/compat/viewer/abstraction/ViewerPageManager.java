package aztech.milite.client.compat.viewer.abstraction;

import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ViewerPageManager {
    public final List<SizedIngredient> stacks;
    public final int pageSize;

    public int currentPage;

    public ViewerPageManager(List<SizedIngredient> stacks, int pageSize) {
        this.stacks = stacks;
        this.pageSize = pageSize;
    }

    public int totalPages() {
        return (stacks.size() - 1) / pageSize + 1;
    }

    public void scroll(int delta) {
        currentPage += delta;
        int totalPages = totalPages();
        if (currentPage < 0) {
            currentPage = totalPages - 1;
        }
        if (currentPage >= totalPages) {
            currentPage = 0;
        }
    }

    @Nullable
    public SizedIngredient get(int index) {
        index += pageSize * currentPage;
        if (index < stacks.size()) {
            return stacks.get(index);
        }
        return null;
    }
}
