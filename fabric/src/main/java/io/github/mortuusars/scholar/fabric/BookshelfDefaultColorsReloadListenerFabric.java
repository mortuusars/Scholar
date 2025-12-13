package io.github.mortuusars.scholar.fabric;

import io.github.mortuusars.scholar.client.chiseled_bookshelf.BookshelfDefaultColorsReloadListener;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.Identifier;

public class BookshelfDefaultColorsReloadListenerFabric extends BookshelfDefaultColorsReloadListener implements IdentifiableResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return ID;
    }
}
