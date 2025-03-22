package io.github.mortuusars.scholar.fabric;

import io.github.mortuusars.scholar.PlatformHelper;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.ScholarClient;
import io.github.mortuusars.scholar.book.BookColor;
import io.github.mortuusars.scholar.client.gui.screen.edit.LecternSpreadBookEditScreen;
import io.github.mortuusars.scholar.client.gui.screen.view.LecternSpreadBookViewScreen;
import io.github.mortuusars.scholar.client.render.ChiseledBookShelf;
import io.github.mortuusars.scholar.fabric.integration.MoreChiseledBookshelfVariantsCompat;
import io.github.mortuusars.scholar.network.fabric.FabricS2CPacketHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class ScholarFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.CHISELED_BOOKSHELF, RenderType.cutout());
        ColorProviderRegistry.BLOCK.register(ChiseledBookShelf::getSlotTintColor, Blocks.CHISELED_BOOKSHELF);
        if (PlatformHelper.isModLoaded("lolmcbv")) {
            MoreChiseledBookshelfVariantsCompat.initClient();
        }

        ColorProviderRegistry.ITEM.register(BookColor::getItemTintColor, Items.WRITABLE_BOOK);
        ColorProviderRegistry.ITEM.register(BookColor::getItemTintColor, Items.WRITTEN_BOOK);

        ScholarClient.KeyMappings.register(KeyBindingHelper::registerKeyBinding);

        MenuScreens.register(Scholar.MenuTypes.LECTERN_SPREAD_BOOK_VIEW.get(), LecternSpreadBookViewScreen::new);
        MenuScreens.register(Scholar.MenuTypes.LECTERN_SPREAD_BOOK_EDIT.get(), LecternSpreadBookEditScreen::new);

        HudRenderCallback.EVENT.register(ChiseledBookShelf::renderSlotTooltip);

        FabricS2CPacketHandler.register();
    }
}
