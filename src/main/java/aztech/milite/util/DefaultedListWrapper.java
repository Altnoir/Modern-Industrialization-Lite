package aztech.milite.util;

import net.minecraft.core.NonNullList;

import java.util.List;

public class DefaultedListWrapper<T> extends NonNullList<T> {
    public DefaultedListWrapper(List<T> actualList) {
        super(actualList, null);
    }
}
