package io.github.mortuusars.scholar.neoforge.event;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.ScholarClient;
import io.github.mortuusars.scholar.client.gui.InWorldTooltip;
import io.github.mortuusars.scholar.client.chiseled_bookshelf.BookshelfDefaultColorsReloadListener;
import io.github.mortuusars.scholar.book.BookItemColorsReloadListener;
import io.github.mortuusars.scholar.client.gui.screen.edit.LecternSpreadBookEditScreen;
import io.github.mortuusars.scholar.client.gui.screen.view.LecternSpreadBookViewScreen;
import io.github.mortuusars.scholar.client.chiseled_bookshelf.ChiseledBookshelfColors;
import io.github.mortuusars.scholar.client.resource.BuiltInResourcePacks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Scholar.ID, value = Dist.CLIENT)
public class NeoForgeClientEvents {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ScholarClient::init);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
        Block[] bookshelves = BuiltInRegistries.BLOCK.stream()
              .filter(block -> block instanceof ChiseledBookShelfBlock)
              .toArray(Block[]::new);
        event.register(ChiseledBookshelfColors.slotTintSources(), bookshelves);
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(BookshelfDefaultColorsReloadListener.ID, new BookshelfDefaultColorsReloadListener());
        event.addListener(BookItemColorsReloadListener.ID, new BookItemColorsReloadListener());
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
                event.addPackFinders(
                      pack.id().withPrefix("resourcepacks/"),
                      PackType.CLIENT_RESOURCES,
                      pack.name(),
                      PackSource.FEATURE,
                      false,
                      Pack.Position.TOP);
            }
        });
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        InWorldTooltip.extract(event.getGuiGraphics(), event.getPartialTick());
    }
}
