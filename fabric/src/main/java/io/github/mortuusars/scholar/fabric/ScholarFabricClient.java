package io.github.mortuusars.scholar.fabric;

import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.ScholarClient;
import io.github.mortuusars.scholar.book.BookItemColorsReloadListener;
import io.github.mortuusars.scholar.client.chiseled_bookshelf.BookshelfDefaultColorsReloadListener;
import io.github.mortuusars.scholar.client.chiseled_bookshelf.ChiseledBookshelfColors;
import io.github.mortuusars.scholar.client.gui.InWorldTooltip;
import io.github.mortuusars.scholar.client.gui.screen.edit.LecternSpreadBookEditScreen;
import io.github.mortuusars.scholar.client.gui.screen.view.LecternSpreadBookViewScreen;
import io.github.mortuusars.scholar.client.resource.BuiltInResourcePacks;
import io.github.mortuusars.scholar.network.fabric.FabricS2CPacketHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import org.jspecify.annotations.NonNull;

public class ScholarFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ChiseledBookshelfColors.setBookshelfRenderLayer(BlockRenderLayerMap::putBlock);
        ChiseledBookshelfColors.registerBookshelfBlockColors(ColorProviderRegistry.BLOCK::register);

        ConfigScreenFactoryRegistry.INSTANCE.register(Scholar.ID, ConfigurationScreen::new);

        ResourceLoader.get(PackType.CLIENT_RESOURCES)
              .registerReloader(BookItemColorsReloadListener.ID, new BookItemColorsReloadListener());
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
              .registerReloader(BookshelfDefaultColorsReloadListener.ID, new BookshelfDefaultColorsReloadListenerFabric());

        FabricLoader.getInstance().getModContainer(Scholar.ID).ifPresent(container -> {
            for (BuiltInResourcePacks.Pack pack : BuiltInResourcePacks.get()) {
                ResourceLoader.registerBuiltinPack(pack.id(), container, pack.name(), convertActivationType(pack.activation().fabric()));
            }
        });

        ScholarClient.KeyMappings.register(KeyBindingHelper::registerKeyBinding);

        MenuScreens.register(Scholar.MenuTypes.LECTERN_SPREAD_BOOK_VIEW.get(), LecternSpreadBookViewScreen::new);
        MenuScreens.register(Scholar.MenuTypes.LECTERN_SPREAD_BOOK_EDIT.get(), LecternSpreadBookEditScreen::new);

        HudRenderCallback.EVENT.register(InWorldTooltip::render);

        FabricS2CPacketHandler.register();
    }

    private static @NonNull PackActivationType convertActivationType(BuiltInResourcePacks.ActivationType type) {
        return switch (type) {
            case DEFAULT_DISABLED -> PackActivationType.NORMAL;
            case DEFAULT_ENABLED -> PackActivationType.DEFAULT_ENABLED;
            case ALWAYS_ENABLED -> PackActivationType.ALWAYS_ENABLED;
        };
    }
}