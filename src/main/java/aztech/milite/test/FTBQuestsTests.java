package aztech.milite.test;

import aztech.milite.MI;
import aztech.milite.advancement.BuiltMultiblockContext;
import aztech.milite.compat.ftbquests.FTBQuestsFacadeImpl;
import aztech.milite.compat.ftbquests.task.BuiltMultiblockQuestTask;
import aztech.milite.config.MIServerConfig;
import aztech.milite.test.framework.MIGameTestHelper;
import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObjectBase;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class FTBQuestsTests {
    private FTBQuestsTests() {}

    public static void validate(MIGameTestHelper helper) {
        var file = ServerQuestFile.INSTANCE;
        var chapter = new Chapter(0x7ffffff0L, file, file.getDefaultChapterGroup());
        var quest = new Quest(0x7ffffff1L, chapter);
        var task = new BuiltMultiblockQuestTask(0x7ffffff2L, quest);
        helper.assertTrue(task.getType() == TaskTypes.TYPES.get(MI.id("built_multiblock")), "registered Lite task type");

        var context = new BuiltMultiblockContext(MI.id("stainless_steel_blast_furnace"), "kanthal_coil");
        var nbt = new CompoundTag();
        nbt.put("multiblock", BuiltMultiblockContext.CODEC.encodeStart(NbtOps.INSTANCE, context).getOrThrow());
        var registries = helper.getLevel().registryAccess();
        task.readData(nbt, registries);
        helper.assertTrue(task.matches(context), "matching machine and coil");
        helper.assertTrue(!task.matches(new BuiltMultiblockContext(context.id(), "cupronickel_coil")), "different coil rejected");
        helper.assertTrue(!task.matches(new BuiltMultiblockContext(MI.id("electric_quarry"), "kanthal_coil")), "different machine rejected");
        var player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "MILiteTest"));
        helper.assertTrue(!task.canSubmit(new TeamData(UUID.randomUUID(), file), player), "manual completion disabled");

        var saved = new CompoundTag();
        task.writeData(saved, registries);
        var restored = new BuiltMultiblockQuestTask(0x7ffffff3L, quest);
        restored.readData(saved, registries);
        helper.assertTrue(restored.matches(context), "task survives NBT round trip");

        var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), registries);
        try {
            task.writeNetData(buffer);
            restored.readNetData(buffer);
            helper.assertTrue(restored.matches(context), "task survives network round trip");
        } finally {
            buffer.release();
        }

        var anyShape = new BuiltMultiblockContext(context.id(), Optional.empty());
        nbt.put("multiblock", BuiltMultiblockContext.CODEC.encodeStart(NbtOps.INSTANCE, anyShape).getOrThrow());
        task.readData(nbt, registries);
        helper.assertTrue(task.matches(context), "any-shape task accepts named coil");
        helper.assertTrue(task.matches(anyShape), "any-shape task accepts unlabelled shape");

        var testFile = new TestQuestFile(helper.getLevel().getServer());
        chapter = new Chapter(0x7ffffff0L, testFile, testFile.getDefaultChapterGroup());
        quest = new Quest(0x7ffffff1L, chapter);
        testFile.task = new BuiltMultiblockQuestTask(0x7ffffff2L, quest);
        quest.addTask(testFile.task);
        testFile.task.readData(saved, registries);
        var facade = new FTBQuestsFacadeImpl();
        boolean originalSetting = MIServerConfig.INSTANCE.ftbQuestsIntegration.getAsBoolean();
        try {
            ServerQuestFile.INSTANCE = testFile;
            MIServerConfig.INSTANCE.ftbQuestsIntegration.set(false);
            facade.builtMultiblock(player, context);
            helper.assertValueEqual(testFile.data.getProgress(testFile.task), 0L, "disabled integration does not advance tasks");

            MIServerConfig.INSTANCE.ftbQuestsIntegration.set(true);
            facade.builtMultiblock(player, new BuiltMultiblockContext(context.id(), "cupronickel_coil"));
            helper.assertValueEqual(testFile.data.getProgress(testFile.task), 0L, "wrong shape does not advance tasks");
            testFile.data.setLocked(true);
            facade.builtMultiblock(player, context);
            helper.assertValueEqual(testFile.data.getProgress(testFile.task), 0L, "locked team does not advance tasks");
            testFile.data.setLocked(false);
            facade.builtMultiblock(player, context);
            helper.assertValueEqual(testFile.data.getProgress(testFile.task), 1L, "built multiblock completes the matching task");
        } finally {
            MIServerConfig.INSTANCE.ftbQuestsIntegration.set(originalSetting);
            ServerQuestFile.INSTANCE = file;
            player.discard();
        }
    }

    private static class TestQuestFile extends ServerQuestFile {
        final TeamData data = new TeamData(UUID.randomUUID(), this);
        BuiltMultiblockQuestTask task;

        TestQuestFile(MinecraftServer server) {
            super(server);
        }

        @Override
        public Optional<TeamData> getTeamData(Player player) {
            return Optional.of(data);
        }

        @Override
        public <T extends QuestObjectBase> List<T> collect(Class<T> type) {
            return type.isInstance(task) ? List.of(type.cast(task)) : List.of();
        }
    }
}
