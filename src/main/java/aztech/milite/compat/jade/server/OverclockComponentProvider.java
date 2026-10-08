package aztech.milite.compat.jade.server;

import aztech.milite.api.machine.holder.CrafterComponentHolder;
import aztech.milite.MI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class OverclockComponentProvider implements IServerDataProvider<BlockAccessor> {
    @Override
    public ResourceLocation getUid() {
        return MI.id("overclock");
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (accessor.getTarget() instanceof CrafterComponentHolder crafterComponentHolder) {
            var crafterComponent = crafterComponentHolder.getCrafterComponent();
            if (crafterComponent.hasActiveRecipe() && crafterComponent.getMaxEfficiencyTicks() > 0) {
                data.putInt("efficiencyTicks", crafterComponent.getEfficiencyTicks());
                data.putInt("maxEfficiencyTicks", crafterComponent.getMaxEfficiencyTicks());
                data.putLong("baseRecipeEu", crafterComponent.getBaseRecipeEu());
                data.putLong("currentRecipeEu", crafterComponent.getCurrentRecipeEu());
            }
        }
    }
}
