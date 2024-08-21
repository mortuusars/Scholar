package io.github.mortuusars.scholar.fabric;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.render.ChiseledBookShelfOverlay;
import io.github.mortuusars.scholar.client.screen.LecternSpreadScreen;
import io.github.mortuusars.scholar.visual.BookColor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.Items;

public class ScholarFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register(BookColor::getTintColor, Items.WRITABLE_BOOK);
        ColorProviderRegistry.ITEM.register(BookColor::getTintColor, Items.WRITTEN_BOOK);
        MenuScreens.register(Scholar.MenuTypes.LECTERN.get(), LecternSpreadScreen::new);
        HudRenderCallback.EVENT.register(ChiseledBookShelfOverlay::render);
    }
}
