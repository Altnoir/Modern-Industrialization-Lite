package aztech.milite.client.items.armor;

import aztech.milite.config.MIClientConfig;
import aztech.milite.items.armor.GraviChestPlateItem;
import aztech.milite.items.armor.JetpackItem;
import aztech.milite.items.FluidFuelItemHelper;
import aztech.milite.MIText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class HudRenderer {
    public static void onRenderHud(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, MIClientConfig.INSTANCE.armorHudYPosition.getAsInt(), 0);
            ItemStack chest = mc.player.getItemBySlot(EquipmentSlot.CHEST);
            if (chest.getItem() instanceof JetpackItem jetpack) {
                boolean active = jetpack.isActivated(chest);
                MutableComponent jetpackActiveComponent;

                if (active) {
                    jetpackActiveComponent = MIText.JetpackEnabled.text().setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
                } else {
                    jetpackActiveComponent = MIText.JetpackDisabled.text().setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
                }
                guiGraphics.drawString(mc.font, jetpackActiveComponent, 4, 0, 16383998);

                Component fillText = MIText.JetpackFill.text(FluidFuelItemHelper.getAmount(chest) * 100 / JetpackItem.CAPACITY);
                guiGraphics.drawString(mc.font, fillText, 4, 10, 16383998);
            } else if (chest.getItem() instanceof GraviChestPlateItem gsp) {
                boolean active = gsp.isActivated(chest);

                MutableComponent gravichestplateActiveComponent;

                if (active) {
                    gravichestplateActiveComponent = MIText.GravichestplateEnabled.text().setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
                } else {
                    gravichestplateActiveComponent = MIText.GravichestplateDisabled.text().setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
                }

                guiGraphics.drawString(mc.font, gravichestplateActiveComponent, 4, 0, 16383998);
                Component fillText = MIText.EnergyFill.text(
                        gsp.getEnergy(chest) * 100 / GraviChestPlateItem.ENERGY_CAPACITY);
                guiGraphics.drawString(mc.font, fillText, 4, 10, 16383998);
            }
            guiGraphics.pose().popPose();
        }
    }
}
