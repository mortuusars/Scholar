package io.github.mortuusars.scholar.integration.mcbv;

import de.pnku.mcbv.init.McbvBlockInit;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.render.ChiseledBookShelf;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;

public class MoreChiseledBookshelfVariantsIntegration {
    public static void registerBlockColors(BiConsumer<BlockColor, Block> consumer) {
        Scholar.LOGGER.info("Registering chiseled bookshelf block colors to 'More Chiseled Bookshelf Variants (lolmcbv)'. " +
                "If MCBV bookshelves displaying wrongly - try without Scholar installed, and if incompatibility " +
                "is confirmed - report to Scholar github.");
        consumer.accept(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.SPRUCE_CHISELED_BOOKSHELF);
        consumer.accept(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.BIRCH_CHISELED_BOOKSHELF);
        consumer.accept(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.JUNGLE_CHISELED_BOOKSHELF);
        consumer.accept(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.ACACIA_CHISELED_BOOKSHELF);
        consumer.accept(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.DARK_OAK_CHISELED_BOOKSHELF);
        consumer.accept(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.MANGROVE_CHISELED_BOOKSHELF);
        consumer.accept(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.CHERRY_CHISELED_BOOKSHELF);
        consumer.accept(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.BAMBOO_CHISELED_BOOKSHELF);
        consumer.accept(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.CRIMSON_CHISELED_BOOKSHELF);
        consumer.accept(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.WARPED_CHISELED_BOOKSHELF);
    }

    public static void setRenderLayer(BiConsumer<Block, RenderType> consumer) {
        Scholar.LOGGER.info("Setting 'cutout' render type for 'More Chiseled Bookshelf Variants (lolmcbv)'. " +
                "If MCBV bookshelves displaying wrongly - try without Scholar installed, and if incompatibility " +
                "is confirmed - report to Scholar github.");
        consumer.accept(McbvBlockInit.SPRUCE_CHISELED_BOOKSHELF, RenderType.cutout());
        consumer.accept(McbvBlockInit.BIRCH_CHISELED_BOOKSHELF, RenderType.cutout());
        consumer.accept(McbvBlockInit.JUNGLE_CHISELED_BOOKSHELF, RenderType.cutout());
        consumer.accept(McbvBlockInit.ACACIA_CHISELED_BOOKSHELF, RenderType.cutout());
        consumer.accept(McbvBlockInit.DARK_OAK_CHISELED_BOOKSHELF, RenderType.cutout());
        consumer.accept(McbvBlockInit.MANGROVE_CHISELED_BOOKSHELF, RenderType.cutout());
        consumer.accept(McbvBlockInit.CHERRY_CHISELED_BOOKSHELF, RenderType.cutout());
        consumer.accept(McbvBlockInit.BAMBOO_CHISELED_BOOKSHELF, RenderType.cutout());
        consumer.accept(McbvBlockInit.CRIMSON_CHISELED_BOOKSHELF, RenderType.cutout());
        consumer.accept(McbvBlockInit.WARPED_CHISELED_BOOKSHELF, RenderType.cutout());
    }
}