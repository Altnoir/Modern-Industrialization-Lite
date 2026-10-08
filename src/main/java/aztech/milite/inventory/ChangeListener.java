package aztech.milite.inventory;

import java.util.List;
import java.util.Map;

public abstract class ChangeListener {
    protected abstract void onChange();

    protected abstract boolean isValid(Object token); // TODO: do we really need the token? (it's unused for now)

    public void listenAll(List<? extends AbstractConfigurableStack<?, ?>> stacks, Object token) {
        for (var stack : stacks) {
            stack.addListener(this, token);
        }
    }

    public static void notify(Map<ChangeListener, Object> listeners) {
        for (var it = listeners.entrySet().iterator(); it.hasNext();) {
            var entry = it.next();
            if (entry.getKey().isValid(entry.getValue())) {
                entry.getKey().onChange();
            } else {
                it.remove();
            }
        }
    }
}
