package io.github.mortuusars.scholar.book;

import com.mojang.serialization.Codec;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.util.HexColor;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class BookItemColorsReloadListener extends SimpleJsonResourceReloadListener<Map<Identifier, Integer>> {
    public static final Identifier ID = Scholar.resource("book_item_colors_reload_listener");

    public BookItemColorsReloadListener() {
        super(Codec.unboundedMap(Identifier.CODEC, HexColor.CODEC), FileToIdConverter.json("book/item_colors"));
    }

    @Override
    protected void apply(Map<Identifier, Map<Identifier, Integer>> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, Integer> result = new HashMap<>();

        for (Map<Identifier, Integer> value : map.values()) {
            result.putAll(value);
        }

        BookColor.ITEM_COLORS = ImmutableMap.copyOf(itemColors);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, Integer> itemColors = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation location = entry.getKey();
            if (!location.getNamespace().equals("scholar")) {
                LOGGER.info("Ignoring book item color definition '{}' because it's not in Scholar namespace.", location);
                continue;
            }

            CODEC.decode(JsonOps.INSTANCE, entry.getValue())
                  .ifError(err -> LOGGER.error("Contents of '{}' cannot be parsed as a valid id to color map: {}", location, err))
                  .ifSuccess(data -> itemColors.putAll(data.getFirst()));
        }

        BookColor.ITEM_COLORS = ImmutableMap.copyOf(itemColors);
    }
}