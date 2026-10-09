package aztech.milite.compat.ftbquests;

import dev.ftb.mods.ftbquests.events.ClearFileCacheEvent;
import dev.ftb.mods.ftbquests.item.MissingItem;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.task.ItemTask;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
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

    @Override
    public void init() {
        ClearFileCacheEvent.EVENT.register(event -> itemTasks = null);
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
        if (item instanceof MissingItem) {
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
        var bucketItem = fluid.getBucket();
        if (bucketItem == null || bucketItem == Items.AIR) {
            return;
        }

        addCompleted(uuid, bucketItem, 1);
    }
}
