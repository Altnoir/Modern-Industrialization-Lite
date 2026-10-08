package aztech.milite.nuclear;

import aztech.milite.definition.ItemDefinition;
import aztech.milite.items.SortOrder;
import aztech.milite.MIComponents;
import aztech.milite.MIItem;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

public class NuclearAbsorbable extends NuclearComponentItem {
    public final int desintegrationMax;

    public NuclearAbsorbable(Properties settings, int maxTemperature, double heatConduction, NeutronBehaviour neutronBehaviour,
            int desintegrationMax) {
        super(settings.component(MIComponents.REMAINING_DISINTEGRATIONS, desintegrationMax), maxTemperature, heatConduction, neutronBehaviour);
        this.desintegrationMax = desintegrationMax;
    }

    public void setRemainingDesintegrations(ItemStack stack, int value) {
        if (value < 0 || value > desintegrationMax) {
            throw new IllegalArgumentException(
                    String.format("Remaining desintegration %d must be between 0 and max desintegration = %d", value, desintegrationMax));
        }

        stack.set(MIComponents.REMAINING_DISINTEGRATIONS, value);
    }

    public static ItemDefinition<NuclearComponentItem> of(String englishName, String id, int maxTemperature, double heatConduction,
            NeutronBehaviour neutronBehaviour, int desintegrationMax, SortOrder sortOrder) {
        return MIItem.item(englishName, id,
                (settings) -> new NuclearAbsorbable(settings.stacksTo(1), maxTemperature, heatConduction, neutronBehaviour, desintegrationMax),
                sortOrder);
    }

    public double getDurabilityBarProgress(ItemStack stack) {
        return (double) getRemainingDesintegrations(stack) / desintegrationMax;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float f = (float) getRemainingDesintegrations(stack) / desintegrationMax;
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getRemainingDesintegrations(stack) != desintegrationMax;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return (int) Math.round(getDurabilityBarProgress(stack) * 13);
    }

    public int getRemainingDesintegrations(ItemStack stack) {
        return stack.getOrDefault(MIComponents.REMAINING_DISINTEGRATIONS, 0);
    }

    protected static int randIntFromDouble(double value, RandomSource rand) {
        return (int) Math.floor(value) + (rand.nextDouble() < (value % 1) ? 1 : 0);
    }

    public int simulateAbsorption(double neutronsReceived, ItemStack stack, RandomSource rand) {
        int absorbNeutrons = Math.min(randIntFromDouble(neutronsReceived, rand), getRemainingDesintegrations(stack));

        setRemainingDesintegrations(stack, getRemainingDesintegrations(stack) - absorbNeutrons);
        return absorbNeutrons;
    }
}
