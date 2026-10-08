package aztech.milite.machines.helper;

import aztech.milite.machines.components.EnergyComponent;
import aztech.milite.machines.components.OrientationComponent;
import aztech.milite.machines.MachineBlockEntity;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Pushes energy out of a machine into the neighbouring block, using NeoForge's standard energy capability so
 * that any RF cable or energy pipe from another mod can receive it.
 */
public class EnergyHelper {
    public static void autoOutput(MachineBlockEntity machine, OrientationComponent orientation, IEnergyStorage energySource) {
        autoOutput(machine, orientation.outputDirection, energySource);
    }

    public static void autoOutput(MachineBlockEntity machine, Direction side, IEnergyStorage energySource) {
        IEnergyStorage target = machine.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK,
                machine.getBlockPos().relative(side), side.getOpposite());
        if (target != null) {
            if (EnergyComponent.moveEnergy(energySource, target) > 0) {
                machine.setChanged();
            }
        }
    }
}
