package io.github.mortuusars.scholar.client;

import io.github.mortuusars.scholar.client.chiseled_bookshelf.ChiseledBookshelfTooltip;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.world.item.ItemStack;

public class InWorldTooltip {
    public static boolean render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().options.hideGui) {
            return false;
        }
        return ChiseledBookshelfTooltip.render(guiGraphics, deltaTracker)
              || LecternTooltip.render(guiGraphics, deltaTracker);
    }

    public static void renderItemTooltip(GuiGraphics guiGraphics, DeltaTracker deltaTracker, ItemStack stack) {
        int x = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 16;
        int y = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 - 9;

        TooltipRenderUtil.renderTooltipBackground(guiGraphics, x, y, 18, 18, 400);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 400);
        guiGraphics.renderItem(stack, x + 1, y + 1);
        guiGraphics.pose().popPose();

        guiGraphics.renderTooltip(Minecraft.getInstance().font, stack, x + 16, y + 12);
    }
}
