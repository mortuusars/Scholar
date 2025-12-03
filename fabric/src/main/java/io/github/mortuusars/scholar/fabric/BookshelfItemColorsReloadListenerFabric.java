package io.github.mortuusars.scholar.fabric;

import io.github.mortuusars.scholar.client.chiseled_bookshelf.BookshelfItemColorsReloadListener;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

public class BookshelfItemColorsReloadListenerFabric extends BookshelfItemColorsReloadListener implements IdentifiableResourceReloadListener {
    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }
}
