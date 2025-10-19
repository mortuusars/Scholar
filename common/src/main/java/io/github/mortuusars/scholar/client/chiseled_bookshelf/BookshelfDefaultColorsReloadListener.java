package io.github.mortuusars.scholar.client.chiseled_bookshelf;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class BookshelfDefaultColorsReloadListener extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<Map<ResourceLocation, BookshelfDefaultColors>> CODEC =
          Codec.unboundedMap(ResourceLocation.CODEC, BookshelfDefaultColors.CODEC);

    public BookshelfDefaultColorsReloadListener() {
        super(new Gson(), "chiseled_bookshelf/default_colors");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, BookshelfDefaultColors> bookshelfColors = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation location = entry.getKey();
            if (!location.getNamespace().equals("scholar")) {
                LOGGER.info("Ignoring chiseled bookshelf default color definition '{}' because it's not in Scholar namespace.", location);
                continue;
            }

            CODEC.decode(JsonOps.INSTANCE, entry.getValue())
                  .resultOrPartial(err -> LOGGER.error("Contents of '{}' cannot be parsed as a valid id to color list map: {}", location, err))
                  .ifPresent(data -> bookshelfColors.putAll(data.getFirst()));
        }

        ChiseledBookshelfColors.DEFAULT_SLOT_COLORS = ImmutableMap.copyOf(bookshelfColors);
    }
}