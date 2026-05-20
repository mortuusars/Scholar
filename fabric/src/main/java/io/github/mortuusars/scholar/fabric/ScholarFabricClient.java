package io.github.mortuusars.scholar.fabric;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.ScholarClient;
import io.github.mortuusars.scholar.book.BookColor;
import io.github.mortuusars.scholar.client.InWorldTooltip;
import io.github.mortuusars.scholar.client.gui.screen.edit.LecternSpreadBookEditScreen;
import io.github.mortuusars.scholar.client.gui.screen.view.LecternSpreadBookViewScreen;
import io.github.mortuusars.scholar.client.chiseled_bookshelf.ChiseledBookshelfColors;
import io.github.mortuusars.scholar.client.resource.BuiltInResourcePacks;
import io.github.mortuusars.scholar.network.fabric.PacketsImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Items;

public class ScholarFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ChiseledBookshelfColors.setBookshelfRenderLayer(BlockRenderLayerMap.INSTANCE::putBlock);
        ChiseledBookshelfColors.registerBookshelfBlockColors(ColorProviderRegistry.BLOCK::register);

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new BookshelfDefaultColorsReloadListenerFabric());
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new BookshelfItemColorsReloadListenerFabric());

        FabricLoader.getInstance().getModContainer(Scholar.ID).ifPresent(container -> {
            for (BuiltInResourcePacks.Pack pack : BuiltInResourcePacks.get()) {
                ResourcePackActivationType activationType = switch (pack.activation().fabric()) {
                    case DEFAULT_DISABLED -> ResourcePackActivationType.NORMAL;
                    case DEFAULT_ENABLED -> ResourcePackActivationType.DEFAULT_ENABLED;
                    case ALWAYS_ENABLED -> ResourcePackActivationType.ALWAYS_ENABLED;
                };
                ResourceManagerHelper.registerBuiltinResourcePack(pack.id(), container, pack.name(), activationType);
            }
        });

        ColorProviderRegistry.ITEM.register(BookColor::getItemTintColor, Items.WRITABLE_BOOK);
        ColorProviderRegistry.ITEM.register(BookColor::getItemTintColor, Items.WRITTEN_BOOK);

        ScholarClient.KeyMappings.register(KeyBindingHelper::registerKeyBinding);

        MenuScreens.register(Scholar.MenuTypes.LECTERN_SPREAD_BOOK_VIEW.get(), LecternSpreadBookViewScreen::new);
        MenuScreens.register(Scholar.MenuTypes.LECTERN_SPREAD_BOOK_EDIT.get(), LecternSpreadBookEditScreen::new);

        HudRenderCallback.EVENT.register(InWorldTooltip::render);

        PacketsImpl.registerS2CPackets();
    }
}
