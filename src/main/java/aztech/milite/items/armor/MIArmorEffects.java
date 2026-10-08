package aztech.milite.items.armor;

import aztech.milite.MIItem;
import aztech.milite.MIRegistries;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.concurrent.ThreadLocalRandom;

public class MIArmorEffects {
    private MIArmorEffects() {}

    public static boolean quantumArmorPreventsDamage(LivingEntity entity) {
        double parts = entity.getAttributeValue(MIRegistries.QUANTUM_ARMOR);
        return ThreadLocalRandom.current().nextDouble() < parts / 4d;
    }

    public static boolean canTankFlyIntoWall(ItemStack helmet) {
        return helmet.getItem() == MIItem.RUBBER_HELMET.asItem() || helmet.getItem() == MIItem.QUANTUM_HELMET.asItem();
    }

    public static boolean canTankFall(ItemStack boots) {
        return boots.getItem() == MIItem.RUBBER_BOOTS.asItem() || boots.getItem() == MIItem.QUANTUM_BOOTS.asItem();
    }

    public static void init() {
        NeoForge.EVENT_BUS.addListener(LivingIncomingDamageEvent.class, event -> {
            if (quantumArmorPreventsDamage(event.getEntity())) {
                event.setCanceled(true);
            }
        });

        NeoForge.EVENT_BUS.addListener(LivingIncomingDamageEvent.class, event -> {
            var entity = event.getEntity();
            var source = event.getContainer().getSource();
            float amount = event.getContainer().getOriginalDamage();

            // Find a suitable stack that can "tank" the damage
            ItemStack tankingStack = null;
            EquipmentSlot es = null;
            if (source.is(DamageTypes.FLY_INTO_WALL)) {
                es = EquipmentSlot.HEAD;
                ItemStack head = entity.getItemBySlot(es);
                if (MIArmorEffects.canTankFlyIntoWall(head)) {
                    tankingStack = head;
                }
            } else if (source.is(DamageTypes.FALL)) {
                es = EquipmentSlot.FEET;
                ItemStack head = entity.getItemBySlot(es);
                if (MIArmorEffects.canTankFall(head)) {
                    tankingStack = head;
                }
            }
            // Have the stack tank the damage
            if (tankingStack != null) {
                int intAmount = (int) Math.ceil(amount);
                tankingStack.hurtAndBreak(intAmount, entity, es);
                event.setCanceled(true);
            }
        });
    }
}
