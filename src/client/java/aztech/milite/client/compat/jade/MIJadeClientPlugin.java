package aztech.milite.client.compat.jade;

import aztech.milite.compat.jade.server.MachineComponentProvider;
import aztech.milite.compat.jade.server.TankComponentProvider;
import aztech.milite.machines.MachineBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MIJadeClientPlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEnergyStorageClient(new MachineComponentProvider.Energy());
        registration.registerFluidStorageClient(new MachineComponentProvider.Fluids());
        registration.registerItemStorageClient(new MachineComponentProvider.Items());
        registration.registerProgressClient(new MachineComponentProvider.Progress());

        registration.registerFluidStorageClient(new TankComponentProvider());
    }

    static float ratio(double current, double max) {
        return (float) (current / max);
    }
}
