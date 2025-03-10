package io.github.mortuusars.scholar.fabric;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.ScholarClient;
import io.github.mortuusars.scholar.client.render.ChiseledBookShelfOverlay;
import io.github.mortuusars.scholar.client.screen.LecternSpreadBookEditScreen;
import io.github.mortuusars.scholar.client.screen.LecternSpreadBookViewScreen;
import io.github.mortuusars.scholar.book.BookColor;
import io.github.mortuusars.scholar.network.fabric.PacketsImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.Items;

public class ScholarFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register(BookColor::getTintColor, Items.WRITABLE_BOOK);
        ColorProviderRegistry.ITEM.register(BookColor::getTintColor, Items.WRITTEN_BOOK);

        ScholarClient.KeyMappings.register(KeyBindingHelper::registerKeyBinding);

        MenuScreens.register(Scholar.MenuTypes.LECTERN_SPREAD_BOOK_VIEW.get(), LecternSpreadBookViewScreen::new);
        MenuScreens.register(Scholar.MenuTypes.LECTERN_SPREAD_BOOK_EDIT.get(), LecternSpreadBookEditScreen::new);

        HudRenderCallback.EVENT.register(ChiseledBookShelfOverlay::render);

        PacketsImpl.registerS2CPackets();
    }
}
