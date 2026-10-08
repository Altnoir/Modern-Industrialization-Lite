package aztech.milite.client.machines.guicomponents;

import aztech.milite.client.machines.gui.ClientComponentRenderer;
import aztech.milite.client.machines.gui.GuiComponentClient;
import aztech.milite.client.machines.gui.MachineScreen;
import aztech.milite.client.util.RenderHelper;
import aztech.milite.machines.guicomponents.CraftingMultiblockGui;
import aztech.milite.MI;
import aztech.milite.MIText;
import aztech.milite.util.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;

import java.util.List;
import java.util.Optional;

public class CraftingMultiblockGuiClient extends GuiComponentClient<Unit, CraftingMultiblockGui.Data> {
    public CraftingMultiblockGuiClient(Unit params, CraftingMultiblockGui.Data data) {
        super(params, data);
    }

    public boolean isShapeValid() {
        return data.isShapeValid();
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        return new Renderer();
    }

    public static class BaseScreenRenderer {
        private static final ResourceLocation TEXTURE = MI.id("textures/gui/container/multiblock_info.png");

        protected static int drawWordWrap(GuiGraphics graphics, Font font, Component text, int x, int y, int lineHeight, int color, boolean dropShadow) {
            int lineWidth = CraftingMultiblockGui.W - 10;
            int deltaY = 0;
            for (var line : font.split(text, lineWidth)) {
                graphics.drawString(font, line, x, y + deltaY, color, dropShadow);
                deltaY += lineHeight;
            }
            return deltaY;
        }

        /**
         * Returns {@code deltaY}.
         */
        public int renderScreenAndStatus(boolean shapeValid, boolean matchesMultipleRecipes, Font font, GuiGraphics guiGraphics, int x, int y) {
            guiGraphics.blit(TEXTURE, x + CraftingMultiblockGui.X, y + CraftingMultiblockGui.Y, 0, 0,
                    CraftingMultiblockGui.W, CraftingMultiblockGui.H, CraftingMultiblockGui.W, CraftingMultiblockGui.H);

            int deltaY = 21;

            deltaY += drawWordWrap(guiGraphics, font, shapeValid ? MIText.MultiblockShapeValid.text() : MIText.MultiblockShapeInvalid.text(), x + 10, y + deltaY, 11, shapeValid ? 0xFFFFFF : 0xFF0000, false);

            if (shapeValid) {
                deltaY += drawWordWrap(guiGraphics, font, MIText.MultiblockStatusActive.text(), x + 10, y + deltaY, 11, 0xFFFFFF, false);
            }
            if (matchesMultipleRecipes) {
                deltaY += drawWordWrap(guiGraphics, font, MIText.MachineMultipleRecipes1.text(), x + 10, y + deltaY, 11, 0xFF0000, false);
            }

            return deltaY;
        }
    }

    public class Renderer extends BaseScreenRenderer implements ClientComponentRenderer {
        @Override
        public void renderBackground(GuiGraphics guiGraphics, int x, int y) {
            Font font = Minecraft.getInstance().font;

            int deltaY = renderScreenAndStatus(data.isShapeValid(), data.matchesMultipleRecipes(), font, guiGraphics, x, y);

            if (data.isShapeValid()) {
                if (data.activeRecipe().isPresent()) {
                    var recipe = data.activeRecipe().get();

                    deltaY += drawWordWrap(guiGraphics, font, MIText.Progress.text(String.format("%.1f", recipe.progress() * 100) + " %"), x + 10, y + deltaY, 11, 0xFFFFFF, false);

                    deltaY += drawWordWrap(guiGraphics, font, MIText.BaseRfRecipe.text(TextHelper.getRfTextTick(recipe.baseRecipeRf())), x + 10, y + deltaY, 11, 0xFFFFFF, false);

                    deltaY += drawWordWrap(guiGraphics, font, MIText.CurrentRfRecipe.text(TextHelper.getRfTextTick(recipe.currentRecipeRf())), x + 10, y + deltaY, 11, 0xFFFFFF, false);
                }
            }

            if (data.remainingOverclockTicks() > 0) {
                drawWordWrap(guiGraphics, font, GunpowderOverclockGuiClient.Renderer.formatOverclock(data.remainingOverclockTicks()), x + 10, y + deltaY, 11, 0xFFFFFF, false);
            }
        }

        private List<Component> getTooltip() {
            if (data.matchesMultipleRecipes()) {
                return List.of(
                        MIText.MachineMultipleRecipes1.text().withStyle(ChatFormatting.RED),
                        MIText.MachineMultipleRecipes2.text().withStyle(ChatFormatting.RED));
            }
            return List.of();
        }

        @Override
        public boolean renderTooltip(MachineScreen screen, Font font, GuiGraphics guiGraphics, int x, int y, int cursorX, int cursorY) {
            if (RenderHelper.isPointWithinRectangle(CraftingMultiblockGui.X, CraftingMultiblockGui.Y, CraftingMultiblockGui.W, CraftingMultiblockGui.H, cursorX - x, cursorY - y)) {
                List<Component> tooltip = getTooltip();
                if (!tooltip.isEmpty()) {
                    guiGraphics.renderTooltip(font, tooltip, Optional.empty(), cursorX, cursorY);
                    return true;
                }
            }
            return false;
        }
    }
}
