package io.github.mortuusars.scholar.fabric;

import io.github.mortuusars.scholar.book.BookItemColorsReloadListener;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.Identifier;

public class BookItemColorsReloadListenerFabric extends BookItemColorsReloadListener implements IdentifiableResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return ID;
    }
}
