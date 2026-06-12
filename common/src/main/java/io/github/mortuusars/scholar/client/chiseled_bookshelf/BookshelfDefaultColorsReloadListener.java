package io.github.mortuusars.scholar.client.chiseled_bookshelf;

import com.mojang.serialization.Codec;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BookshelfDefaultColorsReloadListener extends SimpleJsonResourceReloadListener<Map<Identifier, BookshelfDefaultColors>> {
    public static final Identifier ID = Scholar.identifier("bookshelf_default_colors");

    public BookshelfDefaultColorsReloadListener() {
        super(Codec.unboundedMap(Identifier.CODEC, BookshelfDefaultColors.CODEC),
              FileToIdConverter.json("chiseled_bookshelf/default_colors"));
    }

    @Override
    protected void apply(Map<Identifier, Map<Identifier, BookshelfDefaultColors>> map,
                         @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<Identifier, BookshelfDefaultColors> result = new HashMap<>();

        for (Map<Identifier, BookshelfDefaultColors> value : map.values()) {
            result.putAll(value);
        }

        ChiseledBookshelfColors.DEFAULT_SLOT_COLORS = result;
    }
}