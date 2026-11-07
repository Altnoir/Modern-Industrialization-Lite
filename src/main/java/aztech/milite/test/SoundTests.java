package aztech.milite.test;

import aztech.milite.MI;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.sounds.MISounds;
import aztech.milite.test.framework.MIGameTest;
import aztech.milite.test.framework.MIGameTestHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;

import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;

public class SoundTests {
    private static final List<String> MACHINES_WITH_SOUNDS = List.of(
            "electric_macerator",
            "electric_assembler",
            "bronze_furnace",
            "electric_diesel_generator",
            "steel_boiler",
            "coke_oven",
            "steam_quarry",
            "electric_blast_furnace",
            "nuclear_reactor",
            "replicator");

    private static final List<String> MACHINES_WITHOUT_SOUNDS = List.of(
            "large_tank",
            "configurable_chest");

    @MIGameTest
    public void machineSoundsHaveTheirFiles(MIGameTestHelper helper) {
        var stream = SoundTests.class.getResourceAsStream("/assets/modern_tech/sounds.json");
        if (stream == null) {
            helper.fail("assets/modern_tech/sounds.json is missing");
            return;
        }

        var sounds = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
        var registered = new HashSet<String>();
        for (var event : BuiltInRegistries.SOUND_EVENT) {
            var id = BuiltInRegistries.SOUND_EVENT.getKey(event);
            if (id.getNamespace().equals(MI.ID)) {
                registered.add(id.getPath());
            }
        }

        for (var entry : sounds.entrySet()) {
            if (!registered.contains(entry.getKey())) {
                helper.fail("sounds.json declares " + entry.getKey() + " but no sound event is registered for it");
                return;
            }
            for (JsonElement element : entry.getValue().getAsJsonObject().getAsJsonArray("sounds")) {
                var name = element.getAsString();
                var file = "/assets/modern_tech/sounds/" + name.substring(MI.ID.length() + 1) + ".ogg";
                if (SoundTests.class.getResourceAsStream(file) == null) {
                    helper.fail("sounds.json uses " + name + " but " + file + " is missing");
                    return;
                }
            }
        }

        if (registered.size() != sounds.size()) {
            helper.fail("expected " + sounds.size() + " machine sounds, found " + registered.size() + " registered events");
            return;
        }

        helper.succeed();
    }

    @MIGameTest
    public void machinesPlayTheirSoundOnlyWhileRunning(MIGameTestHelper helper) {
        var pos = new BlockPos(0, 1, 0);
        for (var id : MACHINES_WITH_SOUNDS) {
            helper.setBlock(pos, BuiltInRegistries.BLOCK.get(MI.id(id)));
            var machine = (MachineBlockEntity) helper.getBlockEntity(pos);
            long gameTime = helper.getLevel().getGameTime();
            machine.lastSoundTick = gameTime - 1000;
            MISounds.play(machine);
            helper.assertValueEqual(machine.lastSoundTick, gameTime, id + " played its sound");
            MISounds.play(machine);
            helper.assertValueEqual(machine.lastSoundTick, gameTime, id + " waited for its sound to end");
        }

        for (var id : MACHINES_WITHOUT_SOUNDS) {
            helper.setBlock(pos, BuiltInRegistries.BLOCK.get(MI.id(id)));
            var machine = (MachineBlockEntity) helper.getBlockEntity(pos);
            long gameTime = helper.getLevel().getGameTime();
            machine.lastSoundTick = gameTime - 1000;
            MISounds.play(machine);
            helper.assertValueEqual(machine.lastSoundTick, gameTime - 1000, id + " stayed silent");
        }

        helper.succeed();
    }
}
