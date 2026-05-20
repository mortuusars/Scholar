package io.github.mortuusars.scholar.client.chiseled_bookshelf;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.client.InWorldTooltip;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.OptionalInt;

public class ChiseledBookshelfTooltip {
    public static boolean render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!Config.Common.CHISELED_BOOKSHELF_TOOLTIP.get()) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null || minecraft.player == null || minecraft.screen != null
              || !(minecraft.hitResult instanceof BlockHitResult blockHitResult)) {
            return false;
        }

        BlockPos hitPos = blockHitResult.getBlockPos();
        BlockState blockState = minecraft.level.getBlockState(hitPos);

        if (!(blockState.getBlock() instanceof ChiseledBookShelfBlock chiseledBookShelfBlock)) {
            return false;
        }

        @Nullable BlockEntity blockEntity = minecraft.level.getBlockEntity(hitPos);

        if (!(blockEntity instanceof ChiseledBookShelfBlockEntity chiseledBookShelfBlockEntity)) {
            return;
        }

        Optional<Vec2> blockHitPos = ChiseledBookShelfBlock.getRelativeHitCoordinatesForBlockFace(blockHitResult,
              blockState.getValue(HorizontalDirectionalBlock.FACING));
        if (blockHitPos.isEmpty()) {
            return;
        }

        int hitSlot = getHitSlot(blockState, blockHitPos.get());

        ItemStack bookStack = chiseledBookShelfBlockEntity.getItem(hitSlot);
        if (bookStack.isEmpty()) {
            return;
        }

        InWorldTooltip.renderItemTooltip(guiGraphics, deltaTracker, bookStack);
        return true;
    }

    private static int getHitSlot(BlockState state, Vec2 hitPos) {
        if (Mods.WOODWORKS.isLoaded()) {
            OptionalInt slot = WoodworksIntegration.getHitSlot(state, hitPos);
            if (slot.isPresent()) {
                return slot.getAsInt();
            }
        }

        return ChiseledBookShelfBlock.getHitSlot(hitPos);
    }
}
