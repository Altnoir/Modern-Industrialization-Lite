package aztech.milite.client.compat.jade;

import aztech.milite.MI;
import aztech.milite.MIText;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;

public class OverclockComponentProvider implements IBlockComponentProvider {
    @Override
    public ResourceLocation getUid() {
        return MI.id("overclock");
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag tag = accessor.getServerData();
        if (tag.contains("efficiencyTicks") && tag.contains("maxEfficiencyTicks") && tag.contains("baseRecipeEu")
                && tag.contains("currentRecipeEu")) {

            int efficiencyTicks = tag.getInt("efficiencyTicks");
            int maxEfficiencyTicks = tag.getInt("maxEfficiencyTicks");
            long baseRecipeEu = tag.getLong("baseRecipeEu");
            long currentEu = tag.getLong("currentRecipeEu");

            double mult = (double) currentEu / baseRecipeEu;
            var helper = IElementHelper.get();

            tooltip.add(helper.progress(
                    MIJadeClientPlugin.ratio(efficiencyTicks, maxEfficiencyTicks),
                    MIJadeClientPlugin.textAndRatio(
                            MIText.Efficiency.text(),
                            String.valueOf(efficiencyTicks),
                            String.valueOf(maxEfficiencyTicks)),
                    helper.progressStyle().color(0xFF61C928, 0xFF438C1C).textColor(-1),
                    BoxStyle.getNestedBox(),
                    true));

            tooltip.add(helper.text(MIText.EuTOverclocked.text(
                    String.format("%.1f", mult), String.format("%d", currentEu))));
        }
    }
}
