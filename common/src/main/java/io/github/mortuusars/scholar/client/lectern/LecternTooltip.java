package io.github.mortuusars.scholar.client.lectern;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.client.gui.InWorldTooltip;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.phys.BlockHitResult;

public class LecternTooltip {
    public static boolean extract(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        if (!Config.Common.LECTERN_TOOLTIP.get()) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null || minecraft.player == null || minecraft.screen != null
              || !(minecraft.hitResult instanceof BlockHitResult blockHitResult)
              || !(minecraft.level.getBlockEntity(blockHitResult.getBlockPos()) instanceof LecternBlockEntity lecternBlockEntity)) {
            return false;
        }

        if (Config.Common.TOOLTIP_REQUIRES_SNEAK.get() && !minecraft.player.isSecondaryUseActive()) {
            return false;
        }

        ItemStack bookStack = lecternBlockEntity.getBook();
        if (bookStack.isEmpty()) {
            return false;
        }

        InWorldTooltip.renderItemTooltip(guiGraphics, deltaTracker, bookStack);
        return true;
    }
}
