package aztech.milite.client.machines.multiblocks;

import aztech.milite.machines.multiblocks.MultiblockMemberTooltipData;
import aztech.milite.util.TextHelper;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import org.joml.Matrix4f;

import java.util.List;

public record MultiblockMemberTooltip(MultiblockMemberTooltipData data) implements ClientTooltipComponent {
    private List<ClientTooltipComponent> getLines() {
        var minecraft = Minecraft.getInstance();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        var lines = TextHelper.getMultiblockMemberTooltip(data.registries(), data.member(), data.hatchFlags());
        // 换行
        return ClientHooks.gatherTooltipComponentsFromElements(
                ItemStack.EMPTY,
                lines.stream().map(Either::<FormattedText, TooltipComponent>left).toList(),
                screenWidth / 2,
                screenWidth,
                minecraft.getWindow().getGuiScaledHeight(),
                minecraft.font);
    }

    @Override
    public int getHeight() {
        return 18 + Math.max(0, getLines().size() - 1) * (Minecraft.getInstance().font.lineHeight + 1);
    }

    @Override
    public int getWidth(Font font) {
        var lines = getLines();
        int maxWidth = 18;
        for (int index = 0; index < lines.size(); index++) {
            int width = lines.get(index).getWidth(font) + (index == 0 ? 18 : 0);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }

    private ItemStack getItemToDisplay() {
        var items = data.member().getItemPreviewState(data.registries()).getItems();
        if (items.length == 0) {
            return ItemStack.EMPTY;
        }
        int itemIndex = (int) ((System.currentTimeMillis() / 1500L) % items.length);
        return items[itemIndex];
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        graphics.renderFakeItem(getItemToDisplay(), x, y);
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource buffer) {
        var lines = getLines();
        for (int index = 0; index < lines.size(); index++) {
            var line = lines.get(index);
            int lineX = x + (index == 0 ? 18 : 0);
            int lineY = y + 4 + (index * (font.lineHeight + 1)) + (index > 0 ? 4 : 0);
            line.renderText(font, lineX, lineY, matrix, buffer);
        }
    }
}
