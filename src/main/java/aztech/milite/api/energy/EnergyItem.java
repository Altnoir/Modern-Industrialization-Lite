package aztech.milite.api.energy;

import net.minecraft.world.item.ItemStack;

/**
 * Implemented by MI items that can store RF, such as the portable storage unit or the gravi chestplate.
 *
 * <p>
 * MI Lite exposes these through NeoForge's standard {@code Capabilities.EnergyStorage.ITEM} capability so that
 * chargers and energy pipes from other mods can fill them up.
 */
public interface EnergyItem {
    long getEnergyCapacity(ItemStack stack);

    /**
     * @return the maximum amount of RF that can be pushed into this item every time it is charged.
     */
    long getEnergyMaxInput(ItemStack stack);

    /**
     * @return the maximum amount of RF that can be pulled out of this item.
     */
    long getEnergyMaxOutput(ItemStack stack);

    long getStoredEnergy(ItemStack stack);

    void setStoredEnergy(ItemStack stack, long amount);
}
