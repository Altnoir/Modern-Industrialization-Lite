package aztech.milite.items.armor;

import aztech.milite.api.energy.EnergyItem;
import aztech.milite.items.ActivatableItem;
import aztech.milite.MI;
import aztech.milite.MIComponents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;

public class GraviChestPlateItem extends ArmorItem implements ActivatableItem, EnergyItem {
    public GraviChestPlateItem(Properties settings) {
        super(MIArmorMaterials.GRAVICHESTPLATE, Type.CHESTPLATE,
                settings.stacksTo(1).rarity(Rarity.EPIC).component(MIComponents.ACTIVATED.get(), false));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        if (this.getStoredEnergy(stack) > 0 && this.isActivated(stack)) {
            return ItemAttributeModifiers.builder()
                    .add(
                            NeoForgeMod.CREATIVE_FLIGHT,
                            new AttributeModifier(MI.id("gravichestplate_flight"), 1, AttributeModifier.Operation.ADD_VALUE),
                            EquipmentSlotGroup.CHEST)
                    .build();
        }
        return ItemAttributeModifiers.EMPTY;
    }

    public long getEnergy(ItemStack stack) {
        return getStoredEnergy(stack);
    }

    public void setEnergy(ItemStack stack, long energy) {
        setStoredEnergy(stack, energy);
    }

    public static final long FLIGHT_COST = 1024;
    public static final long ENERGY_CAPACITY = 1 << 24;

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide())
            return;
        if (entity instanceof Player player && stack == player.getItemBySlot(EquipmentSlot.CHEST)) {
            if (player.getAbilities().flying) {
                setEnergy(stack, Math.max(0, getEnergy(stack) - FLIGHT_COST));
            }
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return (int) Math.round(getEnergy(stack) / (double) ENERGY_CAPACITY * 13);
    }

    @Override
    public long getStoredEnergy(ItemStack stack) {
        return stack.getOrDefault(MIComponents.ENERGY.get(), 0L);
    }

    @Override
    public void setStoredEnergy(ItemStack stack, long amount) {
        stack.set(MIComponents.ENERGY.get(), Math.max(0, Math.min(ENERGY_CAPACITY, amount)));
    }

    @Override
    public long getEnergyCapacity(ItemStack stack) {
        return ENERGY_CAPACITY;
    }

    @Override
    public long getEnergyMaxInput(ItemStack stack) {
        return ENERGY_CAPACITY;
    }

    @Override
    public long getEnergyMaxOutput(ItemStack stack) {
        return 0;
    }
}
