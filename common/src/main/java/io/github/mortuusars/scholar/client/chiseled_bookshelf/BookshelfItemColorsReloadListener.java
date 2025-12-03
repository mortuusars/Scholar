package io.github.mortuusars.scholar.client.chiseled_bookshelf;

import com.mojang.serialization.Codec;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.util.HexColor;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class BookshelfItemColorsReloadListener extends SimpleJsonResourceReloadListener<Map<ResourceLocation, Integer>> {
    public static final ResourceLocation ID = Scholar.resource("bookshelf_item_colors_reload_listener");

    public BookshelfItemColorsReloadListener() {
        super(Codec.unboundedMap(ResourceLocation.CODEC, HexColor.CODEC), FileToIdConverter.json("chiseled_bookshelf/item_colors"));
    }

    @Override
    protected void apply(Map<ResourceLocation, Map<ResourceLocation, Integer>> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, Integer> result = new HashMap<>();

        for (Map<ResourceLocation, Integer> value : map.values()) {
            result.putAll(value);
        }

        ChiseledBookshelfColors.ITEM_COLORS = result;
    }
}