package aztech.milite.util;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.concurrent.atomic.AtomicLong;

public class TickHelper {
    private static final AtomicLong currentTick = new AtomicLong();

    public static long getCurrentTick() {
        return currentTick.get();
    }

    static {
        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Pre.class, event -> {
            currentTick.incrementAndGet();
        });
    }
}
