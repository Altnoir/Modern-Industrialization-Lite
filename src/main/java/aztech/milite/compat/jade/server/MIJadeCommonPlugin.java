package aztech.milite.compat.jade.server;

import aztech.milite.blocks.storage.tank.TankBlockEntity;
import aztech.milite.machines.MachineBlockEntity;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MIJadeCommonPlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEnergyStorage(new MachineComponentProvider.Energy(), MachineBlockEntity.class);
        registration.registerFluidStorage(new MachineComponentProvider.Fluids(), MachineBlockEntity.class);
        registration.registerItemStorage(new MachineComponentProvider.Items(), MachineBlockEntity.class);
        registration.registerProgress(new MachineComponentProvider.Progress(), MachineBlockEntity.class);

        registration.registerFluidStorage(new TankComponentProvider(), TankBlockEntity.class);
    }

    public static JadeFluidObject fluidStack(FluidVariant variant, long amount) {
        return JadeFluidObject.of(variant.getFluid(), amount, variant.getComponentsPatch());
    }
}
