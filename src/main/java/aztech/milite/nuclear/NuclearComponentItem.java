package aztech.milite.nuclear;

import aztech.milite.definition.ItemDefinition;
import aztech.milite.items.SortOrder;
import aztech.milite.MIItem;
import aztech.milite.thirdparty.fabrictransfer.api.item.ItemVariant;
import net.minecraft.world.item.Item;

public class NuclearComponentItem extends Item implements NuclearComponent<ItemVariant> {
    public final int maxTemperature;
    public final double heatConduction;
    public final NeutronBehaviour neutronBehaviour;

    public NuclearComponentItem(Properties settings, int maxTemperature, double heatConduction, NeutronBehaviour neutronBehaviour) {
        super(settings);
        this.maxTemperature = maxTemperature;
        this.heatConduction = heatConduction;
        this.neutronBehaviour = neutronBehaviour;
    }

    public static ItemDefinition<NuclearComponentItem> of(String englishName, String id, int maxTemperature, double heatConduction,
            NeutronBehaviour neutronBehaviour) {
        return MIItem
                .item(englishName, id, (settings) -> new NuclearComponentItem(settings.stacksTo(1), maxTemperature, heatConduction, neutronBehaviour),
                        SortOrder.NUCLEAR.create(NuclearOrder.HEAT_EXCHANGER).and(heatConduction));
    }

    public int getMaxTemperature() {
        return maxTemperature;
    }

    @Override
    public double getHeatConduction() {
        return heatConduction;
    }

    @Override
    public NeutronBehaviour getNeutronBehaviour() {
        return neutronBehaviour;
    }

    @Override
    public ItemVariant getVariant() {
        return ItemVariant.of(this);
    }
}
