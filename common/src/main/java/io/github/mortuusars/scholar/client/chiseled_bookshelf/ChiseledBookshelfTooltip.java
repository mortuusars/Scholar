package io.github.mortuusars.scholar.client.chiseled_bookshelf;

import io.github.mortuusars.scholar.Config;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.OptionalInt;

public class ChiseledBookshelfTooltip {
    public static void renderSlotTooltip(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
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

        if (!(blockState.getBlock() instanceof ChiseledBookShelfBlock chiseledBookShelfBlock)) {
            return;
        }

        @Nullable BlockEntity blockEntity = minecraft.level.getBlockEntity(hitPos);

        if (!(blockEntity instanceof ChiseledBookShelfBlockEntity chiseledBookShelfBlockEntity)) {
            return;
        }

        OptionalInt hitSlot = chiseledBookShelfBlock.getHitSlot(blockHitResult, blockState);

        if (hitSlot.isEmpty()) return;

        ItemStack bookStack = chiseledBookShelfBlockEntity.getItem(hitSlot.getAsInt());
        if (bookStack.isEmpty()) {
            return;
        }

        int x = minecraft.getWindow().getGuiScaledWidth() / 2 + 16;
        int y = minecraft.getWindow().getGuiScaledHeight() / 2 - 9;

        TooltipRenderUtil.renderTooltipBackground(guiGraphics, x, y, 18, 18, null);
        guiGraphics.renderItem(bookStack, x + 1, y + 1);


//        List<Component> lines = Screen.getTooltipFromItem(minecraft, bookStack);
//        ResourceLocation style = bookStack.get(DataComponents.TOOLTIP_STYLE);

//        guiGraphics.renderTooltip(minecraft.font, List.of(ClientTooltipComponent.create(Component.literal("asd").getVisualOrderText())),
//              x + 16, y + 12, DefaultTooltipPositioner.INSTANCE, null);

//        guiGraphics.renderTooltip(minecraft.font, List.of(ClientTooltipComponent.create(Component.literal("asd").getVisualOrderText())),
//              x + 16, y + 12, DefaultTooltipPositioner.INSTANCE, null);

        guiGraphics.setTooltipForNextFrame(minecraft.font, bookStack, x + 16, y + 12);
        guiGraphics.renderDeferredTooltip();
    }
}
