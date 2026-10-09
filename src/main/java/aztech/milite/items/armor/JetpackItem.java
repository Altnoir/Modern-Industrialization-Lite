package aztech.milite.items.armor;

import aztech.milite.api.datamaps.FluidFuel;
import aztech.milite.fluid.MIFluid;
import aztech.milite.items.ActivatableItem;
import aztech.milite.items.FluidFuelItemHelper;
import aztech.milite.MIComponents;
import aztech.milite.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.List;

public class JetpackItem extends ArmorItem implements ActivatableItem {
    public static final int CAPACITY = 8 * FluidType.BUCKET_VOLUME;

    public JetpackItem(Properties settings) {
        super(MIArmorMaterials.DIESEL_JETPACK, Type.CHESTPLATE,
                settings.stacksTo(1).rarity(Rarity.UNCOMMON).component(MIComponents.ACTIVATED.get(), false));
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return isActivated(stack) && FluidFuelItemHelper.getAmount(stack) > 0;
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        // Fluid consumption is handled in the armor tick.
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player && stack == player.getItemBySlot(EquipmentSlot.CHEST)) {
            tickArmor(stack, player);
        }
    }

    private void tickArmor(ItemStack stack, Player player) {
        if (isActivated(stack) && !player.onGround()) {
            FluidVariant fluid = FluidFuelItemHelper.getFluid(stack);
            long amount = FluidFuelItemHelper.getAmount(stack);
            if (amount > 0) {
                // Always consume one mb of fuel
                FluidFuelItemHelper.decrement(stack);
                if (MIKeyMap.isHoldingUp(player)) {
                    // Consume one more mb when pressing space
                    FluidFuelItemHelper.decrement(stack);
                    if (player.isFallFlying()) {
                        // Boost forward if fall flying
                        Vec3 playerFacing = player.getLookAngle();
                        Vec3 playerVelocity = player.getDeltaMovement();
                        double maxSpeed = Math.sqrt(FluidFuel.getFe(fluid.getFluid()) / 200.0);
                        double attenuationFactor = 0.5;
                        player.setDeltaMovement(playerVelocity.scale(attenuationFactor).add(playerFacing.scale(maxSpeed)));
                    } else {
                        // Otherwise boost vertically
                        double maxSpeed = Math.sqrt(FluidFuel.getFe(fluid.getFluid()) / 200.0);
                        double acceleration = 0.25;
                        Vec3 v = player.getDeltaMovement();
                        if (v.y < maxSpeed) {
                            player.setDeltaMovement(v.x, Math.min(maxSpeed, v.y + acceleration), v.z);
                        }
                        // Reset fall distance (but not in elytra mode)
                        if (!player.level().isClientSide()) {
                            player.fallDistance = 0;
                        }
                    }
                    if (player instanceof ServerPlayer serverPlayer) {
                        ObfuscationReflectionHelper.setPrivateValue(ServerGamePacketListenerImpl.class, serverPlayer.connection, 0,
                                "aboveGroundTickCount");
                    }
                }
            }
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return (int) Math.round(getDurabilityBarProgress(stack) * 13);
    }

    public double getDurabilityBarProgress(ItemStack stack) {
        return (double) FluidFuelItemHelper.getAmount(stack) / CAPACITY;
    }

    public boolean hasDurabilityBar(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        Fluid fluid = FluidFuelItemHelper.getFluid(stack).getFluid();

        if (fluid instanceof MIFluid cf) {
            return cf.color;
        } else {
            return 0;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        FluidFuelItemHelper.appendTooltip(stack, tooltip, CAPACITY);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return ItemAttributeModifiers.EMPTY;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack otherStack, Slot slot, ClickAction clickType, Player player,
            SlotAccess cursorStackReference) {
        // TODO NEO jetpack filling in inventory
//        if (clickType == ClickAction.SECONDARY) {
//            Storage<FluidVariant> jetpackStorage = getStackStorage(stack, player);
//            Storage<FluidVariant> cursorStorage = ContainerItemContext.ofPlayerCursor(player, player.containerMenu).find(FluidStorage.ITEM);
//
//            return StorageUtil.move(cursorStorage, jetpackStorage, fk -> true, Long.MAX_VALUE, null) > 0;
//        }
        return false;
    }

    // TODO NEO
//    @Nullable
//    private static Storage<FluidVariant> getStackStorage(ItemStack stack, Player player) {
//        Inventory inventory = player.getInventory();
//        ContainerItemContext context = null;
//
//        for (int i = 0; i < inventory.getContainerSize(); ++i) {
//            if (inventory.getItem(i) == stack) {
//                InventoryStorage wrapper = PlayerInventoryStorage.of(inventory);
//                context = ContainerItemContext.ofPlayerSlot(player, wrapper.getSlots().get(i));
//                break;
//            }
//        }
//
//        if (context != null) {
//            return context.find(FluidStorage.ITEM);
//        } else {
//            return null;
//        }
//    }
}
