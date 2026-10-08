package aztech.milite.attributes;

import aztech.milite.items.tools.QuantumSword;
import aztech.milite.MIText;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TooltipFlag;

public class InfiniteDamageAttribute extends DisplayNamedAttribute {
    private static final String INFINITY = "\u221e";

    public InfiniteDamageAttribute() {
        super(MIText.AttributeInfiniteDamage.getTranslationKey(), 0);
    }

    // Allows a modifier with the base id to render green with the effective value.
    // Other modifiers still render blue with the additional value.
    @Override
    public ResourceLocation getBaseId() {
        return QuantumSword.BASE_INFINITE_DAMAGE;
    }

    @Override
    public MutableComponent toValueComponent(AttributeModifier.Operation operation, double value, TooltipFlag flag) {
        return Component.literal(INFINITY);
    }

    @Override
    public MutableComponent toBaseComponent(double value, double entityBase, boolean merged, TooltipFlag flag) {
        String stringValue = value > Mth.EPSILON ? INFINITY : FORMAT.format(0);
        MutableComponent comp = Component.translatable("attribute.modifier.equals.0", stringValue,
                Component.translatable(this.getTooltipDescriptionId()));
        if (flag.isAdvanced() && !merged) {
            Component debugInfo = Component.literal(" ")
                    .append(Component.translatable("neoforge.attribute.debug.base", FORMAT.format(entityBase), FORMAT.format(value - entityBase))
                            .withStyle(ChatFormatting.GRAY));
            comp.append(debugInfo);
        }
        return comp;
    }

    @Override
    public String getTooltipDescriptionId() {
        return "attribute.name.generic.attack_damage";
    }
}
