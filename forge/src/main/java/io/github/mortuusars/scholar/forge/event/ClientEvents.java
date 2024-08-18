package io.github.mortuusars.scholar.forge.event;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.render.ChiseledBookShelfOverlay;
import io.github.mortuusars.scholar.screen.LecternSpreadScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents {
    public static class ModBus {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                MenuScreens.register(Scholar.MenuTypes.LECTERN.get(), LecternSpreadScreen::new);
            });
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Scholar.ID, value = Dist.CLIENT)
    public static class ForgeBus {
        @SubscribeEvent
        public static void onRenderGuiPost(RenderGuiEvent.Post event) {
            ChiseledBookShelfOverlay.render(event.getGuiGraphics(), event.getPartialTick());
        }
    }
}
