package io.github.mortuusars.scholar.client.render;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.book.BookColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ChiseledBookShelf {
    public static final int DEFAULT_TINT_SLOT_0 = 0xFF1AB8BC;
    public static final int DEFAULT_TINT_SLOT_1 = 0xFF78B140;
    public static final int DEFAULT_TINT_SLOT_2 = 0xFFA743E5;
    public static final int DEFAULT_TINT_SLOT_3 = 0xFFD44A6E;
    public static final int DEFAULT_TINT_SLOT_4 = 0xFFD57856;
    public static final int DEFAULT_TINT_SLOT_5 = 0xFF2F6BC6;

    public static final Map<ResourceLocation, Integer> CUSTOM_COLORS = new HashMap<>();

    static {
        CUSTOM_COLORS.put(new ResourceLocation("minecraft:book"), 0xFF9C763E);
        CUSTOM_COLORS.put(new ResourceLocation("exposure:album"), 0xFFA43E2C);
        CUSTOM_COLORS.put(new ResourceLocation("exposure:signed_album"), 0xFFA43E2C);
    }

    public static int getSlotTintColor(BlockState state, @Nullable BlockAndTintGetter blockGetter,
                                       @Nullable BlockPos pos, int tintIndex) {
        if (!Config.Common.CHISELED_BOOKSHELF_COLORS.get()) {
            return getDefaultTintColorForSlot(tintIndex);
        }

        if (blockGetter == null || pos == null || tintIndex < 0 || tintIndex > 5
                || !(blockGetter.getBlockEntity(pos) instanceof ChiseledBookShelfBlockEntity blockEntity)) {
            return getDefaultTintColorForSlot(tintIndex);
        }

        ItemStack stackInSlot = blockEntity.getItem(tintIndex);
        if (stackInSlot.isEmpty()) return -1;

        if (stackInSlot.getItem() instanceof WrittenBookItem || stackInSlot.getItem() instanceof WritableBookItem) {
            return BookColor.of(stackInSlot);
        }

        return CUSTOM_COLORS.getOrDefault(BuiltInRegistries.ITEM.getKey(stackInSlot.getItem()), getDefaultTintColorForSlot(tintIndex));
    }

    public static int getDefaultTintColorForSlot(int slot) {
        return switch (slot) {
            case 1 -> DEFAULT_TINT_SLOT_1;
            case 2 -> DEFAULT_TINT_SLOT_2;
            case 3 -> DEFAULT_TINT_SLOT_3;
            case 4 -> DEFAULT_TINT_SLOT_4;
            case 5 -> DEFAULT_TINT_SLOT_5;
            default -> DEFAULT_TINT_SLOT_0;
        };
    }

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

        int hitSlot = ChiseledBookShelfBlock.getHitSlot(blockHitPos.get());

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
}
