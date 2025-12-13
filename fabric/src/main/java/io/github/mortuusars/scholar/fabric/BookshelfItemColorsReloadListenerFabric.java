package io.github.mortuusars.scholar.fabric;

import io.github.mortuusars.scholar.client.chiseled_bookshelf.BookshelfItemColorsReloadListener;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.Identifier;

public class BookshelfItemColorsReloadListenerFabric extends BookshelfItemColorsReloadListener implements IdentifiableResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return ID;
    }
}
