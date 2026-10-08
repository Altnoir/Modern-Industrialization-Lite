package aztech.milite.client.items.armor;

import aztech.milite.client.MIKeybinds;
import aztech.milite.items.armor.MIKeyMap;
import aztech.milite.network.armor.UpdateKeysPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

public class ClientKeyHandler {
    private static boolean up = false;

    public static void onEndTick(Minecraft client) {
        updateKeyMap(client);

        for (MIKeybinds.Keybind keybind : MIKeybinds.getMappings()) {
            while (keybind.holder().get().consumeClick()) {
                keybind.action().run();
            }
        }
    }

    public static void updateKeyMap(Minecraft client) {
        Options settings = client.options;

        if (client.getConnection() == null)
            return;

        boolean upNow = settings.keyJump.isDown();
        if (upNow != up) {
            up = upNow;

            MIKeyMap.update(client.player, up);
            new UpdateKeysPacket(up).sendToServer();
        }
    }
}
