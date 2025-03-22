package io.github.mortuusars.scholar.fabric.integration;

import de.pnku.mcbv.init.McbvBlockInit;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.render.ChiseledBookShelf;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.RenderType;

public class MoreChiseledBookshelfVariantsCompat {
    public static void initClient() {
        Scholar.LOGGER.info("Adding 'colored books in chiseled bookshelves' compat to 'More Chiseled Bookshelf Variants (lolmcbv)'. If MCBV bookshelves displaying wrongly - try without Scholar installed, and if incompatibility is confirmed - report to Scholar github.");

        ColorProviderRegistry.BLOCK.register(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.SPRUCE_CHISELED_BOOKSHELF);
        ColorProviderRegistry.BLOCK.register(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.BIRCH_CHISELED_BOOKSHELF);
        ColorProviderRegistry.BLOCK.register(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.JUNGLE_CHISELED_BOOKSHELF);
        ColorProviderRegistry.BLOCK.register(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.ACACIA_CHISELED_BOOKSHELF);
        ColorProviderRegistry.BLOCK.register(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.DARK_OAK_CHISELED_BOOKSHELF);
        ColorProviderRegistry.BLOCK.register(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.MANGROVE_CHISELED_BOOKSHELF);
        ColorProviderRegistry.BLOCK.register(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.CHERRY_CHISELED_BOOKSHELF);
        ColorProviderRegistry.BLOCK.register(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.BAMBOO_CHISELED_BOOKSHELF);
        ColorProviderRegistry.BLOCK.register(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.CRIMSON_CHISELED_BOOKSHELF);
        ColorProviderRegistry.BLOCK.register(ChiseledBookShelf::getSlotTintColor, McbvBlockInit.WARPED_CHISELED_BOOKSHELF);

        BlockRenderLayerMap.INSTANCE.putBlock(McbvBlockInit.SPRUCE_CHISELED_BOOKSHELF, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(McbvBlockInit.BIRCH_CHISELED_BOOKSHELF, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(McbvBlockInit.JUNGLE_CHISELED_BOOKSHELF, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(McbvBlockInit.ACACIA_CHISELED_BOOKSHELF, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(McbvBlockInit.DARK_OAK_CHISELED_BOOKSHELF, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(McbvBlockInit.MANGROVE_CHISELED_BOOKSHELF, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(McbvBlockInit.CHERRY_CHISELED_BOOKSHELF, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(McbvBlockInit.BAMBOO_CHISELED_BOOKSHELF, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(McbvBlockInit.CRIMSON_CHISELED_BOOKSHELF, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(McbvBlockInit.WARPED_CHISELED_BOOKSHELF, RenderType.cutout());
    }
}
