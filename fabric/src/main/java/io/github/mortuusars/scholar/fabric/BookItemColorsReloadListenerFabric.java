package io.github.mortuusars.scholar.fabric;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.book.BookItemColorsReloadListener;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

public class BookItemColorsReloadListenerFabric extends BookItemColorsReloadListener implements IdentifiableResourceReloadListener {
    public static final ResourceLocation ID = Scholar.resource("bookshelf_item_colors_reload_listener");
    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }
}
