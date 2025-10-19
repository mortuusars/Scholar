package io.github.mortuusars.scholar.forge.event;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.ScholarClient;
import io.github.mortuusars.scholar.book.BookColor;
import io.github.mortuusars.scholar.client.chiseled_bookshelf.BookshelfDefaultColorsReloadListener;
import io.github.mortuusars.scholar.client.chiseled_bookshelf.BookshelfItemColorsReloadListener;
import io.github.mortuusars.scholar.client.chiseled_bookshelf.ChiseledBookshelfColors;
import io.github.mortuusars.scholar.client.chiseled_bookshelf.ChiseledBookshelfTooltip;
import io.github.mortuusars.scholar.client.gui.screen.edit.LecternSpreadBookEditScreen;
import io.github.mortuusars.scholar.client.gui.screen.view.LecternSpreadBookViewScreen;
import io.github.mortuusars.scholar.client.resource.BuiltInResourcePacks;
import io.github.mortuusars.scholar.forge.resource.ModFilePackResources;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("unused")
public class ClientEvents {
    @Mod.EventBusSubscriber(modid = Scholar.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ScholarClient.init();
                MenuScreens.register(Scholar.MenuTypes.LECTERN_SPREAD_BOOK_VIEW.get(), LecternSpreadBookViewScreen::new);
                MenuScreens.register(Scholar.MenuTypes.LECTERN_SPREAD_BOOK_EDIT.get(), LecternSpreadBookEditScreen::new);

                //noinspection deprecation
                ChiseledBookshelfColors.setBookshelfRenderLayer(ItemBlockRenderTypes::setRenderLayer);
            });
        }

        @SubscribeEvent
        public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
            ChiseledBookshelfColors.registerBookshelfBlockColors(event::register);
        }

        @SubscribeEvent
        public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(new BookshelfDefaultColorsReloadListener());
            event.registerReloadListener(new BookshelfItemColorsReloadListener());
        }

        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.register(BookColor::getItemTintColor, Items.WRITABLE_BOOK);
            event.register(BookColor::getItemTintColor, Items.WRITTEN_BOOK);
        }

        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            ScholarClient.KeyMappings.register(event::register);
        }

        @SubscribeEvent
        public static void addPacks(AddPackFindersEvent event) {
            if (event.getPackType() != PackType.CLIENT_RESOURCES) return;

            ModList.get().getModContainerById(Scholar.ID).ifPresent(modContainer -> {
                event.addRepositorySource((packConsumer) -> {
                    for (BuiltInResourcePacks.Pack pack : BuiltInResourcePacks.get()) {
                        Pack createdPack = Pack.readMetaAndCreate(
                                pack.id().toString(),
                                pack.name(),
                                pack.activation().forge() == BuiltInResourcePacks.ActivationType.ALWAYS_ENABLED,
                                (id) -> new ModFilePackResources(
                                        id,
                                        modContainer.getModInfo().getOwningFile().getFile(),
                                        "resourcepacks/" + pack.id().getPath(),
                                        true
                                ),
                                PackType.CLIENT_RESOURCES,
                                Pack.Position.TOP,
                                PackSource.BUILT_IN);

                        packConsumer.accept(createdPack);
                    }
                });
            });
        }
    }

    @Mod.EventBusSubscriber(modid = Scholar.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeBus {
        @SubscribeEvent
        public static void onRenderGuiPost(RenderGuiEvent.Post event) {
            ChiseledBookshelfTooltip.renderSlotTooltip(event.getGuiGraphics(), event.getPartialTick());
        }
    }
}
