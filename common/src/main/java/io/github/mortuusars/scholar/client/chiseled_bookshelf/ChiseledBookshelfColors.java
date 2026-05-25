package io.github.mortuusars.scholar.client.chiseled_bookshelf;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.book.BookColor;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;

public class ChiseledBookshelfColors {
    public static Map<Identifier, BookshelfDefaultColors> DEFAULT_SLOT_COLORS = Collections.emptyMap();

    public static int getSlotTintColor(BlockState state, @Nullable BlockAndTintGetter blockGetter,
                                       @Nullable BlockPos pos, int tintIndex) {
        if (blockGetter == null || pos == null || tintIndex < 0 || tintIndex > 5
                || !(blockGetter.getBlockEntity(pos) instanceof ChiseledBookShelfBlockEntity blockEntity)) {
            return getDefaultTintColorForSlot(state, tintIndex);
        }

        ItemStack stackInSlot = blockEntity.getItem(tintIndex);
        if (stackInSlot.isEmpty()) return -1;

        return BookColor.of(stackInSlot, getDefaultTintColorForSlot(state, tintIndex));
    }

    public static int getDefaultTintColorForSlot(BlockState state, int slot) {
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return DEFAULT_SLOT_COLORS.getOrDefault(blockId, BookshelfDefaultColors.VANILLA).getDefaultTintForSlot(slot);
    }

    // --

    // Scholar is changing every ChiseledBookshelfBlock to have cutout RenderType and BlockColors.
    // This greatly simplifies adding colored book support - any resourcepack will now just work out of the box.
    // (in the previous version each block needed to be changed manually in code, using its reference)

    public static void setBookshelfRenderLayer(BiConsumer<Block, ChunkSectionLayer> consumer) {
        Scholar.LOGGER.info("Scholar is setting RenderType of all 'ChiseledBookShelfBlock's " +
              "to 'cutout' so the books could have proper colors in bookshelves.");
        Scholar.LOGGER.info("If Chiseled Bookshelves do not look correctly, please report to the Scholar github.");
        BuiltInRegistries.BLOCK.stream()
              .filter(bl -> bl instanceof ChiseledBookShelfBlock)
              .forEach(bl -> consumer.accept(bl, ChunkSectionLayer.CUTOUT));
    }

    public static void registerBookshelfBlockColors(BiConsumer<BlockColor, Block> consumer) {
        Scholar.LOGGER.info("Scholar is registering 'BlockColor' (tints) for all 'ChiseledBookShelfBlock's, " +
              "so the books could have proper colors in bookshelves.");
        Scholar.LOGGER.info("If Chiseled Bookshelves do not look correctly, please report to the Scholar github.");
        BuiltInRegistries.BLOCK.stream()
              .filter(bl -> bl instanceof ChiseledBookShelfBlock)
              .forEach(bl -> consumer.accept(ChiseledBookshelfColors::getSlotTintColor, bl));
    }
}
