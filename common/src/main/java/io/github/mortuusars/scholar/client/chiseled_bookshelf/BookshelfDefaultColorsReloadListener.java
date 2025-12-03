package io.github.mortuusars.scholar.client.chiseled_bookshelf;

import com.mojang.serialization.Codec;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class BookshelfDefaultColorsReloadListener extends SimpleJsonResourceReloadListener<Map<ResourceLocation, BookshelfDefaultColors>> {
    public static final ResourceLocation ID = Scholar.resource("bookshelf_default_colors");

    public BookshelfDefaultColorsReloadListener() {
        super(Codec.unboundedMap(ResourceLocation.CODEC, BookshelfDefaultColors.CODEC),
              FileToIdConverter.json("chiseled_bookshelf/default_colors"));
    }

    @Override
    protected void apply(Map<ResourceLocation, Map<ResourceLocation, BookshelfDefaultColors>> map,
                         ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, BookshelfDefaultColors> result = new HashMap<>();

        for (Map<ResourceLocation, BookshelfDefaultColors> value : map.values()) {
            result.putAll(value);
        }

        ChiseledBookshelfColors.DEFAULT_SLOT_COLORS = result;
    }
}