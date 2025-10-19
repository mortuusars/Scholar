package io.github.mortuusars.scholar.client.chiseled_bookshelf;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.integration.Mods;
import io.github.mortuusars.scholar.integration.woodworks.WoodworksIntegration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
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
    public static void renderSlotTooltip(GuiGraphics guiGraphics, float partialTick) {
        if (!Config.Common.CHISELED_BOOKSHELF_TOOLTIP.get()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null || minecraft.player == null || minecraft.screen != null
              || !(minecraft.hitResult instanceof BlockHitResult blockHitResult)) {
            return;
        }

        BlockPos hitPos = blockHitResult.getBlockPos();
        BlockState blockState = minecraft.level.getBlockState(hitPos);

        if (!(blockState.getBlock() instanceof ChiseledBookShelfBlock)) {
            return;
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

        int x = minecraft.getWindow().getGuiScaledWidth() / 2 + 16;
        int y = minecraft.getWindow().getGuiScaledHeight() / 2 - 9;

        TooltipRenderUtil.renderTooltipBackground(guiGraphics, x, y, 18, 18, 400);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 400);
        guiGraphics.renderItem(bookStack, x + 1, y + 1);
        guiGraphics.pose().popPose();

        guiGraphics.renderTooltip(minecraft.font, bookStack, x + 16, y + 12);
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
