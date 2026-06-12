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
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import org.jspecify.annotations.NonNull;

public class ScholarFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScholarClient.init();

        Block[] bookshelves = BuiltInRegistries.BLOCK.stream()
              .filter(block -> block instanceof ChiseledBookShelfBlock)
              .toArray(Block[]::new);
        BlockColorRegistry.register(ChiseledBookshelfColors.slotTintSources(), bookshelves);

        ConfigScreenFactoryRegistry.INSTANCE.register(Scholar.ID, ConfigurationScreen::new);

        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
              BookItemColorsReloadListener.ID, new BookItemColorsReloadListener());
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
              .registerReloadListener(BookshelfDefaultColorsReloadListener.ID, new BookshelfDefaultColorsReloadListenerFabric());

        FabricLoader.getInstance().getModContainer(Scholar.ID).ifPresent(container -> {
            for (BuiltInResourcePacks.Pack pack : BuiltInResourcePacks.get()) {
                ResourceLoader.registerBuiltinPack(pack.id(), container, pack.name(), convertActivationType(pack.activation().fabric()));
            }
        });

        ScholarClient.KeyMappings.register(KeyMappingHelper::registerKeyMapping);

        MenuScreens.register(Scholar.MenuTypes.LECTERN_SPREAD_BOOK_VIEW.get(), LecternSpreadBookViewScreen::new);
        MenuScreens.register(Scholar.MenuTypes.LECTERN_SPREAD_BOOK_EDIT.get(), LecternSpreadBookEditScreen::new);

        HudElementRegistry.addLast(Scholar.identifier("tooltip"), InWorldTooltip::extract);

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