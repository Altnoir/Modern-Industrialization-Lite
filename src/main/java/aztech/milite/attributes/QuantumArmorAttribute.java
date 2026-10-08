package aztech.milite.attributes;

import aztech.milite.MIText;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TooltipFlag;

public class QuantumArmorAttribute extends DisplayNamedAttribute {
    public QuantumArmorAttribute() {
        super(MIText.AttributeQuantumArmor.getTranslationKey(), 0);
    }

    @Override
    public MutableComponent toValueComponent(AttributeModifier.Operation operation, double value, TooltipFlag flag) {
        return Component.literal("\u00B9\u2044\u2084 |\u221E> + \u00B3\u2044\u2084 |0>");
    }

    @Override
    public String getTooltipDescriptionId() {
        return "attribute.name.generic.armor";
    }
}
