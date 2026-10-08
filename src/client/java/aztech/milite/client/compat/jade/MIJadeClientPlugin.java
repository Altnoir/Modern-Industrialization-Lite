package aztech.milite.client.compat.jade;

import aztech.milite.compat.jade.server.MachineComponentProvider;
import aztech.milite.compat.jade.server.TankComponentProvider;
import aztech.milite.machines.MachineBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.ui.IDisplayHelper;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MIJadeClientPlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new OverclockComponentProvider(), MachineBlock.class);

        registration.registerEnergyStorageClient(new MachineComponentProvider.Energy());
        registration.registerFluidStorageClient(new MachineComponentProvider.Fluids());
        registration.registerItemStorageClient(new MachineComponentProvider.Items());
        registration.registerProgressClient(new MachineComponentProvider.Progress());

        registration.registerFluidStorageClient(new TankComponentProvider());
    }

    static float ratio(double current, double max) {
        return (float) (current / max);
    }

    static String getUnicodeMillibuckets(long amount, boolean simplify) {
        return IDisplayHelper.get().humanReadableNumber((double) amount, "B", true);
    }

    static Component textAndRatio(Component text, String current, String max) {
        return Component.literal("")
                .append(text.copy().withStyle(ChatFormatting.WHITE))
                .append(" ")
                .append(Component.literal(current).withStyle(ChatFormatting.WHITE))
                .append(" / ")
                .append(Component.literal(max))
                .withStyle(ChatFormatting.GRAY);
    }
}
