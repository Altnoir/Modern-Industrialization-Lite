package aztech.milite.compat.ftbquests;

import aztech.milite.MI;
import aztech.milite.advancement.BuiltMultiblockContext;
import aztech.milite.compat.ftbquests.task.BuiltMultiblockQuestTask;
import aztech.milite.config.MIServerConfig;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.events.ClearFileCacheEvent;
import dev.ftb.mods.ftbquests.item.MissingItem;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.task.ItemTask;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class FTBQuestsFacadeImpl implements FTBQuestsFacade {
    @Nullable
    private Map<Item, List<ItemTask>> itemTasks;
    @Nullable
    private List<BuiltMultiblockQuestTask> builtMultiblockTasks;

    @Override
    public void init() {
        BuiltMultiblockQuestTask.TYPE = TaskTypes.register(MI.id("built_multiblock"), BuiltMultiblockQuestTask::new,
                () -> Icon.getIcon("moderntech:item/wrench"));
        ClearFileCacheEvent.EVENT.register(event -> {
            itemTasks = null;
            builtMultiblockTasks = null;
        });
    }

    private List<BuiltMultiblockQuestTask> getBuiltMultiblockTasks() {
        if (builtMultiblockTasks == null) {
            builtMultiblockTasks = ServerQuestFile.INSTANCE.collect(BuiltMultiblockQuestTask.class);
        }
        return builtMultiblockTasks;
    }

    private List<ItemTask> getItemTasks(Item item) {
        if (itemTasks == null) {
            itemTasks = ServerQuestFile.INSTANCE.collect(ItemTask.class).stream()
                    .filter(task -> !(task.getItemStack().getItem() instanceof MissingItem) && !task.consumesResources())
                    .collect(Collectors.groupingBy(task -> task.getItemStack().getItem()));
        }

        var tasks = itemTasks.get(item);
        return tasks == null ? List.of() : tasks;
    }

    @Override
    public void addCompleted(UUID uuid, Item item, long amount) {
        if (!MIServerConfig.INSTANCE.ftbQuestsIntegration.getAsBoolean() || item instanceof MissingItem) {
            return;
        }

        var file = ServerQuestFile.INSTANCE;
        var team = FTBTeamsAPI.api().getManager().getTeamForPlayerID(uuid).orElse(null);
        if (team == null) {
            return;
        }
        var data = file.getNullableTeamData(team.getId());

        if (data == null || data.isLocked()) {
            return;
        }

        var stack = new ItemStack(item, (int) amount);

        for (var task : getItemTasks(item)) {
            if (data.canStartTasks(task.getQuest()) && !data.isCompleted(task) && task.test(stack)) {
                data.addProgress(task, amount);
            }
        }
    }

    @Override
    public void addCompleted(UUID uuid, Fluid fluid, long amount) {
        if (!MIServerConfig.INSTANCE.ftbQuestsIntegration.getAsBoolean()) {
            return;
        }
        var bucketItem = fluid.getBucket();
        if (bucketItem == null || bucketItem == Items.AIR) {
            return;
        }

        addCompleted(uuid, bucketItem, 1);
    }

    @Override
    public void builtMultiblock(ServerPlayer player, BuiltMultiblockContext context) {
        if (!MIServerConfig.INSTANCE.ftbQuestsIntegration.getAsBoolean()) {
            return;
        }
        var file = ServerQuestFile.INSTANCE;
        file.getTeamData(player).ifPresent(data -> {
            if (!data.isLocked()) {
                file.withPlayerContext(player, () -> {
                    for (var task : getBuiltMultiblockTasks()) {
                        if (data.canStartTasks(task.getQuest()) && task.canComplete(data) && task.matches(context)) {
                            data.setProgress(task, 1);
                        }
                    }
                });
            }
        });
    }
}
