package io.github.mortuusars.scholar.client.chiseled_bookshelf;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import io.github.mortuusars.scholar.client.util.HexColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class BookshelfItemColorsReloadListener extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<Map<ResourceLocation, Integer>> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, HexColor.CODEC);

    public BookshelfItemColorsReloadListener() {
        super(new Gson(), "chiseled_bookshelf/item_colors");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, Integer> itemColors = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation location = entry.getKey();
            if (!location.getNamespace().equals("scholar")) {
                LOGGER.info("Ignoring chiseled bookshelf item color definition '{}' because it's not in Scholar namespace.", location);
                continue;
            }

            CODEC.decode(JsonOps.INSTANCE, entry.getValue())
                  .ifError(err -> LOGGER.error("Contents of '{}' cannot be parsed as a valid id to color map: {}", location, err))
                  .ifSuccess(data -> itemColors.putAll(data.getFirst()));
        }

        ChiseledBookshelfColors.ITEM_COLORS = ImmutableMap.copyOf(itemColors);
    }
}