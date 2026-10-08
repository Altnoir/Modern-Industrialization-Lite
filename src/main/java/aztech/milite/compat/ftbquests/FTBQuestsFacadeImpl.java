package aztech.milite.compat.ftbquests;

import dev.ftb.mods.ftbquests.item.MissingItem;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.task.ItemTask;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class FTBQuestsFacadeImpl implements FTBQuestsFacade {
    @Override
    public void addCompleted(UUID uuid, Item item, long amount) {
        var file = ServerQuestFile.INSTANCE;
        var team = FTBTeamsAPI.api().getManager().getTeamForPlayerID(uuid).orElse(null);
        if (team == null) {
            return;
        }
        var data = file.getNullableTeamData(team.getId());

        if (data == null || data.isLocked()) {
            return;
        }

        ItemStack stack = new ItemStack(item, (int) amount);

        for (var task : file.getSubmitTasks()) {
            if (task instanceof ItemTask itemTask && data.canStartTasks(task.getQuest())) {
                if (data.isCompleted(task) || itemTask.getItemStack().getItem() instanceof MissingItem || item instanceof MissingItem) {
                    continue;
                }

                if (!task.consumesResources() && itemTask.test(stack)) {
                    data.addProgress(task, amount);
                }
            }
        }
    }
}
