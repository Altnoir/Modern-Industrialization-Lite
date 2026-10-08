package aztech.milite.attributes;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TooltipFlag;

public abstract class DisplayNamedAttribute extends Attribute {
    protected DisplayNamedAttribute(String descriptionId, double defaultValue) {
        super(descriptionId, defaultValue);
    }

    /**
     * Translation key to use in tooltips, thanks to our override of {@link #toComponent(AttributeModifier, TooltipFlag)}.
     * {@link #getDescriptionId()} is still used in /attribute commands and other contexts.
     */
    public abstract String getTooltipDescriptionId();

    /*
     * The below methods is copied from IAttributeExtension and uses DisplayAttribute#getDisplayDescriptionId so that Attribute#getDescriptionId
     * can return the MIText.AttributeQuantumArmor description id. This is desirable because when using the /attribute command it will display the
     * attribute name as "Quantum Armor" instead of just "Armor" like it does when using "attribute.name.generic.armor" as the description id (or
     * "Infinite Damage" instead of just "Damage" for the InfiniteDamageAttribute).
     */

    @Override
    public MutableComponent toComponent(AttributeModifier modif, TooltipFlag flag) {
        double value = modif.amount();
        String key = value > 0 ? "neoforge.modifier.plus" : "neoforge.modifier.take";
        ChatFormatting color = this.getStyle(value > 0);

        Component attrDesc = Component.translatable(this.getTooltipDescriptionId());
        Component valueComp = this.toValueComponent(modif.operation(), value, flag);
        MutableComponent comp = Component.translatable(key, valueComp, attrDesc).withStyle(color);

        return comp.append(this.getDebugInfo(modif, flag));
    }
}
