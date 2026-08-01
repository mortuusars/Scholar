package io.github.mortuusars.scholar.client.gui;

import io.github.mortuusars.scholar.client.chiseled_bookshelf.ChiseledBookshelfTooltip;
import io.github.mortuusars.scholar.client.lectern.LecternTooltip;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.world.item.ItemStack;

public class InWorldTooltip {
    public static boolean extract(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().gui.hud.isHidden()) {
            return false;
        }
        return ChiseledBookshelfTooltip.extract(guiGraphics, deltaTracker)
              || LecternTooltip.extract(guiGraphics, deltaTracker);
    }

    public static void renderItemTooltip(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, ItemStack stack) {
        int x = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 16;
        int y = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 - 9;
        TooltipRenderUtil.extractTooltipBackground(guiGraphics, x, y, 18, 18, null);
        guiGraphics.item(stack, x + 1, y + 1);
        guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font, stack, x + 16, y + 12);
        guiGraphics.extractDeferredElements(0, 0, 0); // Renders the tooltip
    }
}
