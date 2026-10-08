package aztech.milite.machines.components;

import aztech.milite.machines.MachineComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class TemperatureComponent implements MachineComponent {
    private double temperature;
    public final double temperatureMax;

    public TemperatureComponent(double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public void setTemperature(double temp) {
        this.temperature = temp;
        this.temperature = Math.min(Math.max(temperature, 0), temperatureMax);
    }

    public void increaseTemperature(double temp) {
        setTemperature(getTemperature() + temp);
    }

    public void decreaseTemperature(double temp) {
        setTemperature(getTemperature() - temp);
    }

    public double getTemperature() {
        return temperature;
    }

    @Override
    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putDouble("temperature", temperature);
    }

    @Override
    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine) {
        setTemperature(tag.getDouble("temperature"));
    }
}
