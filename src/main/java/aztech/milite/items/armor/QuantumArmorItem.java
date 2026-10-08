package aztech.milite.items.armor;

import aztech.milite.MI;
import aztech.milite.MIRegistries;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.NeoForgeMod;

public class QuantumArmorItem extends ArmorItem {
    public QuantumArmorItem(ArmorItem.Type type, Properties settings) {
        super(MIArmorMaterials.QUANTUM, type, settings.stacksTo(1).attributes(buildModifiers(type)));
    }

    private static ItemAttributeModifiers buildModifiers(ArmorItem.Type type) {
        var builder = ItemAttributeModifiers.builder()
                .add(
                        MIRegistries.QUANTUM_ARMOR,
                        // This needs a unique name for each armor piece or else the value of one piece will override the others
                        new AttributeModifier(MI.id("quantum_armor_%s".formatted(type.getName())), 1, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.bySlot(type.getSlot()));
        if (type == Type.CHESTPLATE) {
            builder.add(
                    NeoForgeMod.CREATIVE_FLIGHT,
                    new AttributeModifier(MI.id("quantum_flight"), 1, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.CHEST);
        }
        return builder.build();
    }
}
