package io.github.mortuusars.scholar.fabric;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.render.ChiseledBookShelfOverlay;
import io.github.mortuusars.scholar.client.screen.LecternSpreadScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screens.MenuScreens;

public class ScholarFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(Scholar.MenuTypes.LECTERN.get(), LecternSpreadScreen::new);

        HudRenderCallback.EVENT.register(ChiseledBookShelfOverlay::render);

//        ClientPlayNetworking.registerGlobalReceiver(RequestChiseledBookShelfUpdateC2SP.ID, (client, handler, buf, responseSender) -> {

//        })

    }
}
