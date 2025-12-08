package io.github.mortuusars.scholar.neoforge.event;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.ScholarClient;
import io.github.mortuusars.scholar.client.chiseled_bookshelf.BookshelfDefaultColorsReloadListener;
import io.github.mortuusars.scholar.client.chiseled_bookshelf.BookshelfItemColorsReloadListener;
import io.github.mortuusars.scholar.client.chiseled_bookshelf.ChiseledBookshelfTooltip;
import io.github.mortuusars.scholar.client.gui.screen.edit.LecternSpreadBookEditScreen;
import io.github.mortuusars.scholar.client.gui.screen.view.LecternSpreadBookViewScreen;
import io.github.mortuusars.scholar.client.chiseled_bookshelf.ChiseledBookshelfColors;
import io.github.mortuusars.scholar.client.resource.BuiltInResourcePacks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforgespi.locating.IModFile;

import java.nio.file.Path;
import java.util.Optional;

@SuppressWarnings("unused")
public class ClientEvents {
    @EventBusSubscriber(modid = Scholar.ID, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ScholarClient.init();
                ChiseledBookshelfColors.setBookshelfRenderLayer(ItemBlockRenderTypes::setRenderLayer);
            });
        }

        @SubscribeEvent
        public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
            ChiseledBookshelfColors.registerBookshelfBlockColors(event::register);
        }

        @SubscribeEvent
        public static void registerClientReloadListeners(AddClientReloadListenersEvent event) {
            event.addListener(BookshelfDefaultColorsReloadListener.ID, new BookshelfDefaultColorsReloadListener());
            event.addListener(BookshelfItemColorsReloadListener.ID, new BookshelfItemColorsReloadListener());
        }

        @SubscribeEvent
        public static void registerMenuScreens(RegisterMenuScreensEvent event) {
            event.register(Scholar.MenuTypes.LECTERN_SPREAD_BOOK_VIEW.get(), LecternSpreadBookViewScreen::new);
            event.register(Scholar.MenuTypes.LECTERN_SPREAD_BOOK_EDIT.get(), LecternSpreadBookEditScreen::new);
        }

        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            ScholarClient.KeyMappings.register(event::register);
        }

        @SubscribeEvent
        public static void addPacks(AddPackFindersEvent event) {
            if (event.getPackType() != PackType.CLIENT_RESOURCES) return;

            ModList.get().getModContainerById(Scholar.ID).ifPresent(modContainer -> {
                for (BuiltInResourcePacks.Pack pack : BuiltInResourcePacks.get()) {
                    IModFile modFile = modContainer.getModInfo().getOwningFile().getFile();
                    Path resourcePath = modFile.findResource("resourcepacks/" + pack.id().getPath());

                    event.addRepositorySource((consumer) -> consumer.accept(
                            Pack.readMetaAndCreate(
                                    new PackLocationInfo(pack.id().toString(), pack.name(), PackSource.BUILT_IN,
                                            Optional.of(new KnownPack("scholar", pack.id().getPath(),
                                                    modContainer.getModInfo().getVersion().toString()))),
                                    BuiltInPackSource.fromName((path) -> new PathPackResources(path, resourcePath)),
                                    PackType.CLIENT_RESOURCES,
                                    new PackSelectionConfig(
                                            pack.activation().neoforge() == BuiltInResourcePacks.ActivationType.ALWAYS_ENABLED,
                                            Pack.Position.TOP,
                                            false))));
                }
            });
        }

        @SubscribeEvent
        public static void onRenderGuiPost(RenderGuiEvent.Post event) {
            ChiseledBookshelfTooltip.renderSlotTooltip(event.getGuiGraphics(), event.getPartialTick());
        }
    }
}
