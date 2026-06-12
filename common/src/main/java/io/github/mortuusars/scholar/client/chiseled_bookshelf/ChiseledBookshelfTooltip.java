package io.github.mortuusars.scholar.client.chiseled_bookshelf;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.client.gui.InWorldTooltip;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public class ChiseledBookshelfTooltip {
    public static boolean extract(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        if (!Config.Common.CHISELED_BOOKSHELF_TOOLTIP.get()) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null || minecraft.player == null || minecraft.screen != null
              || !(minecraft.hitResult instanceof BlockHitResult blockHitResult)) {
            return false;
        }

        if (Config.Common.TOOLTIP_REQUIRES_SNEAK.get() && !minecraft.player.isSecondaryUseActive()) {
            return false;
        }

        BlockPos hitPos = blockHitResult.getBlockPos();
        BlockState blockState = minecraft.level.getBlockState(hitPos);

        if (!(blockState.getBlock() instanceof ChiseledBookShelfBlock chiseledBookShelfBlock)) {
            return false;
        }

        @Nullable BlockEntity blockEntity = minecraft.level.getBlockEntity(hitPos);

        if (!(blockEntity instanceof ChiseledBookShelfBlockEntity chiseledBookShelfBlockEntity)) {
            return false;
        }

        OptionalInt hitSlot = chiseledBookShelfBlock.getHitSlot(blockHitResult, blockState.getValue(ChiseledBookShelfBlock.FACING));

        if (hitSlot.isEmpty()) return false;

        ItemStack bookStack = chiseledBookShelfBlockEntity.getItem(hitSlot.getAsInt());
        if (bookStack.isEmpty()) {
            return false;
        }

        InWorldTooltip.renderItemTooltip(guiGraphics, deltaTracker, bookStack);
        return true;
    }
}
