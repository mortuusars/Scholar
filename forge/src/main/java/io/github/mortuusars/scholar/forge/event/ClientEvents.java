package io.github.mortuusars.scholar.forge.event;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.ScholarClient;
import io.github.mortuusars.scholar.client.render.ChiseledBookShelfOverlay;
import io.github.mortuusars.scholar.client.screen.LecternSpreadScreen;
import io.github.mortuusars.scholar.visual.BookColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
                MenuScreens.register(Scholar.MenuTypes.LECTERN.get(), LecternSpreadScreen::new);
            });
        }

        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.register(BookColor::getTintColor, Items.WRITABLE_BOOK);
            event.register(BookColor::getTintColor, Items.WRITTEN_BOOK);
        }
    }

    @Mod.EventBusSubscriber(modid = Scholar.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeBus {
        @SubscribeEvent
        public static void onRenderGuiPost(RenderGuiEvent.Post event) {
            ChiseledBookShelfOverlay.render(event.getGuiGraphics(), event.getPartialTick());
        }
    }
}
