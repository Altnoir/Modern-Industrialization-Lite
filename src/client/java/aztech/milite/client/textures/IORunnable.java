package aztech.milite.client.textures;

import java.io.IOException;

@FunctionalInterface
interface IORunnable {
    void run() throws IOException;

    default void safeRun() {
        try {
            run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
