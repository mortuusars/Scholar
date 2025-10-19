package io.github.mortuusars.scholar.fabric;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.chiseled_bookshelf.BookshelfDefaultColorsReloadListener;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

public class BookshelfDefaultColorsReloadListenerFabric extends BookshelfDefaultColorsReloadListener implements IdentifiableResourceReloadListener {
    public static final ResourceLocation ID = Scholar.resource("bookshelf_default_colors_reload_listener");
    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }
}
